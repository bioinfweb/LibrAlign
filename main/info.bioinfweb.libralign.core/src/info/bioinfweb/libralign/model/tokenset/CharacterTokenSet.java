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


import java.util.Set;

import info.bioinfweb.commons.bio.CharacterStateSetType;
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
	public CharacterTokenSet(CharacterStateSetType type) {
		super(type);
	}


	/**
	 * Creates a new instance of this class where the initial tokens are determined
	 * by the characters in the specified character sequence.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 * @param tokens - a sequence containing the tokens to be contained in this set 
	 */
	public CharacterTokenSet(CharacterStateSetType type, CharSequence tokens) {
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
	
	
	private static CharacterTokenSet createNucleotideInstance(CharacterStateSetType type, boolean distinguishCase) {
		CharacterTokenSet result = new CharacterTokenSet(type);
		result.addAll(SequenceUtils.getNucleotideCharacters());
		if (CharacterStateSetType.DNA.equals(type)) {
			result.remove('U');
		}
		else if (CharacterStateSetType.RNA.equals(type)) {
			result.remove('T');
		}
		
		if (distinguishCase) {
			Character[] tokens = result.toArray(new Character[result.size()]);
			for (int i = 0; i < tokens.length; i++) {
				result.add(Character.toLowerCase(tokens[i]));
			}
		}
		
		result.add(SequenceUtils.GAP_CHAR);
		result.add(SequenceUtils.MISSING_DATA_CHAR);
		return result;
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including both 
	 * {@code 'T'} and {@code 'U'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newNucleotideInstance(boolean distinguishCase) {
		return createNucleotideInstance(CharacterStateSetType.NUCLEOTIDE, distinguishCase);
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including  
	 * {@code 'T'} and not {@code 'U'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newDNAInstance(boolean distinguishCase) {
		return createNucleotideInstance(CharacterStateSetType.DNA, distinguishCase);
	}
	
	
	/**
	 * Returns a new instance of this class containing all nucleotide tokens, including  
	 * {@code 'U'} and not {@code 'T'}, the gap token ({@code '-'}) and all IUPAC ambiguity codes.
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newRNAInstance(boolean distinguishCase) {
		return createNucleotideInstance(CharacterStateSetType.RNA, distinguishCase);
	}
	
	
	/**
	 * Returns a new instance of this class containing all amino acid tokens (one letter codes), 
	 * including the gap token ({@code '-'}) and ambiguity codes.
	 * <p>
	 * The returned instance can also be used to take up molecular character data from a stream, 
	 * where the character type (amino acid or nucleotide) is not known, because all nucleotide
	 * characters and ambiguity codes are also contained in the amino acid token set. (That is 
	 * not the case the other way around.)
	 * 
	 * @return the new instance
	 */
	public static CharacterTokenSet newAminoAcidInstance(boolean distinguishCase) {
		CharacterTokenSet result = new CharacterTokenSet(CharacterStateSetType.AMINO_ACID);
		Set<Character> tokens = SequenceUtils.getAminoAcidOneLetterCodes(true);
		result.addAll(tokens);
		if (distinguishCase) {
			for (Character token : tokens) {
				result.add(Character.toLowerCase(token));
			}
		}
		result.add(SequenceUtils.GAP_CHAR);
		result.add(SequenceUtils.MISSING_DATA_CHAR);
		return result;
	}
	
	
	/**
	 * Returns the character itself.
	 * 
	 * @return a string consisting only of the character {@code token}
	 */
	@Override
	public String representationByToken(Character token) {
		if ((token != null) && contains(token)) {
			return token.toString();
		}
		else {
			return "";
		}
	}

	
	/**
	 * Returns the first character of {@code representation} if that character is contained in this set.
	 * 
	 * @param representation the string representation of the token (This would usually be the token itself in this class.)
	 * @return the token or {@code null} if no according token is contained in this set
	 * @see info.bioinfweb.libralign.model.tokenset.TokenSet#tokenByRepresentation(java.lang.String)
	 */
	@Override
	public Character tokenByRepresentation(String representation) {
		char c = representation.charAt(0);
		if (contains(c)) {
			return c;
		}
		else {
			return null;
		}
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
