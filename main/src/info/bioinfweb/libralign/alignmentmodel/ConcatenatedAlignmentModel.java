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
package info.bioinfweb.libralign.alignmentmodel;


import info.bioinfweb.libralign.alignmentmodel.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.alignmentmodel.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.alignmentmodel.exception.SequenceNotFoundException;

import java.util.Collection;



/**
 * Interface to be implemented by all classes acting as an alignment model which concatenates
 * implementations of {@link AlignmentPartModel} to a single alignment.
 * <p>
 * All sequences stored are identified by an integer ID and not by their names (which are possibly displayed
 * to an application user), because these names might change during runtime. Subclasses must ensure that
 * these IDs are unique and greater or equal to zero and remain constant during runtime. Additionally IDs of 
 * deleted sequences should not be reused, because other objects might have stored references to the deleted 
 * sequence using its ID and therefore might not be able to determine that the sequence was deleted, if a 
 * new sequence with the same ID is present.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface ConcatenatedAlignmentModel {
	/**
	 * Appends an additional part model at the end of this concatenated alignment.
	 * 
	 * @param model the model to be added
	 * @throws IllegalArgumentException if this instance is not the owner of {@code model} or the IDs of the
	 *         sequences contained in {@code model} do not match the sequence IDs of this instance
	 * @throws NullPointerException if {@code model} is {@code null}
	 */
	public void appendPartModel(AlignmentPartModel<?> model);

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
	public void insertPartModel(int modelIndex, AlignmentPartModel<?> model);

	/**
	 * Removes the part model at the specified index from this concatenated alignment model.
	 * 
	 * @param index the index of the part model to be removed
	 * @return the part model that has been removed
	 * @throws IndexOutOfBoundsException if {@code index} is below 0 or >= {@link #getPartModelCount()}
	 */
	public AlignmentPartModel<?> removePartModel(int index);

	/**
	 * Returns the part model at the specified position.
	 * 
	 * @param index the index of the part model to be returned
	 * @return the part model at the specified position
	 * @throws IndexOutOfBoundsException if {@code index} is below 0 or >= {@link #getPartModelCount()}
	 */
	public AlignmentPartModel<?> getPartModel(int index);

	/**
	 * Returns the right most part model contained in this alignment.
	 * 
	 * @return the right most model or {@code null} if no part models are contained in this alignment
	 */
	public AlignmentPartModel<?> getLastPartModel();

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
	public int partModelIndex(AlignmentPartModel<?> partModel);

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
	public int columnOffsetForPartModel(AlignmentPartModel<?> partModel);

	/**
	 * Returns the part model currently responsible for providing the contents of the specified column.
	 * 
	 * @param columnIndex the global column index (The first column has the index 0.)
	 * @return the according part model
	 * @throws IndexOutOfBoundsException if {@code columnIndex} is below zero or greater than the index of the 
	 *         right most column in this model 
	 */
	public AlignmentPartModel<?> partModelByColumn(int columnIndex);

	/**
	 * Returns the sequence name (that would be visible to the application user) associated with the 
	 * specified unique ID.
	 * 
	 * @param sequenceID - the unique unmodifiable ID the sequence is identified by
	 * @return the sequence name or {@code null} if no sequence with this ID is contained in this model
	 */
	public String sequenceNameByID(int sequenceID);

	/**
	 * Returns the unique sequence ID associated with the specified name.
	 * 
	 * @param sequenceName - the name of the sequence that would be visible to the application user
	 * @return the sequence ID or {@code -1} if no sequence with the specified name is contained in this model
	 */
	public int sequenceIDByName(String sequenceName);

	/**
	 * Renames a sequence in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be renamed
	 * @param newSequenceName - the new name the sequence shall have
	 * @return the name the sequence had until now
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
	 * @throws DuplicateSequenceNameException if a sequence with the specified new name is already present in 
	 *         the underlying data source 
	 * @throws SequenceNotFoundException if a sequence with the specified ID is not present the underlying
	 *         data source
	 */
	public String renameSequence(int sequenceID, String newSequenceName)
			throws AlignmentSourceNotWritableException,
			DuplicateSequenceNameException, SequenceNotFoundException;

	/**
	 * Returns a collection of change listener that are informed of changes in the data provided by this instance
	 * and the contained {@link AlignmentPartModel}s. Modify this collection to add or remove listeners.
	 * 
	 * @return a modifiable collection of listeners
	 */
	public Collection<AlignmentModelChangeListener> getChangeListeners();

}