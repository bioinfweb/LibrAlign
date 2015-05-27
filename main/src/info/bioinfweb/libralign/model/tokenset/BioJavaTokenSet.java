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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.collections.CollectionUtils;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;



/**
 * Implementation of {@link TokenSet} containing shared functionality between classes adopting the 
 * contents of a BioJava {@link CompoundSet} object.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * 
 * @param <C> - the compound type of the compounds contained in this set
 */
public class BioJavaTokenSet<C extends Compound> extends AbstractSet<C> implements TokenSet<C> {
	private CompoundSet<C> compoundSet;
	private boolean spaceForGap;
	
	
	/**
	 * A constructor used to clone instances of this class.
	 * 
	 * @param tokenSet - the instance to be cloned
	 */
	public BioJavaTokenSet(BioJavaTokenSet<C> tokenSet) {
		super();
		this.compoundSet = tokenSet.compoundSet;
		this.spaceForGap = tokenSet.spaceForGap;
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The contents of {@code compoundSet} are copied to the new instance and the key character map of 
	 * {@link AbstractTokenSet} is filled with the first character of the return values of
	 * {@link Compound#getShortName()} from every compound. Clear the contents {@link #getKeyMap()} 
	 * afterwards, if you do not want this mapping.
	 * 
	 * @param compoundSet - the BioJava compound set containing the compounds to be copied into the new instance
	 */
	public BioJavaTokenSet(CompoundSet<C> compoundSet, boolean spaceForGap) {
		super();
		this.compoundSet = compoundSet;
		this.spaceForGap = spaceForGap;
	}


	/**
	 * Returns the BioJava compound set that is viewed by this instance.
	 * 
	 * @return the underlying compound set instance
	 */
	public CompoundSet<C> getCompoundSet() {
		return compoundSet;
	}


	@Override
	public C tokenByKeyChar(char key) {
		Iterator<C> iterator = iterator();
		while (iterator.hasNext()) {
			C token = iterator.next();
			if (representationByToken(token).charAt(0) == key) {
				return token;
			}
		}
		if ((spaceForGap) && (key == ' ')) {
			return tokenByKeyChar(AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER);
		}
		return null;
	}


	@Override
	public boolean isContinuous() {
		return false;
	}


	/**
	 * Returns an unmodifiable iterator of the underlying BioJava compound set.
	 * <p>
	 * Note that this iterator will return the elements of this set as they were when this method
	 * if called. If the set is modified later on, the iterator will not reflect such changes and 
	 * will not throw a {@link ConcurrentModificationException}.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<C> iterator() {
		return CollectionUtils.unmodifiableIterator(compoundSet.getAllCompounds().iterator());  // The BioJava implementation return a new array list and its iterator would allow removing elements without modifying the compound set.
	}


	@Override
	public int size() {
		return compoundSet.getAllCompounds().size();  // BioJava creates a new ArrayList every time this method is called.
	}


	@Override
	public String representationByToken(C token) {
		return token.getShortName();  //TODO Does this return the same as AbstractSequence.getBase()? Does this also return codons? 
	}

	
	@Override
	public C tokenByRepresentation(String representation) {
		return compoundSet.getCompoundForString(representation);
	}


	@Override
	public int maxRepresentationLength() {
		return TokenSetTools.maxRepresentationLength(this);
	}


	@Override
	public boolean representationLengthEqual() {
		return TokenSetTools.representationLengthEqual(this);
	}


	@Override
	public String descriptionByToken(C token) {
		return token.getDescription();
	}


	/**
	 * Clones this instance.
	 * 
	 * @return a deep copy of this object
	 */
	@Override
	public BioJavaTokenSet<C> clone() {
		return new BioJavaTokenSet<C>(this);
	}
}
