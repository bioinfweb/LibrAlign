/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.collections.CollectionUtils;

import java.awt.event.KeyEvent;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.KeyStroke;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;



/**
 * Implementation of {@link TokenSet} backed by a <i>BioJava</i> {@link CompoundSet} object.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.biojava3
 * 
 * @param <C> - the compound type of the compounds contained in this set
 */
public class BioJava3TokenSet<C extends Compound> extends AbstractSet<C> implements TokenSet<C> {
	private CompoundSet<C> compoundSet;
	private boolean spaceForGap;
	private CharacterStateSetType type;
	
	
	/**
	 * A constructor used to clone instances of this class.
	 * 
	 * @param tokenSet - the instance to be cloned
	 */
	public BioJava3TokenSet(BioJava3TokenSet<C> tokenSet) {
		super();
		this.type = tokenSet.type;
		this.compoundSet = tokenSet.compoundSet;
		this.spaceForGap = tokenSet.spaceForGap;
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 * @param compoundSet the <i>BioJava</i> compound set containing the compounds to be copied into the new instance
	 * @param spaceForGap determines whether the space key shall be associated with gap symbol
	 * @throws NullPointerException if {@code type} or {@code compoundSet} are {@code null} 
	 */
	public BioJava3TokenSet(CharacterStateSetType type, CompoundSet<C> compoundSet, boolean spaceForGap) {
		super();
		if (type == null) {
			throw new NullPointerException("The character state set type must not be null.");
		}
		else if (compoundSet == null) {
			throw new NullPointerException("The compound set must not be null.");
		}
		else {
			this.type = type;
			this.compoundSet = compoundSet;
			this.spaceForGap = spaceForGap;
		}
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
	public C tokenByKeyStroke(KeyStroke key) {  //TODO Possibly use key map again, like in AbstractTokenSet. (This implementation is temporary.)
		Iterator<C> iterator = iterator();
		while (iterator.hasNext()) {
			C token = iterator.next();
			if (representationByToken(token).charAt(0) == key.getKeyChar()) {
				return token;
			}
		}
		if ((spaceForGap) && (key.getKeyCode() == KeyEvent.VK_SPACE)) {
			return getGapToken();
		}
		return null;
	}


	/**
	 * Returns an unmodifiable iterator of the underlying <i>BioJava</i> compound set.
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


	@Override
	public CharacterStateSetType getType() {
		return type;
	}


	@Override	
	public boolean isGapToken(C token) {
		return TokenSetTools.isGapToken(this, token);
	}
	
	
	/**
	 * Returns the compound associated with {@link AbstractTokenSet#DEFAULT_GAP_REPRESENTATION} from the underlying
	 * BioJava token set using {@link CompoundSet#getCompoundForString(String)}.
	 * 
	 * @return the gap token or {@code null} if the underlying <i>BioJava</i> token set does contain a representation for
	 *         {@link AbstractTokenSet#DEFAULT_GAP_REPRESENTATION}
	 * @see info.bioinfweb.libralign.model.tokenset.TokenSet#getGapToken()
	 */
	@Override
	public C getGapToken() {
		return compoundSet.getCompoundForString(Character.toString(SequenceUtils.GAP_CHAR));
	}


	@Override
	public boolean isMissingInformationToken(C token) {
		return TokenSetTools.isMissingInformationToken(this, token);
	}


	@Override
	public C getMissingInformationToken() {
		return compoundSet.getCompoundForString(Character.toString(SequenceUtils.MISSING_DATA_CHAR));
	}


	@Override
	public CharacterSymbolType getSymbolType(C token) {
		if ((token instanceof NucleotideCompound) && ((NucleotideCompound)token).isAmbiguous()) {  // AminoAcidCompound does not specify an according method.
			return CharacterSymbolType.UNCERTAIN;
		}
		else {
			return TokenSetTools.getSymbolType(this, token);
		}
	}


	@Override
	public CharacterSymbolMeaning getMeaning(C token) {
		return TokenSetTools.getMeaning(this, token);
	}


	@Override
	public boolean isSpaceForGap() {
		return spaceForGap;
	}


	@Override
	public void setSpaceForGap(boolean spaceForGap) {
		this.spaceForGap = spaceForGap;
	}


	/**
	 * Clones this instance.
	 * 
	 * @return a deep copy of this object
	 */
	@Override
	public BioJava3TokenSet<C> clone() {
		return new BioJava3TokenSet<C>(this);
	}
}
