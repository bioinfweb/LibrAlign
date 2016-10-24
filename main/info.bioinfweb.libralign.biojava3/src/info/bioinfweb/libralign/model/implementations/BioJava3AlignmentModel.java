/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
import java.util.Map;

import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.template.AbstractSequence;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.model.SequenceAccessAlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Provides alignment source data from BioJava {@link Sequence} objects.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @bioinfweb.module info.bioinfweb.libralign.biojava3
 *
 * @param <C> - the compound type used by the underlying sequence object
 */
public class BioJava3AlignmentModel<S extends Sequence<C>, C extends Compound>
    extends AbstractAlignmentModel<C> implements SequenceAccessAlignmentModel<S, C> {

	public static final String DEFAULT_SEQUENCE_NAME_PREFIX = "Sequence";
	
	
	private Alignment<S, C> alignment;
	
	
	private void setMapEntries() {
		Iterator<String> iterator = alignment.nameIterator();
		while (iterator.hasNext()) {
			getIDManager().addSequenceName(iterator.next());
		}
	}
	
	
	/**
	 * Creates a new instance of this class based on the contents of an {@link Alignment} object.
	 * 
	 * @param tokenSet - the set of allowed tokens in the sequences
	 * @param alignment - the object containing the source data
	 */
	public BioJava3AlignmentModel(TokenSet<C> tokenSet, Alignment<S, C> alignment) {
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
	

	/**
	 * Creates a new instance of this class based on the contents of a {@link Profile} object.
	 * 
	 * @param tokenSet - the set of allowed tokens in the sequences
	 * @param profile - the object containing the source data
	 */
	public BioJava3AlignmentModel(TokenSet<C> tokenSet, Profile<S, C> profile) {
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


	/**
	 * Creates a new instance of this class based on the contents of a {@link Map} object.
	 * 
	 * @param tokenSet - the set of allowed tokens in the sequences
	 * @param map - the object containing the source data
	 */
	public BioJava3AlignmentModel(TokenSet<C> tokenSet, Map<String, S> map) {
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
	public boolean containsSequence(String sequenceID) {
		String name = getIDManager().sequenceNameByID(sequenceID);
		return (name != null) && alignment.containsName(name);
	}


	@Override
	public C getTokenAt(String sequenceID, int elementIndex) {
		return alignment.getSequence(sequenceNameByID(sequenceID)).getCompoundAt(elementIndex + 1);  // BioJava indices start with 1.
	}


	@Override
	public void setTokenAt(String sequenceID, int elementIndex, C token)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(String sequenceID, int elementIndex, C token)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(String sequenceID, int elementIndex)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(String sequenceID, int beginIndex,	
			Collection<? extends C> tokens) throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex,
			Collection<? extends C> tokens) throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public int getSequenceCount() {
		return alignment.size();
	}


	@Override
	public Iterator<String> sequenceIDIterator() {
		final Iterator<String> nameIterator = alignment.nameIterator();
		return new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return nameIterator.hasNext();
			}

			@Override
			public String next() {
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
	public int getSequenceLength(String sequenceID) {
		return alignment.getSequence(sequenceNameByID(sequenceID)).getLength();
	}


	@Override
	public int getMaxSequenceLength() {
		return alignment.maxSequenceLength();
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return AlignmentModelWriteType.SEQUENCES_ONLY;
	}


	@Override
	public S getSequence(String sequenceID) {
		return alignment.getSequence(sequenceNameByID(sequenceID));
	}


	@Override
	public String addSequence(String sequenceName, S content) {
		String id = addSequence(sequenceName);
		alignment.replace(sequenceName, content);
		return id;
	}


	@Override
	public S replaceSequence(String sequenceID, S content) {
		S result = getSequence(sequenceID);
		alignment.replace(sequenceNameByID(sequenceID), content);
		return result;
	}


	@Override
	protected void doAddSequence(String sequenceID, String sequenceName) {
		alignment.add(sequenceName, (S)new DNASequence());  // null cannot be added here, because SimpleAlignment.replace() does not work than. 
		//TODO In later versions an empty instance should be created with a BioJava factory here. The current code is only a temporary solution.
	}


	@Override
	protected void doRemoveSequence(String sequenceID) {
		alignment.remove(sequenceNameByID(sequenceID));
	}


	@Override
	protected void doRenameSequence(String sequenceID, String newSequenceName) {
		alignment.renameSequence(sequenceNameByID(sequenceID), newSequenceName);
	}
}
