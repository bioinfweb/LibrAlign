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


import info.bioinfweb.commons.bio.CharacterStateType;
import info.bioinfweb.commons.bio.SequenceUtils;



/**
 * A token set consisting of characters.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class CharacterTokenSet extends AbstractTokenSet<Character> {
	/**
	 * Creates a new empty instance of this class.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 */
	public CharacterTokenSet(CharacterStateType type) {
		super(type);
	}


	/**
	 * Creates a new instance of this class where the initial tokens are determined
	 * by the characters in the specified character sequence.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 * @param tokens - a sequence containing the tokens to be contained in this set 
	 */
	public CharacterTokenSet(CharacterStateType type, CharSequence tokens) {
		super(type);
		for (int i = 0; i < tokens.length(); i++) {
			add(tokens.charAt(i));
		}
	}


	/**
	 * A constructor used to clone instances of this class.
	 * 
	 * @param tokenSet - the instance to be cloned
	 */
	public CharacterTokenSet(CharacterTokenSet tokenSet) {
		super(tokenSet.getType());
		addAll(tokenSet);
		getKeyMap().putAll(tokenSet.getKeyMap());
	}
	
	
	private static CharacterTokenSet createNucleotideInstance(CharacterStateType type) {
		CharacterTokenSet result = new CharacterTokenSet(type);
		result.addAll(SequenceUtils.getNucleotideCharacters());
		result.add(DEFAULT_GAP_REPRESENTATION);
		return result;
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including both 
	 * {@code 'T'} and {@code 'U'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newNucleotideInstance() {
		return createNucleotideInstance(CharacterStateType.NUCLEOTIDE);
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including  
	 * {@code 'T'} and not {@code 'U'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newDNAInstance() {
		CharacterTokenSet result = createNucleotideInstance(CharacterStateType.DNA);
		result.remove('U');
		return result;
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including  
	 * {@code 'U'} and not {@code 'T'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newRNAInstance() {
		CharacterTokenSet result = createNucleotideInstance(CharacterStateType.DNA);
		result.remove('T');
		return result;
	}
	
	
	/**
	 * Returns a new instance of this class containing all amino acid tokens (one letter codes), 
	 * including the gap token ({@code '-'}) and ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newAminoAcidInstance() {
		CharacterTokenSet result = new CharacterTokenSet(CharacterStateType.AMINO_ACID);
		result.addAll(SequenceUtils.getAminoAcidOneLetterCodes(true));
		result.add(DEFAULT_GAP_REPRESENTATION);
		return result;
	}
	
	
	/**
	 * Return the character itself.
	 * 
	 * @return a string consisting only of the character {@code token}
	 */
	@Override
	public String representationByToken(Character token) {
		return token.toString();
	}

	
	/**
	 * Just returns the first character of {@code representation}.
	 * 
	 * @param representation the string representation of the token (This would usually be the token itself in this class.)
	 * @see info.bioinfweb.libralign.model.tokenset.TokenSet#tokenByRepresentation(java.lang.String)
	 */
	@Override
	public Character tokenByRepresentation(String representation) {
		return representation.charAt(0);
	}


	/**
	 * Always returns one. Overwrite this method if you overwrite {@link #representationByToken(Character)}.
	 * 
	 * @return {@code 1}
	 */
	@Override
	public int maxRepresentationLength() {
		return 1;
	}

	
	/**
	 * Always returns {@code true}. Overwrite this method if you overwrite 
	 * {@link #representationByToken(Character)}.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean representationLengthEqual() {
		return true;
	}

	
	/**
	 * In this default implementation the description is equal the character itself. Overwrite
	 * this method if you want to provide a more detailed description.
	 * 
	 * @return a string consisting only of the character {@code token}
	 */
	@Override
	public String descriptionByToken(Character token) {
		return token.toString();
	}

	
	/**
	 * Clones this instance.
	 * 
	 * @return a deep copy of this object
	 */
	@Override
	public CharacterTokenSet clone() {
		return new CharacterTokenSet(this);
	}
}
