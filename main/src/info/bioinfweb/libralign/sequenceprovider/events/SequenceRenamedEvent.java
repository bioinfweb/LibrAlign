/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Event object that indicates that a sequence provided by an instance of {@link SequenceDataProvider}
 * was renamed.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceRenamedEvent<T> extends SequenceDataProviderChangeEvent<T> {
	private String previousName;
	private String newName;
	
	
	public SequenceRenamedEvent(SequenceDataProvider<T> source, int sequenceID,
			String previousName, String newName) {
		
		super(source, sequenceID);
		this.previousName = previousName;
		this.newName = newName;
	}


	public String getPreviousName() {
		return previousName;
	}


	public String getNewName() {
		return newName;
	}
}
