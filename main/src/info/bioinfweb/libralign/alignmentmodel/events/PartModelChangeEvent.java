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
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel;
import info.bioinfweb.libralign.alignmentmodel.DefaultConcatenatedAlignmentModel;



/**
 * Event object that indicates the insertion or removal of an alignment part (an instance of 
 * {@link AlignmentPartModel} as part of an {@link DefaultConcatenatedAlignmentModel}). 
 * <p>
 * An alignment part would be a subsequent set of columns of an alignment displayed in an
 * {@link AlignmentArea} with its own token set.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing part model object works with
 */
public class PartModelChangeEvent<T> extends AbstractPartModelEvent<T> {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param partModel the part model that was inserted or removed 
	 * @param type the type of change ({@link ListChangeType#REPLACEMENT} is not valid here)
	 * @param index
	 */
	protected PartModelChangeEvent(AlignmentPartModel<T> partModel, ListChangeType type, int index) {
		super(partModel, -1, type, index);
	}
	
	
	/**
	 * Creates a new instance of this class indicating the insertion of a new part model in an instance
	 * of {@link DefaultConcatenatedAlignmentModel}.
	 * 
	 * @param partModel the part model that was inserted
	 * @param index the index the new part model has
	 * @return the new instance
	 */
	public static <T> PartModelChangeEvent<T> newInsertInstance(AlignmentPartModel<T> partModel, int index) {
		return new PartModelChangeEvent<T>(partModel, ListChangeType.INSERTION, index);
	}
	
	
	/**
	 * Creates a new instance of this class indicating the removal of a part model from an instance
	 * of {@link DefaultConcatenatedAlignmentModel}.
	 * 
	 * @param partModel the part model that was removed
	 * @param index the index the part model has before it was removed
	 * @return the new instance
	 */
	public static <T> PartModelChangeEvent<T> newRemoveInstance(AlignmentPartModel<T> partModel, int index) {
		return new PartModelChangeEvent<T>(partModel, ListChangeType.DELETION, index);
	}
	
	
	/**
	 * Returns the index where the new part model has been inserted or the the index from where it has been removed.
	 */
	public int getIndex() {
		return super.getIndex();
	}
}
