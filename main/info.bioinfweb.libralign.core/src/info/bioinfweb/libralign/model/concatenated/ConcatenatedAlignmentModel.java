/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.concatenated;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * This interface allows to concatenate multiple instances of {@link SequenceDataProvider} implementations.
 * It extends {@link SequenceDataProvider} by additional methods allowing to add and remove underlying
 * models which provide the contents a sets of subsequent columns of the concatenated alignment.
 * <p>
 * Each contained model may provide a different token set valid for the part of the alignment it models. 
 * To allow combining any different token sets this interface does not have a generic token type but uses 
 * {@link Object} as the token type instead. Implementing classes should return a global token set 
 * containing all tokens if the different submodels with the according inherited method.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface ConcatenatedAlignmentModel extends AlignmentModel<Object> {
	/**
	 * Appends an additional part model at the end of this concatenated alignment.
	 * 
	 * @param model the model to be added
	 * @throws IllegalArgumentException if this instance is not the owner of {@code model} or the IDs of the
	 *         sequences contained in {@code model} do not match the sequence IDs of this instance
	 * @throws NullPointerException if {@code model} is {@code null}
	 */
	public void appendPartModel(AlignmentModel<?> model);

	/**
	 * Inserts an additional part model at the specified position.
	 * 
	 * @param modelIndex the index where the new model shall be inserted (The index in the list of models is
	 *        meant here, which is different from the global index the first alignment column of the new model
	 *        will have.) 
	 * @param model the new model to be inserted
	 * @throws IllegalArgumentException if this instance is not the owner of {@code model} or the IDs of the
	 *         sequences contained in {@code model} do not match the sequence IDs of this instance
	 * @throws NullPointerException if {@code model} is {@code null}
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 *         ({@code index < 0 || index > }{@link #getPartModelCount()}) 
	 */
	public void insertPartModel(int modelIndex, AlignmentModel<?> model);

	/**
	 * Removes the part model at the specified index from this concatenated alignment model.
	 * 
	 * @param index the index of the part model to be removed
	 * @return the part model that has been removed
	 * @throws IndexOutOfBoundsException if {@code index} is below 0 or >= {@link #getPartModelCount()}
	 */
	public AlignmentModel<?> removePartModel(int index);

	/**
	 * Returns the part model at the specified position.
	 * 
	 * @param index the index of the part model to be returned
	 * @return the part model at the specified position
	 * @throws IndexOutOfBoundsException if {@code index} is below 0 or >= {@link #getPartModelCount()}
	 */
	public AlignmentModel<?> getPartModel(int index);

	/**
	 * Returns the right most part model contained in this alignment.
	 * 
	 * @return the right most model or {@code null} if no part models are contained in this alignment
	 */
	public AlignmentModel<?> getLastPartModel();

	/**
	 * Returns the number of part models contained in this alignment.
	 * 
	 * @return the number of contained models
	 */
	public int getPartModelCount();

	/**
	 * Returns the index of the specified part model in this concatenated alignment model.
	 * 
	 * @param partModel the sought-after part model
	 * @return the index or -1 of the specified model is not part of this instance
	 */
	public int partModelIndex(AlignmentModel<?> partModel);
	
	/**
	 * Checks if the specified model is contained in this concatenated model.
	 * 
	 * @param partModel the sought-after part model
	 * @return {@code true} if the specified model was found, {@code false} otherwise
	 */
	public boolean containsPartModel(AlignmentModel<?> partModel);

	/**
	 * Returns the global index of the first column provided by the part model with the specified index.
	 * 
	 * @param partIndex the index of the part model
	 * @return an integer value >= 0
	 * @throws IndexOutOfBoundsException if {@code partIndex} is below zero or >= {@link #getPartModelCount()}
	 */
	public int columnOffsetForPartModel(int partIndex);

	/**
	 * Returns the global index of the first column provided by the specified part model.
	 * 
	 * @param partModel the part model that has the returned offset
	 * @return an integer value >= 0
	 * @throws IllegalArgumentException if {@code partModel} is not contained in this concatenated model
	 */
	public int columnOffsetForPartModel(AlignmentModel<?> partModel);

	/**
	 * Returns the part model currently responsible for providing the contents of the specified column.
	 * 
	 * @param columnIndex the global column index (The first column has the index 0.)
	 * @return the according part model
	 * @throws IndexOutOfBoundsException if {@code columnIndex} is below zero or greater than the index of the 
	 *         right most column in this model 
	 */
	public AlignmentModel<?> partModelByColumn(int columnIndex);
	
	/**
	 * Translates the sequence ID used in an underlying part model to the according sequence ID in this 
	 * concatenated model.
	 * 
	 * @param partID the ID used in the part model
	 * @param partIndex the index of the part model
	 * @return the according sequence ID in the parent model
	 * @throws IllegalArgumentException if the specified ID is not contained in the specified part model
	 * @throws IndexOutOfBoundsException if the index of the part model is not valid
	 */
	public int parentIDByPartID(int partID, int partIndex) throws IllegalArgumentException, IndexOutOfBoundsException;
	
	/**
	 * Translates the sequence ID used in this concatenated model to the according sequence ID in an 
	 * underlying part model.
	 * <p>
	 * Note that not all part models need to contain all sequences represented by the concatenated model.
	 * 
	 * @param parentID the ID used in this model
	 * @param partIndex the index of the part model
	 * @return the according sequence ID in the specified part model or -1 if the specified part model 
	 *         does not contain a sequence according to the parent ID (although other part models do)  
	 * @throws IllegalArgumentException if the specified ID is not contained in this model (i.e. no part model 
	 *         contains an according sequence)
	 * @throws IndexOutOfBoundsException if the index of the part model is not valid
	 */
	public int partIDByParentID(int parentID, int partIndex) throws IllegalArgumentException, IndexOutOfBoundsException;
}
