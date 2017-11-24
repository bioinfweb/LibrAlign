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
package info.bioinfweb.libralign.model.implementations.swingundo;


import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import info.bioinfweb.commons.swing.AbstractDocumentEdit;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.implementations.AbstractAlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.LibrAlignSwingAlignmentEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingAddSequenceEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingConcreteAddSequenceEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingRemoveSequenceEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingRenameSequenceEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.token.SwingInsertTokensEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.token.SwingRemoveTokensEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.token.SwingSetTokensEdit;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * An implementation of {@link AlignmentModel} that creates an {@link UndoableEdit} object for every 
 * modification that is made to the underlying data source using any of the methods specified by
 * {@link AlignmentModel}. These edit objects are created every time a modification method is called 
 * and delegate to the modification methods of another implementation of {@link AlignmentModel} which 
 * has to be provided on creation. This second instance is responsible for the actual manipulation of 
 * the underlying data source. (All non-modifying methods are directly delegated to the underlying 
 * instance.)
 * <p>
 * <b>Important:</b> The modification methods of the specified underlying {@link AlignmentModel} 
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
 * @since 0.1.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class SwingUndoAlignmentModel<T> extends AbstractAlignmentModel<T> 
		implements AlignmentModel<T>, AlignmentModelView<T, T> {
	
	protected AlignmentModel<T> underlyingModel;
	private UndoManager undoManager;
	private SwingEditFactory<T> editFactory;
	
	
	/**
	 * Creates a new instance of this class which creates new edit objects using the provided 
	 * {@link SwingEditFactory}.
	 * 
	 * @param underlyingModel - the alignment model used to perform the actual manipulation of the data
	 *        (It must be able to write at least one of sequences or tokens.)
	 * @param undoManager - the undo manager that will store the generated edit objects
	 * @param editFactory - the custom edit factory that shall be used to create new edit objects  
	 * 
	 * @throws IllegalArgumentException if {@code provider} does not allow to write sequences and tokens
	 *         (If only one of both is forbidden no exception will be thrown.)
	 * @throws NullPointerException if {@code null} is specified for {@code undoManager}
	 */
	public SwingUndoAlignmentModel(AlignmentModel<T> underlyingModel, UndoManager undoManager, 
			SwingEditFactory<T> editFactory) {
		
		super();
		if (underlyingModel.isSequencesReadOnly() && underlyingModel.isTokensReadOnly()) {
			throw new IllegalArgumentException(
					"The underlying provider must either be able to write sequences or tokens.");
		}
		else if (undoManager == null){
			throw new NullPointerException("The undo manager must not be null.");
		}
		else {
			this.underlyingModel = underlyingModel;
			this.undoManager = undoManager;
			this.editFactory = editFactory;
			
			@SuppressWarnings("rawtypes")
			final SwingUndoAlignmentModel newModel = this;
			underlyingModel.getChangeListeners().add(new AlignmentModelChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public <V> void afterTokenChange(TokenChangeEvent<V> e) {
					fireAfterTokenChange((TokenChangeEvent<T>)e.cloneWithNewSource(newModel));
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public <V> void afterSequenceRenamed(SequenceRenamedEvent<V> e) {
					fireAfterSequenceRenamed((SequenceRenamedEvent<T>)e.cloneWithNewSource(newModel));
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public <V> void afterSequenceChange(SequenceChangeEvent<V> e) {
					fireAfterSequenceChange((SequenceChangeEvent<T>)e.cloneWithNewSource(newModel));
				}
				
				@Override
				public <V, W> void afterProviderChanged(AlignmentModel<V> previous, AlignmentModel<W> current) {}  // Forwarding this event does not make sence, since the underlying model should not be contained in any alignment area. If it anyway is, it would be independet of the alignment area this model is contained in.
			});
		}
	}
	
	
	/**
	 * Creates a new instance of this class which creates default LibrAlign edit objects.
	 * 
	 * @param underlyingModel - the alignment model used to perform the actual manipulation of the data
	 *        (It must be able to write at least one of sequences or tokens.)
	 * @param undoManager - the undo manager that will store the generated edit objects
	 * 
	 * @throws IllegalArgumentException if {@code provider} does not allow to write sequences and tokens
	 *         (If only one of both is forbidden no exception will be thrown.)
	 * @throws NullPointerException if {@code null} is specified for {@code undoManager}
	 */
	public SwingUndoAlignmentModel(AlignmentModel<T> underlyingModel, UndoManager undoManager) {
		this(underlyingModel, undoManager, null);
	}


	/**
	 * Returns the underlying sequence data provider used to perform the actual manipulation of the data.
	 * 
	 * @return an instance of a class implementing {@link AlignmentModel}
	 */
	public AlignmentModel<T> getUnderlyingModel() {
		return underlyingModel;
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
	public SwingEditFactory<T> getEditFactory() {
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
	 * registering a {@link AlignmentModelChangeListener}.)
	 * 
	 * @see AbstractDocumentEdit#registerDocumentChange()
	 */
	public void registerDocumentChange() {}
	
	
	private void addEdit(LibrAlignSwingAlignmentEdit<T> edit) {
		UndoableEdit processedEdit = edit;
		if (hasEditFactory()) {
			processedEdit = editFactory.createEdit(edit);
		}
		undoManager.addEdit(processedEdit);
		processedEdit.redo();
	}
	
	
	@Override
	public String getLabel() {
		return underlyingModel.getLabel();
	}


	@Override
	public void setLabel(String label) throws UnsupportedOperationException {
		underlyingModel.setLabel(label);  //TODO This should also be done by an edit.
	}


	@Override
	public TokenSet<T> getTokenSet() {
		return underlyingModel.getTokenSet();
	}


	@Override
	public void setTokenSet(TokenSet<T> set) {
		underlyingModel.setTokenSet(set);  //TODO This should also be done by an edit. How to track changes of the token set?
	}


	@Override
	public T getTokenAt(String sequenceID, int index) {
		return underlyingModel.getTokenAt(sequenceID, index);
	}


	@Override
	public void setTokenAt(String sequenceID, int index, T token)	throws AlignmentSourceNotWritableException {
    setTokensAt(sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void setTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		if (underlyingModel.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingSetTokensEdit<T>(this, sequenceID, beginIndex, tokens));
		}
	}


	@Override
	public void appendToken(String sequenceID, T token) throws AlignmentSourceNotWritableException {
		insertTokenAt(sequenceID, getSequenceLength(sequenceID), token);
	}


	@Override
	public void appendTokens(String sequenceID, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException {
		insertTokensAt(sequenceID, getSequenceLength(sequenceID), tokens);
	}


	@Override
	public void insertTokenAt(String sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		
    insertTokensAt(sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		if (underlyingModel.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingInsertTokensEdit<T>(this, sequenceID, beginIndex, tokens));
		}
	}


	@Override
	public void removeTokenAt(String sequenceID, int index)
			throws AlignmentSourceNotWritableException {

    removeTokensAt(sequenceID, index, index + 1);
	}


	@Override
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		
		if (underlyingModel.isTokensReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			addEdit(new SwingRemoveTokensEdit<T>(this, sequenceID, beginIndex, endIndex));
		}
	}


	@Override
	public int getSequenceLength(String sequenceID) {
		return underlyingModel.getSequenceLength(sequenceID);
	}


	@Override
	public int getMaxSequenceLength() {
		return underlyingModel.getMaxSequenceLength();
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return underlyingModel.getWriteType();
	}


	@Override
	public boolean isTokensReadOnly() {
		return underlyingModel.isTokensReadOnly();
	}


	@Override
	public boolean isSequencesReadOnly() {
		return underlyingModel.isSequencesReadOnly();
	}


	@Override
	public boolean containsSequence(String sequenceID) {
		return underlyingModel.containsSequence(sequenceID);
	}


	@Override
	public Set<String> sequenceIDsByName(String sequenceName) {
		return underlyingModel.sequenceIDsByName(sequenceName);
	}


	@Override
	public String sequenceNameByID(String sequenceID) {
		return underlyingModel.sequenceNameByID(sequenceID);
	}


	@Override
	public String addSequence(String sequenceName) {
		return addSequence(sequenceName, null);
	}


	@Override
	public String addSequence(String sequenceName, String sequenceID) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			SwingAddSequenceEdit edit = new SwingConcreteAddSequenceEdit<T>(this, sequenceName, sequenceID);
			if (hasEditFactory()) {
				edit = editFactory.createAddSequenceEdit((SwingConcreteAddSequenceEdit<T>)edit);
			}
			undoManager.addEdit(edit);
			edit.redo();
			return edit.getNewSequenceID();
		}
	}


	@Override
	public boolean removeSequence(String sequenceID) {
		if (containsSequence(sequenceID)) {
			addEdit(new SwingRemoveSequenceEdit<T>(this, sequenceID));
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public String renameSequence(String sequenceID, String newSequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			String oldName = sequenceNameByID(sequenceID);  // Needs to be saved before the edit is executed
			addEdit(new SwingRenameSequenceEdit<T>(this, sequenceID, newSequenceName));
			return oldName;
		}
	}


	@Override
	public Iterator<String> sequenceIDIterator() {
		return underlyingModel.sequenceIDIterator();
	}


	@Override
	public int getSequenceCount() {
		return underlyingModel.getSequenceCount();
	}
}
