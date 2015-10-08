/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bionfweb.libralign.demo.docexamples;


import java.util.Iterator;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;



/**
 * Example that demonstrates adding sequences and tokens to and reading them from alignment models.
 * <p>
 * This example is used in the 
 * <a href="http://bioinfweb.info/LibrAlign/Documentation/wiki/Alignment_and_data_models">LibrAlign documentation</a>.
 * 
 * @author Ben St&ouml;ver
 */
public class CreateAlignmentModel {
	public static void main(String[] args) {
		// Create new model instance:
		AlignmentModel<Character> model = new PackedAlignmentModel(CharacterTokenSet.newDNAInstance());
		
		// Add one sequence and add single tokens:
		int id = model.addSequence("Seq1");
		model.appendToken(id, 'A');
		model.appendToken(id, 'T');
		model.appendToken(id, 'C');
		model.appendToken(id, 'G');
		
		// Add another sequence and add a list of tokens:
		id = model.addSequence("Seq2");		
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("AT-G", model.getTokenSet()));
		
		
		// Test output of alignment:
		Iterator<Integer> idIterator = model.sequenceIDIterator();
		while (idIterator.hasNext()) {
			id = idIterator.next();
			
			// Print sequence name:
			System.out.print(model.sequenceNameByID(id) + "\t");
			
			// Print tokens (nucleotides):
			for (int column = 0; column < model.getSequenceLength(id); column++) {
				System.out.print(model.getTokenAt(id, column));
			}
			System.out.println();
		}
	}
}
