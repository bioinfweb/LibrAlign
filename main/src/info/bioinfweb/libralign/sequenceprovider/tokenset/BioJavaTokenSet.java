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
package info.bioinfweb.libralign.sequenceprovider.tokenset;


import java.util.Iterator;
import java.util.Map;

import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;



/**
 * Implementation of {@link TokenSet} that adopts the contents of a BioJava {@link CompoundSet} object.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <C> - the compound type of the compounds contained in this set
 */
public class BioJavaTokenSet<C extends Compound> extends AbstractTokenSet<C> implements TokenSet<C> {
	/**
	 * A constructor used to clone instances of this class.
	 * 
	 * @param tokenSet - the instance to be cloned
	 */
	public BioJavaTokenSet(BioJavaTokenSet<C> tokenSet) {
		super();
		addAll(tokenSet);
		getKeyMap().putAll(tokenSet.getKeyMap());
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The contents of {@code compoundSet} are copied to the new instance and the key character map of 
	 * {@link AbstractTokenSet} is filled with the first character of the return values of #
	 * {@link Compound#getShortName()} from every compound. Clear the contents {@link #getKeyMap()} 
	 * afterwards, if you do not want this mapping.
	 * 
	 * @param compoundSet - the BioJava compound set containing the compounds to be copied into the new instance
	 */
	public BioJavaTokenSet(CompoundSet<C> compoundSet) {
		super();
		
		addAll(compoundSet.getAllCompounds());
		Iterator<C> iterator = iterator();
		while (iterator.hasNext()) {
			C compound = iterator.next();
			getKeyMap().put(representationByToken(compound).charAt(0), compound);
		}
	}


	@Override
	public Map<Character, C> getKeyMap() {
		return super.getKeyMap();
	}


	@Override
	public String representationByToken(C token) {
		return token.getShortName();  //TODO Does this return the same as AbstractSequence.getBase()? Does this also return codons? 
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
	 * @return a deep copy of this object.
	 */
	@Override
	public BioJavaTokenSet<C> clone() {
		return new BioJavaTokenSet<C>(this);
	}
}
