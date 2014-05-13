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
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo;


import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import info.bioinfweb.commons.swing.AbstractDocumentEdit;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.LibrAlignSwingAlignmentEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.sequence.SwingAddSequenceEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.sequence.SwingConcreteAddSequenceEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.sequence.SwingRemoveSequenceEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.sequence.SwingRenameSequenceEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.token.SwingInsertTokensEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.token.SwingRemoveTokensEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.token.SwingSetTokensEdit;



/**
 * An implementation of {@link SequenceDataProvider} that creates an {@link UndoableEdit} object for every 
 * modification that is made to the underlying data source using any of the methods specified by
 * {@link SequenceDataProvider}. These edit objects are created every time a modification method is called 
 * and use the modification methods of another implementation of {@link SequenceDataProvider} which has to 
 * be provided on creation. This second instance is responsible for the actual manipulation of the underlying 
 * data source. (All non-modifying methods are directly delegated to the underlying instance.)
 * <p>
 * <b>Important:</b> The modification methods of the specified underlying {@link SequenceDataProvider} 
 * implementation that performs the actual data manipulation must never be called directly, because that
 * would change the state of the data without generating an edit object. In that case the undo 
 * methods of the edit objects would be useless because the would work on an unexpected data state.
 * <p> 
 * This class does not store an {@link UndoManager}. It has to be provided by 
 * the inherited class or the application using the inherited class.
 * <p>
 * By default this class generated default edit objects included in LibrAlign. If you want it to generate
 * custom edit objects (e.g. wrappers around the LibrAlign edit objects matching the needs of your 
 * application) you would have to provide an {@link SwingEditFactory} which creates custom objects.  
 * <p>
 * Since {@link javax.swing.undo.UndoableEdit} and {@link javax.swing.undo.UndoManager} have not really
 * any Swing specific prerequisites, these classes could also be used to track the undo history in an
 * SWT application.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.1
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class SwingUndoSequenceDataProvider<T> implements SequenceDataProvider<T> {
	protected SequenceDataProvider<T> provider;
	private UndoManager undoManager;
	private SwingEditFactory editFactory;
	
	
	/**
	 * Creates a new instance of this class which creates new edit objects using the provided 
	 * {@link SwingEditFactory}.
	 * 
	 * @param provider - the sequence data provider used to perform the actual manipulation of the data
	 *        (It must be able to write at least one of sequences or tokens.)
	 * @param undoManager - the undo manager that will store the generated edit objects
	 * @param editFactory - the custom edit factory that shall be used to create new edit objects  
	 * 
	 * @throws IllegalArgumentException if {@code provider} does not allow to write sequences and tokens
	 *         (If only one of both is forbidden no exception will be thrown.)
	 * @throws NullPointerException if {@code null} is specified for {@code undoManager}
	 */
	public SwingUndoSequenceDataProvider(SequenceDataProvider<T> provider, UndoManager undoManager, 
			SwingEditFactory editFactory) {
		
		super();
		if (provider.isSequencesReadOnly() && provider.isTokensReadOnly()) {
			throw new IllegalArgumentException(
					"The underlying provider must either be able to write sequences or tokens.");
		}
		else if (undoManager == null){
			throw new NullPointerException("The undo manager must not be null.");
		}
		else {
			this.provider = provider;
			this.undoManager = undoManager;
			this.editFactory = editFactory;
		}
	}
	
	
	/**
	 * Creates a new instance of this class which creates default LibrAlign edit objects.
	 * 
	 * @param provider - the sequence data provider used to perform the actual manipulation of the data
	 *        (It must be able to write at least one of sequences or tokens.)
	 * @param undoManager - the undo manager that will store the generated edit objects
	 * 
	 * @throws IllegalArgumentException if {@code provider} does not allow to write sequences and tokens
	 *         (If only one of both is forbidden no exception will be thrown.)
	 * @throws NullPointerException if {@code null} is specified for {@code undoManager}
	 */
	public SwingUndoSequenceDataProvider(SequenceDataProvider<T> provider, UndoManager undoManager) {
		this(provider, undoManager, null);
	}


	/**
	 * Returns the underlying sequence data provider used to perform the actual manipulation of the data.
	 * 
	 * @return an instance of a class implementing {@link SequenceDataProvider}
	 */
	public SequenceDataProvider<T> getUnderlyingProvider() {
		return provider;
	}


	/**
	 * Returns the undo manager used by this instance.
	 * 
	 * @return an instance of {@link UndoManager} or an inherited class
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}


	/**
	 * Returns the edit factory used by this instance to create new edit objects, if one was passed to 
	 * when this instance was created.
	 * 
	 * @return the used edit factory or {@code null} if default LibrAlign edit objects are created
	 */
	public SwingEditFactory getEditFactory() {
		return editFactory;
	}

	
	/**
	 * Checks if this instance uses a custom edit factory to create new edit objects.
	 * 
	 * @return {@code true} if an edit factory is used, {@code false} otherwise.
	 */
	public boolean hasEditFactory() {
		return getEditFactory() != null;
	}
	
	
	/**
	 * This method is called every time the {@code redo} or {@code undo} methods of any of the edit
	 * objects generated by this class are executed. This default implementation is empty but inheriting
	 * classes could overwrite it in order to react to document changes. (This would be an alternative to
	 * registering a {@link SequenceDataChangeListener}.)
	 * 
	 * @see AbstractDocumentEdit#registerDocumentChange()
	 */
	public void registerDocumentChange() {}
	
	
	private void addEdit(LibrAlignSwingAlignmentEdit edit) {
		UndoableEdit processedEdit = edit;
		if (hasEditFactory()) {
			processedEdit = editFactory.createEdit(edit);
		}
		undoManager.addEdit(processedEdit);
		processedEdit.redo();
	}
	
	
	@Override
	public T getTokenAt(int sequenceID, int index) {
		return provider.getTokenAt(sequenceID, index);
	}


	@Override
	public void setTokenAt(int sequenceID, int index, T token)	throws AlignmentSourceNotWritableException {
    setTokensAt(sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void setTokensAt(int sequenceID, int beginIndex,	Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		if (provider.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingSetTokensEdit(this, sequenceID, beginIndex, tokens));
		}
	}


	@Override
	public void insertTokenAt(int sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		
    insertTokensAt(sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		if (provider.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingInsertTokensEdit(this, sequenceID, beginIndex, tokens));
		}
	}


	@Override
	public void removeTokenAt(int sequenceID, int index)
			throws AlignmentSourceNotWritableException {

    removeTokensAt(sequenceID, index, index + 1);
	}


	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		
		if (provider.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingRemoveTokensEdit(this, sequenceID, beginIndex, endIndex));
		}
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		return provider.getSequenceLength(sequenceID);
	}


	@Override
	public int getMaxSequenceLength() {
		return provider.getMaxSequenceLength();
	}


	@Override
	public SequenceDataProviderWriteType getWriteType() {
		return provider.getWriteType();
	}


	@Override
	public boolean isTokensReadOnly() {
		return provider.isTokensReadOnly();
	}


	@Override
	public boolean isSequencesReadOnly() {
		return provider.isSequencesReadOnly();
	}


	@Override
	public AlignmentSourceDataType getDataType() {
		return provider.getDataType();
	}


	@Override
	public boolean containsSequence(int sequenceID) {
		return provider.containsSequence(sequenceID);
	}


	@Override
	public int sequenceIDByName(String sequenceName) {
		return provider.sequenceIDByName(sequenceName);
	}


	@Override
	public String sequenceNameByID(int sequenceID) {
		return provider.sequenceNameByID(sequenceID);
	}


	@Override
	public int addSequence(String sequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			SwingAddSequenceEdit edit = new SwingConcreteAddSequenceEdit(this, sequenceName);
			if (hasEditFactory()) {
				edit = editFactory.createAddSequenceEdit((SwingConcreteAddSequenceEdit)edit);
			}
			undoManager.addEdit(edit);
			edit.redo();
			return edit.getNewSequenceID();
		}
	}


	@Override
	public boolean removeSequence(int sequenceID) {
		if (containsSequence(sequenceID)) {
			addEdit(new SwingRemoveSequenceEdit(this, sequenceID));
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public String renameSequence(int sequenceID, String newSequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			String oldName = sequenceNameByID(sequenceID);  // Needs to be saved before the edit is executed
			addEdit(new SwingRenameSequenceEdit(this, sequenceID, newSequenceName));
			return oldName;
		}
	}


	@Override
	public Iterator<Integer> sequenceIDIterator() {
		return provider.sequenceIDIterator();
	}


	@Override
	public int getSequenceCount() {
		return provider.getSequenceCount();
	}


	@Override
	public Collection<SequenceDataChangeListener> getChangeListeners() {
		return provider.getChangeListeners();
	}
}
