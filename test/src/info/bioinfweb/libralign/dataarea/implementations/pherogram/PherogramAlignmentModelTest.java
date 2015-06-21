/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.junit.* ;



/**
 * Tests {@link PherogramAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramAlignmentModelTest {
	/**
	 * Tests with the following alignment:
	 * <pre>
	 *                              1         2           3         4         5         6
	 * Sequence index:     12345678901234567890123456  78901234567890123456789012345678901234
	 * Editable sequence:           XXXXX--XXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * Base call sequence:      ccccXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXcccc  (c: cut off positions)
	 * Base call index:         123456789  01234567890123456789012345678901234567890123456789
	 *                                     1         2         3         4         5
	 * </pre>
	 */
	@Test
	public void test_editableIndexByBaseCallIndex() {
		try {
			AlignmentContentArea alignmentContentArea = new AlignmentArea().getContentArea();
			PherogramArea pherogramArea = new PherogramArea(alignmentContentArea, new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramArea.setFirstSeqPos(10);
			pherogramArea.setLeftCutPosition(5);
			pherogramArea.setRightCutPosition(55);
			
			PherogramAlignmentModel model = new PherogramAlignmentModel(pherogramArea);
		  model.setShiftChange(10, 2);
		  model.setShiftChange(20, -2);
		  
		  assertEquals(10, model.editableIndexByBaseCallIndex(5).getCorresponding());
		  assertEquals(14, model.editableIndexByBaseCallIndex(9).getCorresponding());
		  assertEquals(17, model.editableIndexByBaseCallIndex(10).getCorresponding());
		  assertEquals(18, model.editableIndexByBaseCallIndex(11).getCorresponding());

		  assertEquals(25, model.editableIndexByBaseCallIndex(18).getCorresponding());
		  assertEquals(26, model.editableIndexByBaseCallIndex(19).getCorresponding());
		  
		  PherogramAlignmentRelation relation = model.editableIndexByBaseCallIndex(20);
		  assertEquals(26, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.GAP, relation.getCorresponding());
		  assertEquals(27, relation.getAfter());
		  
		  relation = model.editableIndexByBaseCallIndex(21);
		  assertEquals(26, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.GAP, relation.getCorresponding());
		  assertEquals(27, relation.getAfter());
		  
		  assertEquals(27, model.editableIndexByBaseCallIndex(22).getCorresponding());
		  assertEquals(28, model.editableIndexByBaseCallIndex(23).getCorresponding());

		  relation = model.editableIndexByBaseCallIndex(0);
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getCorresponding());
		  assertEquals(6, relation.getAfter());

		  relation = model.editableIndexByBaseCallIndex(851);
		  assertEquals(855, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getCorresponding());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getAfter());
		}
		catch (IOException | UnsupportedChromatogramFormatException e) {
			fail(e.getMessage());
		}
	}


	/**
	 * Tests with the following alignment:
	 * <pre>
	 *                              1         2           3         4         5         6
	 * Sequence index:     12345678901234567890123456  78901234567890123456789012345678901234
	 * Editable sequence:           XXXXX--XXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * Base call sequence:      ccccXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXcccc  (c: cut off positions)
	 * Base call index:         012345678  90123456789012345678901234567890123456789012345678
	 *                                      1         2         3         4         5
	 * </pre>
	 */
	@Test
	public void test_baseCallIndexByEditableIndex() {
		try {
			AlignmentContentArea alignmentContentArea = new AlignmentArea().getContentArea();
			PherogramArea pherogramArea = new PherogramArea(alignmentContentArea, new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramArea.setFirstSeqPos(10);
			pherogramArea.setLeftCutPosition(4);
			pherogramArea.setRightCutPosition(54);
			
			PherogramAlignmentModel model = new PherogramAlignmentModel(pherogramArea);
		  model.setShiftChange(9, 2);
		  model.setShiftChange(19, -2);
		  
		  assertEquals(4, model.baseCallIndexByEditableIndex(10).getCorresponding());
		  assertEquals(8, model.baseCallIndexByEditableIndex(14).getCorresponding());
		  
		  PherogramAlignmentRelation relation = model.baseCallIndexByEditableIndex(15);
		  assertEquals(8, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.GAP, relation.getCorresponding());
		  assertEquals(9, relation.getAfter());
		  
		  relation = model.baseCallIndexByEditableIndex(16);
		  assertEquals(8, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.GAP, relation.getCorresponding());
		  assertEquals(9, relation.getAfter());
		  
		  assertEquals(9, model.baseCallIndexByEditableIndex(17).getCorresponding());
		  assertEquals(10, model.baseCallIndexByEditableIndex(18).getCorresponding());

		  assertEquals(17, model.baseCallIndexByEditableIndex(25).getCorresponding());
		  assertEquals(18, model.baseCallIndexByEditableIndex(26).getCorresponding());
		  assertEquals(21, model.baseCallIndexByEditableIndex(27).getCorresponding());
		  assertEquals(22, model.baseCallIndexByEditableIndex(28).getCorresponding());

		  relation = model.baseCallIndexByEditableIndex(5);
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getCorresponding());
		  assertEquals(0, relation.getAfter());
		  assertEquals(0, model.baseCallIndexByEditableIndex(6).getCorresponding());
		  
		  assertEquals(849, model.baseCallIndexByEditableIndex(855).getCorresponding());
		  relation = model.baseCallIndexByEditableIndex(856);
		  assertEquals(849, relation.getBefore());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getCorresponding());
		  assertEquals(PherogramAlignmentRelation.OUT_OF_RANGE, relation.getAfter());  // The source pherogram base call sequence has a length of 850.
		}
		catch (IOException | UnsupportedChromatogramFormatException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void test_shiftChangeIteratorByBaseCallIndex() {
		try {
			AlignmentContentArea alignmentContentArea = new AlignmentArea().getContentArea();
			PherogramArea pherogramArea = new PherogramArea(alignmentContentArea, new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramArea.setFirstSeqPos(10);
			pherogramArea.setLeftCutPosition(5);
			pherogramArea.setRightCutPosition(55);
			
			PherogramAlignmentModel model = new PherogramAlignmentModel(pherogramArea);
		  model.setShiftChange(10, 2);
		  model.setShiftChange(20, -2);
		  
		  ListIterator<ShiftChange> iterator = model.shiftChangeIteratorByBaseCallIndex(0);
		  assertEquals(10, iterator.next().getBaseCallIndex());
		  assertEquals(20, iterator.next().getBaseCallIndex());
		  assertFalse(iterator.hasNext());
		  
		  iterator = model.shiftChangeIteratorByBaseCallIndex(10);
		  assertEquals(10, iterator.next().getBaseCallIndex());
		  assertEquals(20, iterator.next().getBaseCallIndex());
		  assertFalse(iterator.hasNext());
		  
		  iterator = model.shiftChangeIteratorByBaseCallIndex(11);
		  assertEquals(20, iterator.next().getBaseCallIndex());
		  assertFalse(iterator.hasNext());
		  
		  iterator = model.shiftChangeIteratorByBaseCallIndex(20);
		  assertEquals(20, iterator.next().getBaseCallIndex());
		  assertFalse(iterator.hasNext());
		  
		  iterator = model.shiftChangeIteratorByBaseCallIndex(21);
		  assertFalse(iterator.hasNext());
		}
		catch (IOException | UnsupportedChromatogramFormatException e) {
			fail(e.getMessage());
		}		  
	}
	
	
	@Test
	public void test_shiftAtBaseCallIndex() {
		try {
			AlignmentContentArea alignmentContentArea = new AlignmentArea().getContentArea();
			PherogramArea pherogramArea = new PherogramArea(alignmentContentArea, new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramArea.setFirstSeqPos(10);
			pherogramArea.setLeftCutPosition(5);
			pherogramArea.setRightCutPosition(55);
			
			PherogramAlignmentModel model = new PherogramAlignmentModel(pherogramArea);
		  model.setShiftChange(10, 2);
		  model.setShiftChange(20, -2);
		  
		  assertEquals(0, model.shiftAtBaseCallIndex(1));
		  assertEquals(0, model.shiftAtBaseCallIndex(9));
		  assertEquals(2, model.shiftAtBaseCallIndex(10));
		  assertEquals(2, model.shiftAtBaseCallIndex(19));
		  assertEquals(0, model.shiftAtBaseCallIndex(20));
		  assertEquals(0, model.shiftAtBaseCallIndex(21));
		}
		catch (IOException | UnsupportedChromatogramFormatException e) {
			fail(e.getMessage());
		}		  
	}
}
