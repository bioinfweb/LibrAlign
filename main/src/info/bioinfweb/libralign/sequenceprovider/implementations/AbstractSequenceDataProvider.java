/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.sequenceprovider.implementations;


import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.sequenceprovider.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;



/**
 * Implements general functionality for sequence data providers.
 * 
 * @author Ben St&uml;ver
 * @since 0.0.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractSequenceDataProvider<T> implements SequenceDataProvider<T> {
	private Map<String, Integer> idByNameMap = new TreeMap<String, Integer>();
	private Map<Integer, String> nameByIDMap = new TreeMap<Integer, String>();
	private int nextID = 0;
	private TokenSet<T> tokenSet;
	private List<SequenceDataChangeListener> changeListeners = new ArrayList<SequenceDataChangeListener>();
	private int maxSequenceLength = 0;
	private boolean recalculateMaxSequenceLength = true; 

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tokenSet - the set of allowed tokens in the sequences of the implementing class
	 */
	public AbstractSequenceDataProvider(TokenSet<T> tokenSet) {
		super();
		this.tokenSet =  tokenSet;
	}
	

	@Override
	public TokenSet<T> getTokenSet() {
		return tokenSet;
	}


	@Override
	public void setTokenSet(TokenSet<T> set) {
		tokenSet = set;
	}


	protected Map<String, Integer> getIDByNameMap() {
		return idByNameMap;
	}


	protected Map<Integer, String> getNameByIDMap() {
		return nameByIDMap;
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
  	return getNameByIDMap().get(sequenceID);
  }
  
  
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceIDByName(java.lang.String)
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
			getIDByNameMap().put(sequenceName, sequenceID);
			getNameByIDMap().put(sequenceID, sequenceName);
			doAddSequence(sequenceID, sequenceName);
			fireAfterSequenceChange(new SequenceChangeEvent<T>(this, sequenceID, ListChangeType.INSERTION));
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
	
	
	/**
	 * Removes a sequence from the map instances returned by {@link #getNameByIDMap()} and 
	 * {@link AbstractSequenceDataProvider#getIDByNameMap()}. No events are fired and
	 * {@link #doRemoveSequence(int)} is not called by this method. It is a tool method for
	 * inherited classes that offer additional removal operations (e.g. in returned iterators)
	 * which have to avoid a {@link ConcurrentModificationException}.
	 * 
	 * @param sequenceID - the ID associated with the sequence mapping that shall be removed 
	 */
	protected void removeSequenceNameMapping(int sequenceID) {
		getNameByIDMap().remove(sequenceID);
		getIDByNameMap().remove(sequenceNameByID(sequenceID));
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
				removeSequenceNameMapping(sequenceID);
				doRemoveSequence(sequenceID);
				fireAfterSequenceChange(new SequenceChangeEvent<T>(this, sequenceID, ListChangeType.DELETION));
			}
			return result;
		}
	}


	/**
	 * This method is called by {@link #renameSequence(int, String)} if {@link #isReadOnly()} returns 
	 * {@code false} and the specified sequence is contained in this model. Implementing classes should 
	 * overwrite this method and rename the specified sequence in their underlying data source if necessary.
	 * <p>
	 * The renaming in the maps associating names and IDs used by this class have benn updated before the call 
	 * of this method. The according events or exceptions are created by this class if necessary, therefore 
	 * all of this does not need to be done in the implementation of this method. (A change event is fired
	 * after this method has been executed.)
	 * <p>
	 * This default implementation is empty.
	 * 
	 * @param sequenceID - the unique identifier of the sequence to be removed
   * @param newSequenceName - the new name the sequence shall have
	 */
	protected void doRenameSequence(int sequenceID, String newSequenceName) {}
	
	
	/**
	 * Iterates over all sequence IDs and determines the maximum sequence length by calling
	 * {@link #getSequenceLength(int)} for each ID.
	 * 
	 * @return the maximum length a sequence in the underlying data source currently has
	 * @see #getApproxMaxSequenceLength()
	 */
	@Override
	public int getMaxSequenceLength() {
		if (recalculateMaxSequenceLength) {
			maxSequenceLength = 0;
			Iterator<Integer> iterator = sequenceIDIterator();
			while (iterator.hasNext()) {
				maxSequenceLength = Math.max(maxSequenceLength, getSequenceLength(iterator.next()));
			}
			recalculateMaxSequenceLength = false;
		}
		return maxSequenceLength;
	}
	
	
	/**
	 * Returns the maximum sequence length that is currently known. The returned value might not reflect
	 * recent changes to the model. Use {@link #getMaxSequenceLength()} if you need an up-to-date value,
	 * but for a high number of sequences {@link #getMaxSequenceLength()} might be much slower.
	 * 
	 * @return the maximum length a sequence in the underlying data source currently has that is currently known
	 */
	public int getApproxMaxSequenceLength() {
		return maxSequenceLength;
	}
	
	
	public void setMaxSequeceLengthRecalculte() {
		recalculateMaxSequenceLength = true;
	}
	

	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#renameSequence(int, java.lang.String)
	 */
	@Override
	public String renameSequence(int sequenceID, String newSequenceName) {
		if (isSequencesReadOnly()) {
			throw new AlignmentSourceNotWritableException(this);
		}
		else if (containsSequence(sequenceIDByName(newSequenceName))) {
			throw new DuplicateSequenceNameException(this, newSequenceName);
		}
		else {
		  String sequenceName = sequenceNameByID(sequenceID);
		  if (sequenceName == null) {
		  	throw new SequenceNotFoundException(this);
		  }
		  else {
		  	getIDByNameMap().remove(sequenceName);
		  	getIDByNameMap().put(newSequenceName, sequenceID);
		  	getNameByIDMap().put(sequenceID, newSequenceName);
		  	doRenameSequence(sequenceID, newSequenceName);
				fireAfterSequenceChange(new SequenceChangeEvent<T>(this, sequenceID, ListChangeType.DELETION));
		  }
			return sequenceName;
		}
	}

	
	@Override
	public Collection<SequenceDataChangeListener> getChangeListeners() {
		return changeListeners;
	}


	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterSequenceChange(SequenceChangeEvent<T> e) {
		setMaxSequeceLengthRecalculte();
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceChange(e);
		}
	}


	/**
	 * Informs all listeners that a sequence has been renamed.
	 */
	protected void fireAfterSequenceRenamed(SequenceRenamedEvent<T> e) {
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceRenamed(e);
		}
	}
	

	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterTokenChange(TokenChangeEvent<T> e) {
		setMaxSequeceLengthRecalculte();
		Iterator<SequenceDataChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterTokenChange(e);
		}
	}
}
