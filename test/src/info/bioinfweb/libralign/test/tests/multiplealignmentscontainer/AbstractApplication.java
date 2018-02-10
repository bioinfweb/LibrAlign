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
package info.bioinfweb.libralign.test.tests.multiplealignmentscontainer;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.ConsensusSequenceArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;

import java.awt.Color;
import java.io.File;

import org.biojava.bio.chromatogram.ChromatogramFactory;



/**
 * This class provides implementations that are used by both the Swing and the SWT demo applications.
 * 
 * @author Ben St&ouml;ver
 */
public class AbstractApplication {
	private CharSetArea createCharSetArea(AlignmentContentArea owner, AlignmentArea labeledArea) {
		CharSetDataModel model = new CharSetDataModel();
		
		CharSet charSet = new CharSet("A", Color.RED);
		charSet.add(2, 5);
		charSet.add(8, 20);
		model.put("id1", charSet);
		
		charSet = new CharSet("B", Color.GREEN.darker());
		charSet.add(1, 20);
		model.put("id2", charSet);
		
		charSet = new CharSet("C", Color.BLUE);
		charSet.add(7, 12);
		charSet.add(17, 25);
		model.put("id3", charSet);
		
		return new CharSetArea(owner, labeledArea, model);
	}
	
	
	protected MultipleAlignmentsContainer createAlignmentsContainer() {
		try {
			MultipleAlignmentsContainer result = new MultipleAlignmentsContainer();
			
			// Create models:
			BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data" + SystemUtils.FILE_SEPARATOR + "pherograms" + SystemUtils.FILE_SEPARATOR + "Test_qualityScore.scf")));
			
			AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newDNAInstance(false));
			String id = model.addSequence("Sequence 1");
			//model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG", 
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATC"
					+ "GTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCG"
					+ "TAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGT"
					+ "AGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGA", 
					model.getTokenSet()));
			id = model.addSequence("Sequence 2");
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("AT-GTTG",	model.getTokenSet()));
			id = model.addSequence("Sequence 3");
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("AT-GTAG",	model.getTokenSet()));
			
			StringBuffer seqBuffer = new StringBuffer(pherogramProvider.getSequenceLength());
			for (int i = 0; i < pherogramProvider.getSequenceLength(); i++) {
				seqBuffer.append(pherogramProvider.getBaseCall(i));
			}
			id = model.addSequence("Sequence 4");
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("-----" + seqBuffer.substring(0, 38) + 
					seqBuffer.substring(39, 49) + "-A" + seqBuffer.substring(49),	model.getTokenSet()));  // One A is deleted for shift change specified below.

			id = model.addSequence("Sequence 5");
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ATCGTAGATCGTAGATGGTAGATCGTAGATCGT---TCGTAGATCGTAG",	
					model.getTokenSet()));
			

			// Create alignment areas:
			AlignmentArea mainArea = new AlignmentArea(result);  // Needs to be created first to act as a reference for data areas.
			
			// Index:
			AlignmentArea area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(false);
			area.getDataAreas().getTopAreas().add(new SequenceIndexArea(area.getContentArea(), mainArea));
			result.getAlignmentAreas().add(area);
			
			// Char sets:
			area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(true);
			area.getDataAreas().getTopAreas().add(createCharSetArea(area.getContentArea(), mainArea));
			result.getAlignmentAreas().add(area);
      
			// Alignment with pherograms:
			mainArea.setAllowVerticalScrolling(true);
      			
			mainArea.setAlignmentModel(model, false);
			mainArea.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());
			
			PherogramArea pherogramArea = new PherogramArea(mainArea.getContentArea(), new PherogramAreaModel(pherogramProvider));
			mainArea.getDataAreas().getSequenceAreas(model.sequenceIDsByName("Sequence 4").iterator().next()).add(pherogramArea);
			pherogramArea.getModel().setFirstSeqPos(5);
			pherogramArea.getModel().setLeftCutPosition(34);
			pherogramArea.getModel().setRightCutPosition(820);
			pherogramArea.getModel().setShiftChange(38, -1);
			pherogramArea.getModel().setShiftChange(49, 2);
			
			result.getAlignmentAreas().add(mainArea);
			
			// Additional alignment with longer names (to test of other label areas adopt their width):
			model = new PackedAlignmentModel<Character>(CharacterTokenSet.newDNAInstance(false));
			id = model.addSequence("Another Sequence");
			model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG", 
					model.getTokenSet()));

			area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(false);      			
			area.setAlignmentModel(model, false);
			area.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());
			result.getAlignmentAreas().add(area);
			
			// Consensus sequence:
      area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(false);
			area.getDataAreas().getBottomAreas().add(new ConsensusSequenceArea(area.getContentArea(), mainArea));
      result.getAlignmentAreas().add(area);
			
//      result.addKeyListener(new TICKeyListener() {
//				@Override
//				public boolean keyReleased(TICKeyEvent e) {
//					System.out.println("released " + e.getKeyCode());
//					return false;
//				}
//				
//				@Override
//				public boolean keyPressed(TICKeyEvent e) {
//					System.out.println("pressed " + e.getKeyCode());
//					return false;
//				}
//			});
      
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
