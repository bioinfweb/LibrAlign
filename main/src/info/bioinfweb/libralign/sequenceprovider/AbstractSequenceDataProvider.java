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
package info.bioinfweb.libralign.sequenceprovider;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;



/**
 * Implements general functionality for sequence data providers.
 * 
 * @author Ben St&uml;ver
 * @since 0.0.0
 */
public abstract class AbstractSequenceDataProvider implements SequenceDataProvider {
	private AlignmentSourceDataType dataType;
	private Map<String, Integer> idByNameMap = new TreeMap<String, Integer>();
	private Map<Integer, String> nameByIDMap = new TreeMap<Integer, String>();
	private int nextID = 0;
	private List<SequenceDataChangeListener> changeListeners = new ArrayList<SequenceDataChangeListener>();

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param dataType - the token type the implementing class will be using
	 */
	public AbstractSequenceDataProvider(AlignmentSourceDataType dataType) {
		super();
		this.dataType = dataType;
	}
	

	protected Map<String, Integer> getIdByNameMap() {
		return idByNameMap;
	}


	protected Map<Integer, String> getNameByIDMap() {
		return nameByIDMap;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#getDataType()
	 */
	@Override
	public AlignmentSourceDataType getDataType() {
		return dataType;
	}
	
	
	@Override
	public boolean isTokensReadOnly() {
		return getWriteType().equals(SequenceDataProviderWriteType.SEQUENCES_ONLY) ||
				getWriteType().equals(SequenceDataProviderWriteType.NONE);
	}


	@Override
	public boolean isSequencesReadOnly() {
		return getWriteType().equals(SequenceDataProviderWriteType.TOKENS_ONLY) ||
				getWriteType().equals(SequenceDataProviderWriteType.NONE);
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#containsSequence(int)
	 */
	@Override
	public boolean containsSequence(int sequenceID) {
		return sequenceNameByID(sequenceID) != null;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceNameByID(int)
	 */
	@Override
  public String sequenceNameByID(int sequenceID) {
  	return nameByIDMap.get(sequenceID);
  }
  
  
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceIDByName(java.lang.String)
	 */
	@Override
	public int sequenceIDByName(String sequenceName) {
		return idByNameMap.get(sequenceName);
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
	
	
	/**
	 * This method is called by {@link #addSequence(String)} if {@link #isReadOnly()} returns {@code false}. 
	 * Implementing classes should add the specified new sequence to their underlying data source in this
	 * method. The according events or exceptions are already created by this class if necessary, therefore
	 * this does not need to be done in the implementation of this method.
	 * 
	 * @param sequenceID - the unique identifier for the new sequence which has been generated before the
	 *        call of this method
	 * @param sequenceName - the initial name the new sequence shall have 
	 */
	protected abstract void doAddSequence(int sequenceID, String sequenceName);
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#addSequence(java.lang.String)
	 */
	@Override
	public int addSequence(String sequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			int sequenceID = createNewID();
			idByNameMap.put(sequenceName, sequenceID);
			nameByIDMap.put(sequenceID, sequenceName);
			doAddSequence(sequenceID, sequenceName);
			fireAfterSequenceChange(new SequenceChangeEvent(this, sequenceID, ListChangeType.INSERTION));
			return sequenceID;
		}
	}
	
	
	/**
	 * This method is called by {@link #removeSequence(int)} if {@link #isReadOnly()} returns {@code false}. 
	 * Implementing classes should remove the specified sequence from their underlying data source in this
	 * method. The according events or exceptions are already created by this class if necessary, therefore
	 * this does not need to be done in the implementation of this method.
	 * 
	 * @param sequenceID - the unique identifier of the sequence to be removed
	 */
	protected abstract void doRemoveSequence(int sequenceID);
	
	
	private void performRemove(int sequenceID) {
		idByNameMap.remove(sequenceNameByID(sequenceID));
		doRemoveSequence(sequenceID);
		fireAfterSequenceChange(new SequenceChangeEvent(this, sequenceID, ListChangeType.DELETION));
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#removeSequence(int)
	 */
	@Override
	public boolean removeSequence(int sequenceID) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
			boolean result = containsSequence(sequenceID);
			if (result) {
				nameByIDMap.remove(sequenceID);
				performRemove(sequenceID);
			}
			return result;
		}
	}


	/**
	 * This method is called by {@link #renameSequence(int, String)} if {@link #isReadOnly()} returns 
	 * {@code false} and the specified sequence is contained in this model. Implementing classes should 
	 * rename the specified sequence in their underlying data source in this method. The according 
	 * events or exceptions are already created by this class if necessary, therefore this does not need 
	 * to be done in the implementation of this method.
	 * 
	 * @param sequenceID - the unique identifier of the sequence to be removed
   * @param newSequenceName - the new name the sequence shall have
	 */
	protected abstract void doRenameSequence(int sequenceID, String newSequenceName);
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#renameSequence(int, java.lang.String)
	 */
	@Override
	public String renameSequence(int sequenceID, String newSequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else {
		  String sequenceName = sequenceNameByID(sequenceID);
		  if (sequenceName != null) {
		  	doRenameSequence(sequenceID, newSequenceName);
				fireAfterSequenceChange(new SequenceChangeEvent(this, sequenceID, ListChangeType.DELETION));
		  }
			return sequenceName;
		}
	}

	
//TODO This method is commented out because it would not return the IDs in the order they are stored in the data source.
//	/* (non-Javadoc)
//	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceIDIterator()
//	 */
//	@Override
//	public Iterator<Integer> sequenceIDIterator() {
//		final Iterator<Integer> iterator = nameByIDMap.keySet().iterator();
//		return new Iterator<Integer>() {
//			private int currentID = -1;
//			
//			@Override
//			public boolean hasNext() {
//				return iterator.hasNext();
//			}
//
//			@Override
//			public Integer next() {
//				currentID = iterator.next();
//				return currentID;
//			}
//
//			@Override
//			public void remove() {
//				if (isSequencesReadOnly()) {
//					throw new UnsupportedOperationException("The underlying data source does not allow the removal of sequences.");
//				}
//				else {
//					performRemove(currentID);
//					iterator.remove();
//				}
//			}
//		};				
//	}


	@Override
	public Collection<SequenceDataChangeListener> getChangeListeners() {
		return changeListeners;
	}


	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterSequenceChange(SequenceChangeEvent e) {
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceChange(e);
		}
	}


	/**
	 * Informs all listeners that a sequence has been renamed.
	 */
	protected void fireAfterSequenceRenamed(SequenceRenamedEvent e) {
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceRenamed(e);
		}
	}
	

	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterTokenChange(TokenChangeEvent e) {
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterTokenChange(e);
		}
	}
}
