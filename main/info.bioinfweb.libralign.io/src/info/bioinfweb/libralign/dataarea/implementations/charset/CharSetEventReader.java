/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
import java.util.HashMap;
import java.util.Map;

import info.bioinfweb.commons.graphics.UniqueColorLister;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;
import info.bioinfweb.libralign.model.data.NoArgDataModelFactory;
import info.bioinfweb.libralign.model.io.AbstractDataModelEventReader;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.DataModelKey;



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
public class CharSetEventReader extends AbstractDataModelEventReader<CharSetDataModel, CharSetDataModelListener> {
	private boolean publishOnAlignmentEnd = false;
	private LinkedLabeledIDEvent currentStartEvent = null;
	private String currentAlignmentID = null;
	private Map<String, CharSet> loadedCharacterSets = new HashMap<String, CharSet>();
	
	private URIOrStringIdentifier colorPredicate;
	private boolean isReadingColor;
	private Color currentColor = null;
	private UniqueColorLister colorLister = new UniqueColorLister();
	
	
	public CharSetEventReader(AlignmentDataReader mainReader, URIOrStringIdentifier colorPredicate) {
		super(mainReader, new NoArgDataModelFactory<CharSetDataModel, CharSetDataModelListener>(CharSetDataModel.class));
		this.colorPredicate = colorPredicate;
	}


	public CharSetEventReader(AlignmentDataReader mainReader) {
		this(mainReader, null);
	}
	
	
	/**
	 * Determines whether loaded models should be published when reading their associated alignment is finished or not. By
	 * default all character set models will be published when the end of the document is reached. If access to character 
	 * sets is needed directly after having loaded each alignment model, this property should be set to {@code true} 
	 * instead.
	 * <p>
	 * Note that this will work, e.g., for <i>NeXML</i> but will not have a benefit for formats that define character sets 
	 * after alignments, such as <i>Nexus</i>. If character sets are encountered after their associated alignment, these
	 * will be published at the end of the file, no matter if this property is set or not.
	 * 
	 * @return {@code true} if models are published as the end of their associated alignment was read, or {@code false}
	 *         if models are all published when the end of the file is reached. 
	 */
	public boolean isPublishOnAlignmentEnd() {
		return publishOnAlignmentEnd;
	}


	public void setPublishOnAlignmentEnd(boolean publishOnAlignmentEnd) {
		this.publishOnAlignmentEnd = publishOnAlignmentEnd;
	}

	
	private CharSet getCurrentCharSet() {
		if (currentStartEvent != null) {
			// Determine model to write to:
			CharSetDataModel model;
			DataModelKey key;
			if (currentStartEvent.hasLink()) {
				key = new DataModelKey(currentStartEvent.getLinkedID());  // If the linked alignment model ID references a not (yet) existing alignment model, the returned model would also be null.
			}
			else {
				key = new DataModelKey(null);
			}
			model = getLoadingModels().get(key);
			if (model == null) {
				model = getFactory().createNewModel();
				getLoadingModels().put(key, model);
			}
			
			// Read data:
			CharSet result = model.get(currentStartEvent.getID());
			if (result == null) {
				if (currentColor == null) {
					currentColor = colorLister.generateNext();
				}
				result = new CharSet(currentStartEvent.getLabel(), currentColor);  //TODO Create default name if label is null?
				model.put(currentStartEvent.getID(), result);
				loadedCharacterSets.put(currentStartEvent.getID(), result);
			}
			return result;
		}
		else {
			return null;
		}
	}
	

	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) {
		switch (event.getType().getContentType()) {
			case ALIGNMENT:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					currentAlignmentID = event.asLabeledIDEvent().getID();
				}
				else if (publishOnAlignmentEnd) {
					if (currentAlignmentID != null) {
						DataModelKey key = new DataModelKey(currentAlignmentID);
						CharSetDataModel model = getLoadingModels().remove(key);
						if (model != null) {
							getCompletedModels().put(key, model);
						}
					}
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
						CharSet charSet = getCurrentCharSet();
						currentColor = (Color)value;
						if (charSet != null) {
							charSet.setColor(currentColor);
						}
					}
					else {
						//TODO Log warning?
					}
				}
				break;
			case CHARACTER_SET_INTERVAL:
				CharacterSetIntervalEvent intervalEvent = event.asCharacterSetIntervalEvent();
				CharSet charSet = getCurrentCharSet();
				if (charSet != null) {  // Interval events may also occur within a token set definition.
					charSet.add((int)intervalEvent.getStart(), (int)intervalEvent.getEnd() - 1);  //TODO Refactor NonOverlappingIntervalList so that end index is also behind the interval.
				}
				break;
			case SET_ELEMENT:
				SetElementEvent setEvent = event.asSetElementEvent();
				if (setEvent.getLinkedObjectType().equals(EventContentType.CHARACTER_SET)) {  //TODO Can anything else happen?
					CharSet linkedCharSet = loadedCharacterSets.get(setEvent.getLinkedID());
					if (linkedCharSet != null) {
						getCurrentCharSet().addAll(linkedCharSet);
					}
					else {
						//TODO Log warning?
					}
				}
				break;
			case DOCUMENT:
				if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
					getCompletedModels().putAll(getLoadingModels());
					getLoadingModels().clear();
					loadedCharacterSets.clear();
				}
				break;
			default:  // Nothing to do
				break;
		}
	}
}
