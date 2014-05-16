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
package info.bioinfweb.libralign.sequenceprovider.implementations;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.template.AbstractSequence;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;



/**
 * Provides alignment source data from BioJava {@link Sequence} objects.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 *
 * @param <C> - the compound type used by the underlying sequence object
 */
public class BioJavaSequenceDataProvider<S extends Sequence<C>, C extends Compound>
    extends AbstractSequenceDataProvider<C> implements SequenceDataProvider<C> {

	//TODO Also create a version that implements SequenceObjectDataProvider for unmodifiable BioJava sequences.
	
	public static final String DEFAULT_SEQUENCE_NAME_PREFIX = "Sequence";
	
	
	private Alignment<S, C> alignment;
	
	
	private void setMapEntries() {
		Iterator<String> iterator = alignment.nameIterator();
		while (iterator.hasNext()) {
			String sequenceName = iterator.next();
			int sequenceID = createNewID();
			getIDByNameMap().put(sequenceName, sequenceID);
			getNameByIDMap().put(sequenceID, sequenceName);
		}
	}
	
	
	public BioJavaSequenceDataProvider(TokenSet<C> tokenSet, Alignment<S, C> alignment) {
		super(tokenSet);
		this.alignment = alignment;
		setMapEntries();
	}

	
	private String createNewSequenceName() {
		int index = 1;
		String name;
		do {
			name = DEFAULT_SEQUENCE_NAME_PREFIX + index;
			index++;
		} while (alignment.containsName(name));
		return name;
	}
	

	public BioJavaSequenceDataProvider(TokenSet<C> tokenSet, Profile<S, C> profile) {
		super(tokenSet);
		
		alignment = new SimpleAlignment<S, C>();
		for (int i = 0; i < profile.getSize(); i++) {
			String name;
			if (profile.getAlignedSequence(i).getOriginalSequence() instanceof AbstractSequence) {
				name = ((AbstractSequence)profile.getAlignedSequence(i).getOriginalSequence()).getOriginalHeader();
				//TODO Is there a more general way to obtain sequence names from BioJava?
			}
			else {
				name = createNewSequenceName();
			}
			alignment.add(name, (S)profile.getAlignedSequence(i)); 
		}
		setMapEntries();
	}


	public BioJavaSequenceDataProvider(TokenSet<C> tokenSet, Map<String, S> map) {
		super(tokenSet);
		
		alignment = new SimpleAlignment<S, C>();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next(); 
			alignment.add(name, map.get(name));
		}
		setMapEntries();
	}


	@Override
	public C getTokenAt(int sequenceID, int elementIndex) {
		return alignment.getSequence(sequenceNameByID(sequenceID)).getCompoundAt(elementIndex + 1);  // BioJava indices start with 1.
	}


	@Override
	public void setTokenAt(int sequenceID, int elementIndex, C token)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(int sequenceID, int elementIndex, C token)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(int sequenceID, int elementIndex)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(int sequenceID, int beginIndex,	
			Collection<? extends C> tokens) throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(int sequenceID, int beginIndex,
			Collection<? extends C> tokens) throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public int getSequenceCount() {
		return alignment.size();
	}


	@Override
	public Iterator<Integer> sequenceIDIterator() {
		final Iterator<String> nameIterator = alignment.nameIterator();
		return new Iterator<Integer>() {
			@Override
			public boolean hasNext() {
				return nameIterator.hasNext();
			}

			@Override
			public Integer next() {
				return sequenceIDByName(nameIterator.next());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();  // Currently done, because this class specified read only.
				//TODO enable operation as soon as more specific read only properties are available, which allow to edit only sequences and not tokens
			}
		};				
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		return alignment.getSequence(sequenceNameByID(sequenceID)).getLength();
	}


	@Override
	public int getMaxSequenceLength() {
		return alignment.maxSequenceLength();
	}


	@Override
	public SequenceDataProviderWriteType getWriteType() {
		return SequenceDataProviderWriteType.NONE;
	}


	@Override
	protected void doAddSequence(int sequenceID, String sequenceName) {}  //TODO Implement when a way of creating new sequences independent of their type is known.


	@Override
	protected void doRemoveSequence(int sequenceID) {}  //TODO Implement when a way of creating new sequences independent of their type is known.


	@Override
	protected void doRenameSequence(int sequenceID, String newSequenceName) {}  //TODO Implement when a way of creating new sequences independent of their type is known.
}
