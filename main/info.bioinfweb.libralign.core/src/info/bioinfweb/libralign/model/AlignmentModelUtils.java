/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model;


import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.tokenset.AbstractTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * Provider general tool methods to be used with instances of {@link AlignmentModel}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class AlignmentModelUtils {
	/**
	 * Converts a character sequence to a sequence of tokens. Each character in {@code sequence} is considered
	 * to be a valid token representation in the specified set.#
	 * <p>
	 * Note that {@link CharSequenceTokenScanner} is a more powerful alternative to this method that also allows
	 * longer token representations and handled invalid representations.
	 * 
	 * @param sequence the character sequence to be converted
	 * @param tokenSet the token set used to create token objects
	 * @return the sequence of tokens created from {@code sequence}
	 * @throws IllegalArgumentException if a character in {@code sequence} is not a valid token representation
	 *         in {@code tokenSet}
	 * @see CharSequenceTokenScanner
	 */
	public static <T> List<T> charSequenceToTokenList(CharSequence sequence, TokenSet<T> tokenSet) {
		List<T> result = new ArrayList<T>(sequence.length());
		for (int i = 0; i < sequence.length(); i++) {
			T token = tokenSet.tokenByRepresentation(Character.toString(sequence.charAt(i)));
			if (token == null) {
				throw new IllegalArgumentException("There was no token for the string representation \"" + sequence.charAt(i) + 
						"\" found in the specified token set.");
			}
			else {
				result.add(token);
			}
		}
		return result;
	}
	
	
	/**
	 * Reverse complements a part of a nucleotide sequence. Nucleotide tokens need to have a one-character 
	 * string representation, in order to be recognized by this method. Non-nucleotide tokens and all tokens
	 * with a string representation with a length different from 1 are left unchanged in the reverted sequence.
	 * 
	 * @param model the alignment model containing the sequence
	 * @param sequenceID the ID of the sequence to be reverse complemented
	 * @param start the first position of the subsequence to be reverse complemented
	 * @param end the first position behind the subsequence to be reverse complemented
	 * @see PherogramArea#reverseComplement()
	 */
	public static <T> void reverseComplement(AlignmentModel<T> model, int sequenceID, int start, int end) {
		TokenSet<T> tokenSet = model.getTokenSet();
		if (tokenSet.getType().isNucleotide()) {
			Collection<T> tokens = new PackedObjectArrayList<T>(tokenSet.size(), end - start); 
			for (int column = end - 1; column >= start; column--) {
				String representation = tokenSet.representationByToken(model.getTokenAt(sequenceID, column));
				if (representation.length() == 1) {  // Tokens longer than one character are always considered to be non-nucleotide tokens.
					representation = SequenceUtils.complement(representation);
				}
				tokens.add(tokenSet.tokenByRepresentation(representation));
			}			
			model.setTokensAt(sequenceID, start, tokens);
		}
		else {
			throw new IllegalArgumentException("The specified model must contain nucleotide data to be reverse complemented.");
		}
	}

	
	/**
	 * Reverse complements a whole nucleotide sequence.
	 * 
	 * @param model the alignment model containing the sequence
	 * @param sequenceID the ID of the sequence to be reverse complemented
	 */
	public static void reverseComplement(AlignmentModel<?> model, int sequenceID) {
		reverseComplement(model, sequenceID, 0, model.getSequenceLength(sequenceID));
	}
	
	
	/**
	 * Converts the current selection to a {@link String}.
	 * 
	 * @param area the alignment area containing the according alignment and selection model
	 * @param separateBySpace Specify {@code true} here, if single token representations shall be separated by a
	 *        space character, or {@code false} otherwise. ({@code true} should usually be specified, if token
	 *        representations can be longer than one character.)
	 * @return the string representation of the current selection or an empty string if nothing is selected
	 */
	public static String selectionAsString(AlignmentArea area, boolean separateBySpace) {
  	SelectionModel selection = area.getSelection();
  	if (!selection.isEmpty()) {
  		StringBuilder selectedCharacters = new StringBuilder();
  		AlignmentModel<?> alignmentModel = area.getAlignmentModel();
  		for (int row = selection.getFirstRow(); row <= selection.getLastRow(); row++) {
  			int id = area.getSequenceOrder().idByIndex(row);
  			for (int column = selection.getFirstColumn(); column <= selection.getLastColumn(); column++) {
  				if (alignmentModel.getSequenceLength(id) > column) {
  					selectedCharacters.append(alignmentModel.getTokenAt(id, column));
  				}
  				else {  // Add gaps if selection is behind the end of the sequence.
  					selectedCharacters.append(AbstractTokenSet.DEFAULT_GAP_REPRESENTATION);
  				}
  				
  				if (separateBySpace && (row < selection.getLastRow())) {
  					selectedCharacters.append(' ');
  				}
  			}
  			if (row < selection.getLastRow()) {
  				selectedCharacters.append(System.getProperty("line.separator"));
      	}
  		}
  		return selectedCharacters.toString();
  	}
  	return "";
	}
}
