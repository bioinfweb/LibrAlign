/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.model;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.model.adapters.AbstractAlignmentModelAdapter;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.implementations.AbstractMapBasedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.AbstractUndecoratedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;



/**
 * Interface that allows LibrAlign GUI elements to access different types of alignment data sources.
 * Classes implementing this interface act as a model for the sequence names and tokens (e.g. nucleotide
 * or amino acid compounds).
 * <p>
 * All sequences stored are identified by an string ID and not by their names that are possibly displayed
 * to an application user, because these names might change during runtime. Subclasses must ensure that
 * these IDs are unique and do not change during runtime. Additionally IDs of deleted sequences should not 
 * be reused, because other objects might have stored references to these using its ID and therefore would 
 * not be able to determine that the sequence became deleted, if a new sequence with the same ID is present.
 * <p>
 * The ordering of sequences in an {@link AlignmentContentArea} is not defined by the model implementation 
 * but by the instance returned by {@link AlignmentContentArea#getSequenceOrder()}.
 * <p>
 * Note that this interface leaves it up to the implementation if the alignment data is organized in objects
 * storing whole sequences (rows) or another storage pattern. If your implementation uses sequence objects
 * consider implementing {@link SequenceAccessAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @see SequenceAccessAlignmentModel
 * @see AbstractUndecoratedAlignmentModel
 * @see AbstractMapBasedAlignmentModel
 * @see AbstractAlignmentModelAdapter
 *
 * @param <T> the type of sequence elements (tokens) the implementing model object works with
 */
public interface AlignmentModel<T> {
	/**
	 * Returns a string labeling this alignment.
	 * 
	 * @return the name or title of this alignment
	 */
	public String getLabel();
	
	/**
	 * Allows to specify a new label for this alignment.
	 * 
	 * @param label the new label
	 * @throws UnsupportedOperationException if this implementation does not allow to change labels
	 */
	public void setLabel(String label) throws UnsupportedOperationException;
	
	
	/**
	 * Returns the token set which is supported by the implementation.
	 * 
	 * @return a token set containing all valid tokens
	 */
	public TokenSet<T> getTokenSet();
	
	/**
	 * Replaces the current token set with the specified one.
	 * 
	 * @param set - the new token set to be used
	 * @throws UnsupportedOperationException if this implementation does not support changing token sets during runtime
	 * @throws IllegalArgumentException if the specified token set is not a valid replacement for the previous one (in some
	 *         does way does not fulfill the needs of the implementation)
	 */
	public void setTokenSet(TokenSet<T> set) throws UnsupportedOperationException, IllegalArgumentException;
	
	/**
	 * Returns the length of the specified sequence.
	 * 
	 * @param sequenceID - the identifier the sequence in the alignment
	 * @return the length of the sequence or {@code -1} if no sequence with the specified name exists
	 */
	public int getSequenceLength(String sequenceID);
	
	/**
	 * Returns the length of the longest sequence in the alignment which is equivalent to the length of
	 * the alignment. 
	 * <p>
	 * Note that this value represents the number of compounds of the specified data type. If e.g. a
	 * DNA data source is viewed as amino acid data this method would still return the number of 
	 * nucleotides in the longest sequence. 
	 * 
	 * @return the number of columns in the alignment or {@code 0} if there are not sequences contained
	 *         in the underlying data source
	 */
	public int getMaxSequenceLength();

	/**
	 * Returns a value that specifies if whole sequences or single tokens can be edited in the underlying 
	 * data source.
	 * s
	 * @return the write type of this data source 
	 */
	public AlignmentModelWriteType getWriteType();
	
	/**
	 * Checks of tokens can be changed in the underlying data source
	 * 
	 * @return {@code true} if token can be written or removed, {@code false} otherwise
	 */
	public boolean isTokensReadOnly();
	
	/**
	 * Checks of whole sequences can be changed in the underlying data source
	 * 
	 * @return {@code true} if sequences can be written, renamed or removed, {@code false} otherwise
	 */
	public boolean isSequencesReadOnly();
	
  /**
   * Checks if a sequence associated with the specified unique identifier is contained in this model.
   * 
   * @param sequenceID - the ID of the sequence to checked on
   * @return {@code true} if an according sequence was found, {@code false} otherwise 
   */
  public boolean containsSequence(String sequenceID);
  
  /**
   * Returns the unique sequence ID associated with the specified name.
   * 
   * @param sequenceName - the name of the sequence that would be visible to the application user
   * @return the sequence ID or {@code null} if no sequence with the specified name is contained in this model
   */
  public String sequenceIDByName(String sequenceName);

  /**
   * Returns the sequence name (that would be visible to the application user) associated with the 
   * specified unique ID.
   * 
   * @param sequenceID - the unique unmodifiable ID the sequence is identified by
   * @return the sequence name or {@code null} if no sequence with this ID is contained in this model
   */
  public String sequenceNameByID(String sequenceID);

  /**
   * Adds a new empty sequence to the underlying data source and generates an ID for it.
   * 
   * @param sequenceName - the name of the new sequence
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
  public String addSequence(String sequenceName) throws AlignmentSourceNotWritableException;
  
  /**
   * Removes the specified sequence from the underlying data source.
   * 
   * @param sequenceID - the unique ID of the sequence to be removed
   * @return {@code true} if an sequence with the specified ID was removed, {@code false} 
   *         if no sequence with the specified ID was contained in this model
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
  public boolean removeSequence(String sequenceID) throws AlignmentSourceNotWritableException;
  
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
  public String renameSequence(String sequenceID, String newSequenceName) 
  		throws AlignmentSourceNotWritableException, DuplicateSequenceNameException, SequenceNotFoundException;
  
	/**
	 * Returns an iterator over unique IDs of all sequences contained in the underlying data source
	 * in the order they are stored in this model.
	 * 
	 * @return an iterator over the IDs
	 */
	public Iterator<String> sequenceIDIterator();
  
	/**
	 * Returns the number of sequences in the underlying data source.
	 */
	public int getSequenceCount();

  /**
   * Returns all change listeners currently attached to this object. This collection is writable
   * and should also be used to add or remove listeners.
   * <p>
   * This method returns the same object in every call. Therefore changes made to different references
   * always affect all references.
   * 
   * @return a collection object containing the listeners
   */
  public Set<AlignmentModelChangeListener> getChangeListeners();

	/**
	 * Returns the token at the specified position.
	 * 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param index - the index of the element contained in the specified sequence (The first element has the index 0.)
	 * @return the token to be displayed in the GUI alignment view
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws IndexOutOfBoundsException if the specified index is below zero or greater or equal to the length of the
	 *         specified sequence 
	 */
	public T getTokenAt(String sequenceID, int index);
	
	/**
	 * Replaces the token at the specified position by the passed token.
	 * 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param index - the index of the element to be replaced (The first element has the index 0.)
	 * @param token - the new token for the specified position
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws IndexOutOfBoundsException if the specified index is below zero or greater or equal to the length of the
	 *         specified sequence (Note that not all implementations will throw this exception for indices above zero.
	 *         Some may also elongate the sequence accordingly and will up the new positions with e.g. gaps.) 
	 */
	public void setTokenAt(String sequenceID, int index, T token) throws AlignmentSourceNotWritableException;

	/**
	 * Replaces a sequence of tokens starting at the specified position. If the specified collection
	 * contains more tokens than the currently present in the sequence at the specified start position
	 * the additional tokens are appended.
	 * 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @see AlignmentModelUtils#charSequenceToTokenList(CharSequence, TokenSet)
	 */
	public void setTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens) 
			throws AlignmentSourceNotWritableException;

	/**
	 * Appends a token at the end of the sequence.
	 * 
	 * @param sequenceID - the identifier the sequence in the alignment
	 * @param token - the token to be inserted
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 */
	public void appendToken(String sequenceID, T token) throws AlignmentSourceNotWritableException;
	
	/**
	 * Appends a sequence of tokens starting at the end of the current sequence.
	 * 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param tokens - the new tokens for the specified position
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 * @see AlignmentModelUtils#charSequenceToTokenList(CharSequence, TokenSet)
	 */
	public void appendTokens(String sequenceID, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException;

	/**
	 * Inserts a token at the specified position. All tokens located behind the specified index are moved 
	 * by 1.
	 * 
	 * @param sequenceID - the identifier the sequence in the alignment
	 * @param index - the new index the inserted element will have 
	 *        ({@code 0 <= elementIndex < sequenceLength}) 
	 * @param token - the token to be inserted
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 */
	public void insertTokenAt(String sequenceID, int index, T token) throws AlignmentSourceNotWritableException;

	/**
	 * Inserts a sequence of tokens starting at the specified position. All tokens located behind the 
	 * specified index are moved by the number of tokens that are contained in {@code tokens}.
	 * 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 * @see AlignmentModelUtils#charSequenceToTokenList(CharSequence, TokenSet)
	 */
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException;
	
	/**
	 * Removes the token at the specified position from the underlying data source.
	 * 
	 * @param sequenceID - the identifier the sequence in the alignment
	 * @param index - the index of the element to be removed (The first element has the index 0.)
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 */
	public void removeTokenAt(String sequenceID, int index) throws AlignmentSourceNotWritableException;
	
	/**
	 * Removes the token inside the specified interval from the underlying data source.
	 * The subsequence to be removed begins at the specified {@code beginIndex} and extends to the 
	 * token at index {@code endIndex - 1). Thus the length of the subsequence is {@code endIndex - beginIndex}. 
	 * 
	 * @param sequenceID - the identifier the sequence in the alignment
	 * @param beginIndex - the beginning index, inclusive
	 * @param endIndex - the ending index, exclusive
	 * 
	 * @throws SequenceNotFoundException if no according sequence to the specified ID was found in this model
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for tokens
	 */
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex) throws AlignmentSourceNotWritableException;
}