/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.test.tests.empty;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



public class AbstractEmptyAlignmentTest {
	protected MultipleAlignmentsContainer createContainer() {
		MultipleAlignmentsContainer result = new MultipleAlignmentsContainer();
		AlignmentArea labelArea = new AlignmentArea(result);
		AlignmentArea sequenceArea = new AlignmentArea(result);
		
		labelArea.getDataAreas().getTopAreas().add(new SequenceIndexArea(labelArea.getContentArea(), sequenceArea));
		result.getAlignmentAreas().add(labelArea);
		
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false)); 
		sequenceArea.setAlignmentModel(model, false);
		sequenceArea.getAlignmentModel().addSequence("A");
		//model.appendToken(model.sequenceIDByName("A"), 'A');
		result.getAlignmentAreas().add(sequenceArea);
		
		sequenceArea.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());
		
		return result;
	}
}
