/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.awt.Color;

import info.bioinfweb.commons.graphics.UniqueColorLister;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;
import info.bioinfweb.libralign.model.data.NoArgDataModelFactory;
import info.bioinfweb.libralign.model.io.AbstractDataModelEventReader;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.DataModelReadInfo;



/**
 * Reads {@link CharacterSetEvent}s from <i>JPhyloIO</i> into instances of {@link CharSetDataModel}.
 * <p>
 * {@link CharacterSetEvent}s between alignment start and end events are stored as associated
 * with the according alignment model. Events fired outside alignments are stored in an additional
 * global model not associated with any alignment model. This global model is returned when the end
 * of the event stream was reached (if any according events occurred).
 * <p>
 * For parsing <i>Nexus</i> files that would mean that all character sets defined in {@code SETS} 
 * blocks are stored in the global model. Additionally the supported <i>MrBayes</i> {@code MIXED} 
 * data type extension may lead to character set events within alignments. These are than stored in 
 * separate models, because different sets may be defined for each alignment block in the file. If 
 * this <i>MrBayes</i> extension is not used all character sets (also from separate {@code SETS} 
 * blocks will be stored in one global model, which will be the only returned instance.
 * <p>
 * This reader is able to read metadata that specifies the color associated with a character set.
 * Note that an {@link ObjectTranslator} for the respective data type that creates {@link Color}
 * objects must be used. Otherwise the metadata will be ignored. Colors will be generated for sets
 * for which no metadata could be read. 
 *
 * @bioinfweb.module info.bioinfweb.libralign.io
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class CharSetEventReader extends AbstractDataModelEventReader<CharSetDataModel> {
	//TODO If models are stored associated or globally should not be determined by the position of their events in the stream, 
	//     but by their linked matrix. This way SETS blocks from Nexus may also be interpreted correctly, if they use LINK commands.
	//TODO Multiple global models should be possible, if start events with different IDs that do not link a matrix are encountered.
	//TODO Read color metadata as the data adapter writes them.
	
	private CharSetDataModel globalModel = null;
	private LinkedLabeledIDEvent currentStartEvent = null;
	
	private URIOrStringIdentifier colorPredicate;
	private boolean isReadingColor;
	private Color currentColor = null;
	private UniqueColorLister colorLister = new UniqueColorLister();
	
	
	public CharSetEventReader(AlignmentDataReader mainReader, URIOrStringIdentifier colorPredicate) {
		super(mainReader, new NoArgDataModelFactory<CharSetDataModel>(CharSetDataModel.class));
		this.colorPredicate = colorPredicate;
	}


	public CharSetEventReader(AlignmentDataReader mainReader) {
		this(mainReader, null);
	}


	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) {
		switch (event.getType().getContentType()) {
			case ALIGNMENT:
				if (event.getType().getTopologyType().equals(EventTopologyType.END)) {  //TODO Does this work if character sets are specified after the alignment (e.g. in Nexus)? When is the global model published?
					publishCurrentInfo();  // Adds alignment specific model to the result list, if one is present.
				}
				break;
			case CHARACTER_SET:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					currentStartEvent = event.asLinkedLabeledIDEvent();
					currentColor = null;
				}
				else {
					currentStartEvent = null;
				}
				break;
			case LITERAL_META:
				isReadingColor = false;
				if (event.getType().getTopologyType().equals(EventTopologyType.START) && 
						event.asLiteralMetadataEvent().getPredicate().equalsStringOrURI(colorPredicate)) {
						// Will also return false if a respective event is found and colorPredicate is null.
					
					isReadingColor = true;
				}
				
				break;
			case LITERAL_META_CONTENT:
				if (isReadingColor) {
					Object value = event.asLiteralMetadataContentEvent().getObjectValue();
					if (value instanceof Color) {
						currentColor = (Color)value;
					}
					else {
						//TODO Log warning?
					}
				}
				break;
			case CHARACTER_SET_INTERVAL:
				// Determine model to write to:
				CharSetDataModel model;
				if (getMainReader().getAlignmentModelReader().hasCurrentModel()) {  //TODO The linked ID is relevant, not the position.
					if (!super.isReadingInstance()) {
						createNewInfo(getMainReader().getAlignmentModelReader().getCurrentModel());  //TODO Publishing here is not possible, since character set definitions may be continued (e.g. in interleaved MEGA).
					}
					model = getCurrentInfo().getDataModel();
				}
				else {  // Use gobal character set model
					if (globalModel == null) {
						globalModel = getFactory().createNewModel();
					}
					model = globalModel;
				}
				
				// Read data:
				CharacterSetIntervalEvent intervalEvent = event.asCharacterSetIntervalEvent();
				CharSet charSet = model.get(currentStartEvent.getID());
				if (charSet == null) {
					if (currentColor == null) {
						currentColor = colorLister.generateNext();
					}
					charSet = new CharSet(currentStartEvent.getLabel(), currentColor);  //TODO Create default name if label is null?
					model.put(currentStartEvent.getID(), charSet);
				}
				charSet.add((int)intervalEvent.getStart(), (int)intervalEvent.getEnd() - 1);  //TODO Refactor NonOverlappingIntervalList so that end index is also behind the interval.
				break;
			case DOCUMENT:
				if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
					getModels().add(new DataModelReadInfo<CharSetDataModel>(globalModel));
				}
				break;
			default:  // Nothing to do
				break;
		}
	}


	@Override
	public boolean isReadingInstance() {
		return super.isReadingInstance() || (globalModel != null);
	}
}
