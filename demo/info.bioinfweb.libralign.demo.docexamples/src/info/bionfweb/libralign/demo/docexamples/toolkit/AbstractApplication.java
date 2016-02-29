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
package info.bionfweb.libralign.demo.docexamples.toolkit;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



public class AbstractApplication {
	protected MultipleAlignmentsContainer createContainer() {
		// Create components:
		MultipleAlignmentsContainer result = new MultipleAlignmentsContainer();
		AlignmentArea headArea = new AlignmentArea(result);
		AlignmentArea mainArea = new AlignmentArea(result);
		
		// Prepare heaing area:
		headArea.getDataAreas().getTopAreas().add(new SequenceIndexArea(headArea.getContentArea(), mainArea));
		result.getAlignmentAreas().add(headArea);
		
		// Create model:
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance());
		int id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("AT-CG", model.getTokenSet()));

		// Prepare main area:
		mainArea.setAlignmentModel(model, false);  // Define a model
		mainArea.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());  // Define how sequences shall be painted
		result.getAlignmentAreas().add(mainArea);
		
		return result;
	}
}
