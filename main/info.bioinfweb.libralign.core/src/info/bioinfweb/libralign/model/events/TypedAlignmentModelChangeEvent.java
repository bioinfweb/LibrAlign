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
package info.bioinfweb.libralign.model.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;



public class TypedAlignmentModelChangeEvent<T> extends AlignmentModelChangeEvent<T> {
	private ListChangeType type;

	
	public TypedAlignmentModelChangeEvent(AlignmentModel<T> source, String sequenceID, ListChangeType type) {
		super(source, sequenceID);
		if (type == null) {
			throw new IllegalArgumentException("type must not be null.");
		}
		else {
			this.type = type;
		}
}

	
	/**
	 * Defines the type of change represented by this event.
	 * <p>
	 * For {@code SequenceChangeEvent}s only the types {@link ListChangeType#INSERTION} and {@link ListChangeType#DELETION}
	 * may occur. Additional types may be valid in inherited classes.
	 * 
	 * @return the change type represented by this event
	 */
	public ListChangeType getType() {
		return type;
	}

	
	@Override
	public TypedAlignmentModelChangeEvent<T> cloneWithNewSource(AlignmentModel<T> source) {
		return (TypedAlignmentModelChangeEvent<T>)super.cloneWithNewSource(source);
	}

	
	@Override
	public TypedAlignmentModelChangeEvent<T> clone() {
		return (TypedAlignmentModelChangeEvent<T>)super.clone();
	}
}