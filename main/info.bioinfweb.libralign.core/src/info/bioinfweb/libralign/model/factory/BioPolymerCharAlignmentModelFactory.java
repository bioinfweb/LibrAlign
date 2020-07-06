/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.factory;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.AbstractUndecoratedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;



/**
 * Creates instances of {@link PackedAlignmentModel} using a {@link CharacterTokenSet} for
 * nucleotide or amino acid data.
 * <p>
 * The token set of the created models (and the maximum number of different tokens supported by
 * the packed model) depends on the character state type and predefined tokens specified in the
 * parameter map. If no such data is provided an instance supporting all amino acid one letter
 * (ambiguity) codes will be created. Such an instance can also take up all nucleotide (ambiguity)
 * codes, since all such characters are also contained in the amino acid set.
 * <p>
 * In addition to the default molecular tokens, all tokens specified in 
 * {@link NewAlignmentModelParameterMap#getDefinedTokens()} will be added to the token set. Note
 * that only the first character of each string representation will be added the set. There will
 * be no warning, if representations get cut off.
 * <p>
 * In addition the number of supported different tokens by the packed model can be increased by
 * specifying a value for {@link NewAlignmentModelParameterMap#KEY_CHARACTER_STATE_COUNT} which
 * is higher than the number of molecular tokens necessary for the specified character state set
 * type.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class BioPolymerCharAlignmentModelFactory extends AbstractAlignmentModelFactory<Character> 
		implements AlignmentModelFactory<Character> {
	
	private boolean distinguishCase;
	
	
	/**
	 * Creates a new instance of this class without an shared sequence ID manager. Each model instance,
	 * that will be created by this factory, will have its own sequence ID manager. The resulting instance will
	 * not distinguish between upper and lower case tokens and have no default token.
	 */
	public BioPolymerCharAlignmentModelFactory() {
		this(null, false);
	}

	
	/**
	 * Creates a new instance of this class without an shared sequence ID manager. Each model instance,
	 * that will be created by this factory, will have its own sequence ID manager.
	 * 
	 * @param defaultToken a default token to be used if invalid token representations are passed to 
	 *        {@link #createToken(AlignmentModel, String)} (If {@code null}
	 *        is specified here, {@link #createToken(AlignmentModel, String)} will throw an exception 
	 *        instead in such cases.)
	 * @param distinguishCase Specify {@code true} here, if upper and lower case letters (representing nucleotides
	 *        or amino acids) should be supported as different tokens by created alignment models or {@code false} 
	 *        if all token shall be converted to upper case letters in {@link #createToken(AlignmentModel, String)}.
	 */
	public BioPolymerCharAlignmentModelFactory(Character defaultToken, boolean distinguishCase) {
		super(defaultToken);
		this.distinguishCase = distinguishCase;
	}

	
	/**
	 * Creates a new instance of this class using a shared sequence ID manager.
	 * 
	 * @param defaultToken a default token to be used if invalid token representations are passed to 
	 *        {@link #createToken(AlignmentModel, String)} (If {@code null}
	 *        is specified here, {@link #createToken(AlignmentModel, String)} will throw an exception 
	 *        instead in such cases.)
	 * @param characterStateSetChooser Makes it possible to manually change the CharacterStateSetType if it was set
	 * 		  to UNKNOWN. The interface of CharacterStateSetChooser has the method chooseCharacterStateSet() which
	 * 		  by implementation returns the wanted CharacterStateSetType.)
	 * @param distinguishCase Specify {@code true} here, if upper and lower case letters (representing nucleotides
	 *        or amino acids) should be supported as different tokens by created alignment models or {@code false} 
	 *        if all token shall be converted to upper case letters in {@link #createToken(AlignmentModel, String)}.
	 */
	public BioPolymerCharAlignmentModelFactory(Character defaultToken, CharacterStateSetChooser characterStateSetChooser, boolean distinguishCase) {
		super(defaultToken, characterStateSetChooser);
		this.distinguishCase = distinguishCase;
	}
	
	
	/**
	 * Creates a new instance of this class using a shared sequence ID manager.
	 * 
	 * @param sharedIDManager the sequence ID manager that will be shared by all model instances 
	 *        created by this factory 
	 * @param reuseSequenceIDs Specifies whether unused IDs present in their underlying ID managers should be reused 
	 *        by alignment model instances created by this factory. (See the documentation of 
	 *        {@link AbstractUndecoratedAlignmentModel#isReuseSequenceIDs()} for details. Specify {@code false}, if 
	 *        you are unsure what this property does.) 
	 * @param defaultToken a default token to be used if invalid token representations are passed to 
	 *        {@link #createToken(AlignmentModel, String)} (If {@code null}
	 *        is specified here, {@link #createToken(AlignmentModel, String)} will throw an exception 
	 *        instead in such cases.)
	 * @param distinguishCase Specify {@code true} here, if upper and lower case letters (representing nucleotides
	 *        or amino acids) should be supported as different tokens by created alignment models or {@code false} 
	 *        if all token shall be converted to upper case letters in {@link #createToken(AlignmentModel, String)}.
	 * @param characterStateSetChooser Makes it possible to manually change the CharacterStateSetType if it was set
	 * 		  to UNKNOWN. The interface of CharacterStateSetChooser has the method chooseCharacterStateSet() which
	 * 		  by implementation returns the wanted CharacterStateSetType.)
	 */
	public BioPolymerCharAlignmentModelFactory(SequenceIDManager sharedIDManager, boolean reuseSequenceIDs, 
			Character defaultToken, CharacterStateSetChooser characterStateSetChooser, boolean distinguishCase) {
		
		super(sharedIDManager, reuseSequenceIDs, defaultToken, characterStateSetChooser);
		this.distinguishCase = distinguishCase;
	}

	
	/**
	 * Determines whether alignment models created by this instance should distinguish between upper and lower case
	 * letters or not.
	 * <p>
	 * This property can be set using the respective constructor parameter.
	 * 
	 * @return {@code true} if upper and lower case letters are modeled as separate tokens or {@code false} otherwise
	 */
	public boolean isDistinguishCase() {
		return distinguishCase;
	}


	@Override
	public AlignmentModel<Character> doCreateNewModel(NewAlignmentModelParameterMap parameterMap) {
	
		// Create initial set:
		CharacterTokenSet tokenSet;
		switch (parameterMap.getCharacterStateSetType()) {
			case DNA:
				tokenSet = CharacterTokenSet.newDNAInstance(isDistinguishCase());
				break;
			case RNA:
				tokenSet = CharacterTokenSet.newRNAInstance(isDistinguishCase());
				break;
			case NUCLEOTIDE:
				tokenSet = CharacterTokenSet.newNucleotideInstance(isDistinguishCase());
				break;
			//TODO What about CONTINUES?
			default:  // AMINO_ACID and UNKNOWN
				tokenSet = CharacterTokenSet.newAminoAcidInstance(isDistinguishCase());  // Also contains all nucleotide (ambiguity) codes.
				break;
		}

		// Add predefined tokens:
		for (TokenDefinition tokenDefinition : parameterMap.getDefinedTokens()) {
			String representation = tokenDefinition.getRepresentation();
			if (representation.length() > 0) {
				tokenSet.add(tokenDefinition.getRepresentation().charAt(0));  // Cuts off all representations longer than one character. CharacterTokenSet.tokenByRepresentation() does the same.
			}
			//TODO Warnings should be given, if tokens are ignored or cut.
		}
		
		// Create alignment model:
		long charStateCount = Math.max(tokenSet.size(), parameterMap.getLong(NewAlignmentModelParameterMap.KEY_CHARACTER_STATE_COUNT, 0));
		if (charStateCount > Integer.MAX_VALUE) {  //TODO This is not possible if the token type is Character!
			return new ArrayListAlignmentModel<Character>(tokenSet, getSharedIDManager(), isReuseSequenceIDs());  // null is allowed as ID manager
		}
		else {
			return new PackedAlignmentModel<Character>(tokenSet, getSharedIDManager(), isReuseSequenceIDs(), (int)charStateCount);  // null is allowed as ID manager
		}
	}


	@Override
	public Character createToken(AlignmentModel<Character> alignmentModel, String tokenRepresentation) {
		if (!isDistinguishCase()) {
			tokenRepresentation = tokenRepresentation.toUpperCase();
		}
		return super.createToken(alignmentModel, tokenRepresentation);
	}
}
