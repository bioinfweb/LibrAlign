/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.charset;


import info.bioinfweb.commons.graphics.UniqueColorLister;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.libralign.model.data.NoArgDataModelFactory;
import info.bioinfweb.libralign.model.io.AbstractDataModelEventReader;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.DataModelReadInfo;



/**
 * Reads {@link CharacterSetEvent}s from JPhyloIO into instances of {@link CharSetDataModel}.
 * <p>
 * {@link CharacterSetEvent}s between alignment start and end events are stored as associated
 * with the according alignment model. Events fired outside alignments are stored in an additional
 * global model not associated with any alignment model. This global model is returned when the end
 * of the event stream was reached (if any according events occurred).
 * <p>
 * For parsing Nexus files that would mean that all character sets defined in {@code SETS} blocks
 * are stored in the global model. Additionally the supported MrBayes {@code MIXED} data type 
 * extension may lead to character set events within alignments. These are than stored in separate
 * models, because different sets may be defined for each alignment block in the file. If this
 * MrBayes extension is not used all character sets (also from separate {@code SETS} blocks
 * will be stored in one global model, which will be the only returned instance.
 *
 * @bioinfweb.module info.bioinfweb.libralign.io
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class CharSetEventReader extends AbstractDataModelEventReader<CharSetDataModel> {
	//TODO If models are stored associated or globally should not be determined by the position of their events in the stream, 
	//     but by their linked matrix.
	//TODO Multiple global models should be possible, if start events with different IDs that do not link a matrix are encountered. 
	
	private CharSetDataModel globalModel = null;
	private LinkedLabeledIDEvent currentStartEvent = null;
	private UniqueColorLister colorLister = new UniqueColorLister();
	
	
	public CharSetEventReader(AlignmentDataReader mainReader) {
		super(mainReader, new NoArgDataModelFactory<CharSetDataModel>(CharSetDataModel.class));
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
				}
				else {
					currentStartEvent = null;
				}
				break;
			case CHARACTER_SET_INTERVAL:
				// Determine model to write to:
				CharSetDataModel model;
				if (getMainReader().getAlignmentModelReader().hasCurrentModel()) {
					if (!super.isReadingInstance()) {
						createNewInfo(getMainReader().getAlignmentModelReader().getCurrentModel());
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
					charSet = new CharSet(currentStartEvent.getLabel(), colorLister.generateNext());  //TODO Create default name if label is null?
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
