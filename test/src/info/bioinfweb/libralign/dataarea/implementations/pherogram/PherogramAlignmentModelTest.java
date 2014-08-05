/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import static org.junit.Assert.* ;


import info.bioinfweb.libralign.pherogram.BioJavaPherogramProvider;

import java.io.File;
import java.io.IOException;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.junit.* ;



public class PherogramAlignmentModelTest {
	/**
	 * Tests with the following alignment:
	 * <pre>
	 *                               1         2           3         4         5         6
	 * Sequence index:     012345678901234567890123456  78901234567890123456789012345678901234
	 * Editable sequence:           XXXXXX--XXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * Base call sequence:      ccccXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXcccc  (c: cut off positions)
	 * Base call index:         0123456789  01234567890123456789012345678901234567890123456789
	 *                                      1         2         3         4         5
	 * </pre>
	 */
	@Test
	public void test_editableIndexByBaseCallIndex() {
		try {
			PherogramArea pherogramArea = new PherogramArea(null, new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramArea.setFirstSeqPos(10);
			pherogramArea.setLeftCutPosition(5);
			pherogramArea.setRightCutPosition(55);
			
			PherogramAlignmentModel model = new PherogramAlignmentModel(pherogramArea);
		  model.setShiftChange(10, 2);
		  model.setShiftChange(20, -2);
		  
		  assertEquals(10, model.editableIndexByBaseCallIndex(5));
		  assertEquals(14, model.editableIndexByBaseCallIndex(9));
		  assertEquals(17, model.editableIndexByBaseCallIndex(10));
		  assertEquals(18, model.editableIndexByBaseCallIndex(11));

		  assertEquals(25, model.editableIndexByBaseCallIndex(18));
		  assertEquals(26, model.editableIndexByBaseCallIndex(19));
		  assertEquals(PherogramAlignmentModel.GAP, model.editableIndexByBaseCallIndex(20));
		  assertEquals(PherogramAlignmentModel.GAP, model.editableIndexByBaseCallIndex(21));
		  assertEquals(27, model.editableIndexByBaseCallIndex(22));
		  assertEquals(28, model.editableIndexByBaseCallIndex(23));

		  assertEquals(PherogramAlignmentModel.OUT_OF_RANGE, model.editableIndexByBaseCallIndex(4));
		  assertEquals(PherogramAlignmentModel.OUT_OF_RANGE, model.editableIndexByBaseCallIndex(56));
		}
		catch (IOException | UnsupportedChromatogramFormatException e) {
			fail(e.getMessage());
		}
	}
}
