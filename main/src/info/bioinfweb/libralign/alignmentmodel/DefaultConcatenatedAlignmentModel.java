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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentmodel.adapters.AbstractSequenceDataAdapter;
import info.bioinfweb.libralign.alignmentmodel.events.PartModelChangeEvent;
import info.bioinfweb.libralign.alignmentmodel.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.alignmentmodel.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.alignmentmodel.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.alignmentmodel.implementations.AbstractMapBasedSequenceDataProvider;
import info.bioinfweb.libralign.alignmentmodel.implementations.AbstractSequenceDataProvider;



//TODO Update JavaDoc
/**
 * Interface that allows LibrAlign GUI elements to access different types of alignment data sources.
 * Classes implementing this interface act as a model for the sequence names and sequence contents 
 * (nucleotides, amino acids or any other type of tokens).
 * <p>
 * All sequences stored are identified by an integer ID and not by their names (which are possibly displayed
 * to an application user), because these names might change during runtime. Subclasses must ensure that
 * these IDs are unique and greater or equal to zero and remain constant during runtime. Additionally IDs of 
 * deleted sequences should not be reused, because other objects might have stored references to the deleted 
 * sequence using its ID and therefore might not be able to determine that the sequence was deleted, if a 
 * new sequence with the same ID is present.
 * <p>
 * The numeric values of the ID do not have to be sorted in the order of the sequences, which is defined by 
 * {@link #sequenceNameIterator()}. The ordering of sequences in an {@link AlignmentContentArea} is anyway not
 * defined by the data provider but by the instance returned by {@link AlignmentContentArea#getSequenceOrder()}.
 * <p>
 * Note that this interface leafs it up to the implementation if the alignment data is organized in objects
 * storing whole sequences (rows) or another storage pattern. If your implementation uses sequence objects
 * consider implementing {@link SequenceAccessDataProvider} (which inherits from this interface).
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * 
 * @see SequenceAccessDataProvider
 * @see AbstractSequenceDataProvider
 * @see AbstractMapBasedSequenceDataProvider
 * @see AbstractSequenceDataAdapter
 *
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class DefaultConcatenatedAlignmentModel implements AlignmentModel<Object>, ConcatenatedAlignmentModel {
	public static final int NO_SEQUENCE_FOUND = -1;
	
	
	private List<AlignmentPartModel> partModels = new ArrayList<AlignmentPartModel>();
	private Map<String, Integer> idByNameMap = new TreeMap<String, Integer>();
	private Map<Integer, String> nameByIDMap = new TreeMap<Integer, String>();
	private int nextID = 0;
	private List<AlignmentModelChangeListener> changeListeners = new ArrayList<AlignmentModelChangeListener>();
	
	
	protected Map<String, Integer> getIDByNameMap() {
		return idByNameMap;
	}


	protected Map<Integer, String> getNameByIDMap() {
		return nameByIDMap;
	}


	private boolean checkPartSequences(AlignmentPartModel<?> model) {
		if (partModels.isEmpty()) {
			return true;
		}
		else if (getSequenceCount() == model.getSequenceCount()) {
			Iterator<Integer> iterator = sequenceIDIterator();
			while (iterator.hasNext()) {
				if (!model.containsSequence(iterator.next())) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#appendPartModel(info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public void appendPartModel(AlignmentPartModel<?> model) {
		insertPartModel(getSequenceCount(), model);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#insertPartModel(int, info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public void insertPartModel(int index, AlignmentPartModel<?> model) {
		if (model == null) {
			throw new NullPointerException("Part models to be added must not be null.");
		}
		else if (!equals(model.getOwner())) {
			throw new IllegalArgumentException("This instance needs to be the owner of part models that shall be added.");
		}
		else if (!checkPartSequences(model)) {
			throw new IllegalArgumentException("The sequences in the specified part model do not have the same IDs as the "
					+ "sequences already contained in the other models.");
		}
		else {
			partModels.add(index, model);
			fireAfterPartModelChange(PartModelChangeEvent.newInsertInstance(model, index));
		}
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#removePartModel(int)
	 */
	@Override
	public AlignmentPartModel<?> removePartModel(int index) {
		AlignmentPartModel<?> result = partModels.remove(index);
		fireAfterPartModelChange(PartModelChangeEvent.newRemoveInstance(result, index));
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getPartModel(int)
	 */
	@Override
	public AlignmentPartModel<?> getPartModel(int index) {
		return partModels.get(index);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getLastPartModel()
	 */
	@Override
	public AlignmentPartModel<?> getLastPartModel() {
		return getPartModel(getPartModelCount() - 1);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getPartModelCount()
	 */
	@Override
	public int getPartModelCount() {
		return partModels.size();
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelIndex(info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public int partModelIndex(AlignmentPartModel<?> partModel) {
		return partModels.indexOf(partModel);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelIndexOffset(int)
	 */
	@Override
	public int columnOffsetForPartModel(int partIndex) {
		int result = 0;
		for (int i = 0; i < partIndex; i++) {
			result += partModels.get(i).getMaxSequenceLength();
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelIndexOffset(info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public int columnOffsetForPartModel(AlignmentPartModel<?> partModel) {
		int index = partModelIndex(partModel);
		if (index == -1) {
			throw new IllegalArgumentException("The specified part model is not contained in this alignment.");
		}
		else {
			return columnOffsetForPartModel(index);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelByColumn(int)
	 */
	@Override
	public AlignmentPartModel<?> partModelByColumn(int columnIndex) {
		if (columnIndex < 0) {
			throw new IndexOutOfBoundsException("A column index needs to be greater or equal to zero, but was " + columnIndex + ".");
		}
		else {
			int offset = 0;
			for (AlignmentPartModel<?> partModel : partModels) {
				offset += partModel.getMaxSequenceLength();
				if (columnIndex < offset) {
					return partModel;
				}
			}
			throw new IndexOutOfBoundsException("The specified column index (" + columnIndex + 
					") lies behind the last column contained in this model (" + (offset - 1) + ").");
		}
	}
	
	
	/**
	 * Returns the length of the sequence with the specified ID.
	 * <p>
	 * The returned value includes tokens that are inserted between the part models, if they contain sequences
	 * of different length. No such tokens are inserted behind the end of sequences of the last part model. 
	 * Therefore a possible difference between the value returned here and the result of {@link #getMaxSequenceLength()}
	 * is only influenced the length of the according sequence in the right most part model. 
	 * 
	 * @param sequenceID the identifier the sequence in the alignment
	 * @return the length of the sequence or -1 if no sequence with the specified name exists 
	 */
	@Override
	public int getSequenceLength(int sequenceID) {
		return columnOffsetForPartModel(getPartModelCount() - 1) + getLastPartModel().getSequenceLength(sequenceID);
	}

	
	@Override
	public int getMaxSequenceLength() {
		int result = 0;
		for (AlignmentPartModel<?> partModel : partModels) {
			result += partModel.getMaxSequenceLength();
		}
		return result;
	}

	
	/**
	 * Returns the common write type of the all contained part models according to the return values
	 * of {@link #isSequencesReadOnly()} and {@link #isTokensReadOnly()} (see there for more detailed 
	 * information).
	 * 
	 * @return the common write type
	 */
	@Override
	public SequenceDataProviderWriteType getWriteType() {
		boolean tokensOnly = isTokensReadOnly();
		boolean sequencesOnly = isSequencesReadOnly();
		if (tokensOnly && sequencesOnly) {
			return SequenceDataProviderWriteType.NONE;
		}
		else if (tokensOnly) {
			return SequenceDataProviderWriteType.SEQUENCES_ONLY;
		}
		else if (sequencesOnly) {
			return SequenceDataProviderWriteType.TOKENS_ONLY;
		}
		else {
			return SequenceDataProviderWriteType.BOTH;
		}
	}
	

	/**
	 * Checks if at least one of the contained part models allows writing tokens. Note that this method might
	 * return {@code true} although some columns of this model are not writable, because they are part of
	 * an atomic part model.
	 * <p>
	 * Note that the return value of this method may change if part models are inserted or removed from this 
	 * model.
	 * 
	 * @return {@code true} of none of the contained part models allows writing of tokens or {@code false} if
	 *         at least one of them allows writing tokens
	 */
	@Override
	public boolean isTokensReadOnly() {
		for (AlignmentPartModel<?> partModel : partModels) {
			if (!partModel.isTokensReadOnly()) {
				return false;
			}
		}
		return true;
	}
	

	/**
	 * Checks whether sequences can be inserted and removed from this model. This is only the case, if all
	 * contained part models support the insertion or removal of sequences.
	 * <p>
	 * Note that the return value of this method may change if part models are inserted or removed from this 
	 * model.
	 * 
	 * @return {@code true} if all contained part models support adding or removing sequences or {@code false}
	 *         if at least one part model does not support such an operation.
	 */
	@Override
	public boolean isSequencesReadOnly() {
		for (AlignmentPartModel<?> partModel : partModels) {
			if (partModel.isSequencesReadOnly()) {
				return true;
			}
		}
		return false;
	}
	

	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#sequenceIDIterator()
	 */
	@Override
	public Iterator<Integer> sequenceIDIterator() {
		// TODO Auto-generated method stub
		return null;
	}


  /* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#sequenceNameByID(int)
	 */
  @Override
	public String sequenceNameByID(int sequenceID) {
  	return getNameByIDMap().get(sequenceID);
  }
  
  
  /* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#sequenceIDByName(java.lang.String)
	 */
	@Override
	public int sequenceIDByName(String sequenceName) {
		Integer result = getIDByNameMap().get(sequenceName);
		if (result == null) {
			return NO_SEQUENCE_FOUND;
		}
		else {
			return result;
		}
	}


	/**
	 * Returns a new sequence identifier that has not been returned before.
	 * 
	 * @return a value greater or equal to zero
	 */
	protected int createNewID() {
		nextID++;
		return nextID - 1;
	}
	
	
  /* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#renameSequence(int, java.lang.String)
	 */
	@Override
	public String renameSequence(int sequenceID, String newSequenceName)
			throws AlignmentSourceNotWritableException,
			DuplicateSequenceNameException, SequenceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean containsSequence(int sequenceID) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public int addSequence(String sequenceName)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public boolean removeSequence(int sequenceID)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public int getSequenceCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public Object getTokenAt(int sequenceID, int index) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void setTokenAt(int sequenceID, int index, Object token)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void setTokensAt(int sequenceID, int beginIndex,
			Collection<? extends Object> tokens)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void appendTokenAt(int sequenceID, Object token)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void appendTokensAt(int sequenceID, Collection<? extends Object> tokens)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void insertTokenAt(int sequenceID, int index, Object token)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void insertTokensAt(int sequenceID, int beginIndex,
			Collection<? extends Object> tokens)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void removeTokenAt(int sequenceID, int index)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		// TODO Auto-generated method stub
		
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getChangeListeners()
	 */
	@Override
	public Collection<AlignmentModelChangeListener> getChangeListeners() {
		return changeListeners;
	}
	
	
	/**
	 * Informs all listeners that a part model has been inserted or removed.
	 */
	protected <T> void fireAfterPartModelChange(PartModelChangeEvent<T> e) {
		Iterator<AlignmentModelChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterPartModelChange(e);
		}
	}
}


