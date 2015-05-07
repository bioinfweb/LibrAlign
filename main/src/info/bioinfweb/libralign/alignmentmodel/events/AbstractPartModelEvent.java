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
package info.bioinfweb.libralign.alignmentmodel.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel;



/**
 * Events dealing with instances of {@link AlignmentPartModel} can be inherited from this class.
 * It implements the common properties 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public abstract class AbstractPartModelEvent<T> extends SequenceChangeEvent {
	private AlignmentPartModel<T> partModel;
	private int index;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param partModel the affected part model
	 * @param sequenceID the ID of the affected sequence
	 * @param type the type of list change
	 * @param index the index associated with this event
	 */
	public AbstractPartModelEvent(AlignmentPartModel<T> partModel, int sequenceID, ListChangeType type, int index) {
		super(partModel.getOwner(), sequenceID, type);
		this.partModel = partModel;
		this.index = index;
	}


	/**
	 * Returns the affected part model.
	 * <p>
	 * {@link #getSource()} returns the concatenated alignment model which contains the part model returned here. 
	 * 
	 * @return the part model which was the source of the event
	 */
	public AlignmentPartModel<T> getPartModel() {
		return partModel;
	}


	/**
	 * Returns the index associated with this event. Inherited classes may use this field for different purposes.
	 * 
	 * @return the stored index
	 */
	protected int getIndex() {
		return index;
	}
}
