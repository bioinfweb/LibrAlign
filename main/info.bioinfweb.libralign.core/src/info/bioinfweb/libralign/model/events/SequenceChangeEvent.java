/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Event object that indicates that a sequence provided by an instance of {@link AlignmentModel}
 * was added or removed.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class SequenceChangeEvent<T> extends AlignmentModelChangeEvent<T> {
	private ListChangeType type;

	
	protected SequenceChangeEvent(AlignmentModel<T> source, String sequenceID, ListChangeType type) {
		super(source, sequenceID);
		this.type = type;
	}
	
	
	public static <T> SequenceChangeEvent<T> newInsertInstance(AlignmentModel<T> source, String sequenceID) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.INSERTION);
	}
	
	
	public static <T> SequenceChangeEvent<T> newRemoveInstance(AlignmentModel<T> source, String sequenceID) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.DELETION);
	}


	public ListChangeType getType() {
		return type;
	}


	@Override
	public SequenceChangeEvent<T> cloneWithNewSource(AlignmentModel<T> source) {
		return (SequenceChangeEvent<T>)super.cloneWithNewSource(source);
	}


	@Override
	public SequenceChangeEvent<T> clone() {
		return (SequenceChangeEvent<T>)super.clone();
	}
}
