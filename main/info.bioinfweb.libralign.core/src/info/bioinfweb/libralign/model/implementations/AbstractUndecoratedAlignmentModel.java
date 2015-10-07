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
package info.bioinfweb.libralign.model.implementations;


import java.util.Collection;
import java.util.Iterator;

import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Implements general functionality for alignment models that are not decorators of other models.
 * 
 * @author Ben St&uml;ver
 * @since 0.4.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractUndecoratedAlignmentModel<T> extends AbstractAlignmentModel<T> {
	private SequenceIDManager idManager = new SequenceIDManager();
	private TokenSet<T> tokenSet;
	private int maxSequenceLength = 0;
	private boolean recalculateMaxSequenceLength = true; 

	
	/**
	 * Creates a new instance of this class with its own instance of {@link SequenceIDManager}.
	 * 
	 * @param tokenSet - the set of allowed tokens in the sequences of the implementing class
	 */
	public AbstractUndecoratedAlignmentModel(TokenSet<T> tokenSet) {
		super();
		this.tokenSet =  tokenSet;
	}
	

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tokenSet the set of allowed tokens in the sequences of the implementing class
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 */
	public AbstractUndecoratedAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager) {
		super();
		this.idManager = idManager;
		this.tokenSet = tokenSet;
	}


	@Override
	public TokenSet<T> getTokenSet() {
		return tokenSet;
	}


	@Override
	public void setTokenSet(TokenSet<T> set) {
		tokenSet = set;
	}


	protected SequenceIDManager getIDManager() {
		return idManager;
	}


	@Override
	public boolean isTokensReadOnly() {
		return getWriteType().equals(AlignmentModelWriteType.SEQUENCES_ONLY) ||
				getWriteType().equals(AlignmentModelWriteType.NONE);
	}


	@Override
	public boolean isSequencesReadOnly() {
		return getWriteType().equals(AlignmentModelWriteType.TOKENS_ONLY) ||
				getWriteType().equals(AlignmentModelWriteType.NONE);
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceNameByID(int)
	 */
	@Override
  public String sequenceNameByID(int sequenceID) {
		if (containsSequence(sequenceID)) {
			return getIDManager().sequenceNameByID(sequenceID);
		}
		else {
			return null;
		}
  }
  
  
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider#sequenceIDByName(java.lang.String)
	 */
	@Override
	public int sequenceIDByName(String sequenceName) {
		Integer result = getIDManager().sequenceIDByName(sequenceName);
		if ((result == null) || !containsSequence(result)) {
			return NO_SEQUENCE_FOUND;
		}
		else {
			return result;
		}
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
			int sequenceID = getIDManager().addSequenceName(sequenceName);
			doAddSequence(sequenceID, sequenceName);
			fireAfterSequenceChange(SequenceChangeEvent.newInsertInstance(this, sequenceID));
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
				doRemoveSequence(sequenceID);
				fireAfterSequenceChange(SequenceChangeEvent.newRemoveInstance(this, sequenceID));
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
		else {
	  	String oldSequenceName = getIDManager().renameSequence(sequenceID, newSequenceName, this);
	  	doRenameSequence(sequenceID, newSequenceName);
			fireAfterSequenceChange(SequenceChangeEvent.newRemoveInstance(this, sequenceID));
			return oldSequenceName;
		}
	}

	
	@Override
	public void appendToken(int sequenceID, T token) throws AlignmentSourceNotWritableException {
		insertTokenAt(sequenceID, getSequenceLength(sequenceID), token);
	}


	@Override
	public void appendTokens(int sequenceID, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException {
		insertTokensAt(sequenceID, getSequenceLength(sequenceID), tokens);
	}


	@Override
	protected void fireAfterSequenceChange(SequenceChangeEvent<T> e) {
		setMaxSequeceLengthRecalculte();
		super.fireAfterSequenceChange(e);
	}


	@Override
	protected void fireAfterTokenChange(TokenChangeEvent<T> e) {
		setMaxSequeceLengthRecalculte();
		super.fireAfterTokenChange(e);
	}
}
