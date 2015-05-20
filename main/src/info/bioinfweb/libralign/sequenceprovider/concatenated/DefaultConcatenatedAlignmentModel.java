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
package info.bioinfweb.libralign.sequenceprovider.concatenated;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.implementations.AbstractSequenceDataProvider;



public class DefaultConcatenatedAlignmentModel extends AbstractSequenceDataProvider<Object> implements ConcatenatedAlignmentModel {
	private class PartModelInfo {
		public SequenceDataProvider<Object> model;
		public Map<Integer, Integer> partIDByParentIDMap;
		public Map<Integer, Integer> parentIDByPartIDMap;
		
		
		private void fillIDMap() {
			Iterator<Integer> iterator = model.sequenceIDIterator();
			while (iterator.hasNext()) {
				int partID = iterator.next();
				String name = model.sequenceNameByID(partID);
				addIDMapping(name, partID);
			}
		}
		
		
		public PartModelInfo(SequenceDataProvider<Object> model) {
			super();
			this.model = model;
			partIDByParentIDMap = new TreeMap<Integer, Integer>();
			parentIDByPartIDMap = new TreeMap<Integer, Integer>();
			fillIDMap();
		}
		
		
		public void addIDMapping(String sequenceName, int partID) {
			int parentID = sequenceIDByName(sequenceName);
			if (parentID == -1) {
				parentID = createNewID();
				getNameByIDMap().put(parentID, sequenceName);
				getIDByNameMap().put(sequenceName, parentID);
				fireAfterSequenceChange(SequenceChangeEvent.newInsertInstance(thisModel, parentID));
			}
			addIDMapping(parentID, partID);
			//TODO Fire token event if new tokens have been added to the parent model by this operation. (Tokens returned for other part models that do not have this sequence would also needed to be contained in the event.)
		}
		
		
		public void addIDMapping(int parentID, int partID) {
			parentIDByPartIDMap.put(partID, parentID);
			partIDByParentIDMap.put(parentID, partID);
		}
	}
	
	private DefaultConcatenatedAlignmentModel thisModel = this;
	private List<PartModelInfo> partModels = new ArrayList<PartModelInfo>();
	private SequenceDataChangeListener partListener = new SequenceDataChangeListener() {
				private boolean renamingOngoing = false;
		
				@Override
				public <T> void afterTokenChange(TokenChangeEvent<T> e) {
					fireAfterTokenChange(TokenChangeEvent.newForwardedInstance(thisModel, columnOffsetForPartModel(e.getSource()), e));
				}
				
				/**
				 * Renames the according sequence in all other models as well. 
				 */
				@Override
				public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
					if (!renamingOngoing) {
						renamingOngoing = true;
						try {
							renameSequence(partModels.get(partModelIndex(e.getSource())).parentIDByPartIDMap.get(e.getSequenceID()), e.getNewName());
									//TODO Can this method be used here? What happens when the source part model is renamed again?
									// Event should be fired in here and does not need to be forwarded
						}
						finally {
							renamingOngoing = false;
						}
					}
				}
				
				@Override
				public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
					PartModelInfo info = partModels.get(partModelIndex(e.getSource()));
					if (e.getType().equals(ListChangeType.INSERTION)) {
						info.addIDMapping(e.getSource().sequenceNameByID(e.getSequenceID()), e.getSequenceID());  // Fires event if necessary
					}
					else if (e.getType().equals(ListChangeType.DELETION)) {
						Integer parentID = info.parentIDByPartIDMap.remove(e.getSequenceID());
						info.partIDByParentIDMap.remove(parentID);
						if (!containsAnySequencePart(parentID)) {
							removeSequence(parentID);  // Fires event  //TODO Can this method be used in this case?
						}
					}
					else {  // Replacing of sequences is not intended by the architecture.
						throw new InternalError("Unexpected sequence change type " + e.getType());
					}
					//TODO Fire according token event, if no sequence event was fired.
				}
				
				/**
				 * This method moved this listener back to the old model if this is still a part model of
				 * the associated concatenated model.
				 * <p>
				 * This event is not forwarded to the listeners of the concatenated model because this model is not affected.
				 */
				@Override
				public <T, U> void afterProviderChanged(SequenceDataProvider<T> previous,	SequenceDataProvider<U> current) {
					if (!previous.getChangeListeners().contains(this) && containsPartModel(previous)) {
						previous.getChangeListeners().add(this);
					}
					if (current.getChangeListeners().contains(this) && !containsPartModel(previous)) {
						previous.getChangeListeners().remove(this);
					}
				}
			};

	
	private boolean containsAnySequencePart(int parentID) {
		for (PartModelInfo partModelInfo : partModels) {
			if (partModelInfo.partIDByParentIDMap.containsKey(parentID)) {
				return true;
			}
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#appendPartModel(info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public void appendPartModel(SequenceDataProvider<?> model) {
		insertPartModel(getSequenceCount(), model);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#insertPartModel(int, info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void insertPartModel(int index, SequenceDataProvider<?> model) {
		if (model == null) {
			throw new NullPointerException("The part model to be added must not be null.");
		}
		else {
			model.getChangeListeners().add(partListener);
			partModels.add(index, new PartModelInfo((SequenceDataProvider<Object>)model));
			fireAfterPartModelChange(PartModelChangeEvent.newInsertInstance(model, index));
		}
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#removePartModel(int)
	 */
	@Override
	public SequenceDataProvider<?> removePartModel(int index) {
		SequenceDataProvider<?> result = partModels.remove(index).model;
		result.getChangeListeners().remove(partListener);
		fireAfterPartModelChange(PartModelChangeEvent.newRemoveInstance(result, index));
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getPartModel(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SequenceDataProvider<Object> getPartModel(int index) {
		return partModels.get(index).model;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#getLastPartModel()
	 */
	@Override
	public SequenceDataProvider<Object> getLastPartModel() {
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
	public int partModelIndex(SequenceDataProvider<?> partModel) {
		for (int i = 0; i < partModels.size(); i++) {
			if (partModels.get(i).model.equals(partModel)) {
				return i;
			}
		}
		return -1;
	}
	
	
	@Override
	public boolean containsPartModel(SequenceDataProvider<?> partModel) {
		return partModelIndex(partModel) != -1;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelIndexOffset(int)
	 */
	@Override
	public int columnOffsetForPartModel(int partIndex) {
		int result = 0;
		for (int i = 0; i < partIndex; i++) {
			result += partModels.get(i).model.getMaxSequenceLength();
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel#partModelIndexOffset(info.bioinfweb.libralign.alignmentmodel.AlignmentPartModel)
	 */
	@Override
	public int columnOffsetForPartModel(SequenceDataProvider<?> partModel) {
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
	public SequenceDataProvider<Object> partModelByColumn(int columnIndex) {
		if (columnIndex < 0) {
			throw new IndexOutOfBoundsException("A column index needs to be greater or equal to zero, but was " + columnIndex + ".");
		}
		else {
			int offset = 0;
			for (PartModelInfo partModelInfo : partModels) {
				offset += partModelInfo.model.getMaxSequenceLength();
				if (columnIndex < offset) {
					return partModelInfo.model;
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
		for (PartModelInfo partModelInfo : partModels) {
			result += partModelInfo.model.getMaxSequenceLength();
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
		for (PartModelInfo partModelInfo : partModels) {
			if (!partModelInfo.model.isTokensReadOnly()) {
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
		for (PartModelInfo partModelInfo : partModels) {
			if (partModelInfo.model.isSequencesReadOnly()) {
				return true;
			}
		}
		return false;
	}


	@Override
	public int parentIDByPartID(int partID, int partIndex) throws IllegalArgumentException {
		Integer parentID = partModels.get(partIndex).parentIDByPartIDMap.get(partID);
		if (parentID == null) {
			throw new IllegalArgumentException("The ID \"" + partID + "\" was not found in the part model with the index \"" + 
					partIndex + "\".");
		}
		else {
			return parentID;
		}
	}


	@Override
	public int partIDByParentID(int parentID, int partIndex) throws IllegalArgumentException {
		Integer partID = partModels.get(partIndex).partIDByParentIDMap.get(parentID);
		if (partID == null) {
			if (sequenceNameByID(parentID) == null) {
				throw new IllegalArgumentException("The ID \"" + parentID + "\" was not found in this model.");
			}
			else {
				return -1;
			}
		}
		else {
			return partID;
		}
	}
}
