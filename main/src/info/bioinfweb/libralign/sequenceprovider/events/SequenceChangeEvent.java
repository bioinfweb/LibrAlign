/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.sequenceprovider.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Event object that indicates that a sequence provided by an instance of {@link SequenceDataProvider}
 * was renamed.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class SequenceChangeEvent extends SequenceDataProviderChangeEvent {
	private ListChangeType type;

	
	public SequenceChangeEvent(SequenceDataProvider source, int sequenceID, ListChangeType type) {
		super(source, sequenceID);
		this.type = type;
	}


	public ListChangeType getType() {
		return type;
	}
}