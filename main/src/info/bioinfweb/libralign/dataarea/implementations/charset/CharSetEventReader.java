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
import info.bioinfweb.jphyloio.events.CharacterSetEvent;
import info.bioinfweb.jphyloio.events.EventType;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.libralign.io.AbstractDataModelEventReader;



/**
 * Reads {@link CharacterSetEvent}s from JPhyloIO into an instance of {@link CharSetDataModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class CharSetEventReader extends AbstractDataModelEventReader {
	private UniqueColorLister colorLister = new UniqueColorLister();
	
	
	public CharSetEventReader(CharSetDataModel model) {
		super(model);
	}


	@Override
	public CharSetDataModel getModel() {
		return (CharSetDataModel)super.getModel();
	}

	
	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) {
		if (event.getEventType().equals(EventType.CHARACTER_SET)) {
			CharacterSetEvent characterSetEvent = event.asCharacterSetEvent();
			
			CharSet charSet = getModel().getByName(characterSetEvent.getName());
			if (charSet == null) {
				charSet = new CharSet(characterSetEvent.getName(), colorLister.generateNext());
				getModel().add(charSet);
			}
			
			charSet.add((int)characterSetEvent.getStart(), (int)characterSetEvent.getEnd() - 1);  //TODO Refactor NonOverlappingIntervalList so that end index is also behind the interval.
		}
	}
}
