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
package info.bioinfweb.libralign.dataarea.implementations;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.bio.AmbiguityBaseScore;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * A data area that displays the consensus sequence of an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class ConsensusSequenceArea extends DataArea {
	public static final float DEFAULT_HEIGHT_FACTOR = 3f;
	
	
	private TreeMap<String, AmbiguityBaseScore> mapByBase;
	private Map<Integer, AmbiguityBaseScore> scores;
	
	
	/**
	 * Creates a new instance of this class that uses the sequence data provider of the specified
	 * alignment area.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @throws IllegalArgumentException if {@code owner} does not have a sequence data provider
	 */
	public ConsensusSequenceArea(AlignmentContentArea owner) {
		this(owner, owner.getOwner());
	}
	
	
	/**
	 * Creates a new instance of this class that uses the specified sequence data provider.
	 * <p>
	 * Note that this instance will not react to calls of 
	 * {@link #afterProviderChanged(AlignmentModel, AlignmentModel)} when this constructor
	 * is used. Such changes would than have to be done manually by the application code using 
	 * {@link #setAlignmentModel(AlignmentModel)}.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param labeledAlignmentArea the alignment area containg the alignment model that shall provide the 
	 *        source data for the consensus sequence (Should only be different from {@code owner.getOwner()} 
	 *        if the new instance will be placed in a different alignment area than the sequence data in a 
	 *        scenario with a {@link MultipleAlignmentsContainer}.) 
	 * @throws IllegalArgumentException if {@code sequenceDataProvider} is {@code null}
	 */
	public ConsensusSequenceArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea) {
		super(owner, labeledAlignmentArea);
		createMap();
		scores = new TreeMap<Integer, AmbiguityBaseScore>();  // Saves scores between change events of the provider.
		assignSize();
	}

	
	private void createMap() {  //TODO Use ConsensusSequenceCreator instead when implementation is adjusted.
		mapByBase = new TreeMap<String, AmbiguityBaseScore>();
		mapByBase.put("A", new AmbiguityBaseScore(1, 0, 0, 0));
		mapByBase.put("T", new AmbiguityBaseScore(0, 1, 0, 0));
		mapByBase.put("C", new AmbiguityBaseScore(0, 0, 1, 0));
		mapByBase.put("G", new AmbiguityBaseScore(0, 0, 0, 1));
		mapByBase.put("Y", new AmbiguityBaseScore(0, 1, 1, 0, 1));
		mapByBase.put("R", new AmbiguityBaseScore(1, 0, 0, 1, 1));
		mapByBase.put("W", new AmbiguityBaseScore(1, 1, 0, 0, 1));
		mapByBase.put("S", new AmbiguityBaseScore(0, 0, 1, 1, 1));
		mapByBase.put("K", new AmbiguityBaseScore(0, 1, 0, 1, 1));
		mapByBase.put("M", new AmbiguityBaseScore(1, 0, 1, 0, 1));
		mapByBase.put("B", new AmbiguityBaseScore(0, 1, 1, 1, 1));
		mapByBase.put("D", new AmbiguityBaseScore(1, 1, 0, 1, 1));
		mapByBase.put("H", new AmbiguityBaseScore(1, 1, 1, 0, 1));
		mapByBase.put("V", new AmbiguityBaseScore(1, 0, 1, 1, 1));
		mapByBase.put("N", new AmbiguityBaseScore(1, 1, 1, 1, 1));
		mapByBase.put("-", new AmbiguityBaseScore(0, 0, 0, 0));
	}
	
	
	public AmbiguityBaseScore getScore(int column) {
		AmbiguityBaseScore score = scores.get(column);
		if (score == null) {
			score = new AmbiguityBaseScore(0, 0, 0, 0);
			Iterator<Integer> iterator = getLabeledAlignmentModel().sequenceIDIterator();
			while (iterator.hasNext()) {
				int id = iterator.next();
				if (getLabeledAlignmentModel().getSequenceLength(id) > column) {
					AmbiguityBaseScore addend = mapByBase.get(((NucleotideCompound)getLabeledAlignmentModel().getTokenAt(id, column)).getBase());
					if (addend != null) {
						score.add(addend);
					}
				}
			}
			score.rescale(1);
		}
		return score;
	}
	
	
	@Override
	public int getLength() {
		return getLabeledAlignmentArea().getContentArea().paintXByColumn(getLabeledAlignmentModel().getMaxSequenceLength()) 
				- getOwner().getOwner().getDataAreas().getGlobalMaxLengthBeforeStart();
	}

	
	@Override
	public int getHeight() {
		return (int)Math.round(DEFAULT_HEIGHT_FACTOR * getLabeledAlignmentArea().getPaintSettings().getTokenHeight());
	}


	@Override
	public void paint(TICPaintEvent event) {
		// Paint background:
		Graphics2D g = event.getGraphics();
		g.setColor(SystemColor.menu);
		g.fill(event.getRectangle());

		
		// Determine area to be painted:
		int firstIndex = Math.max(0, getLabeledAlignmentArea().getContentArea().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getLabeledAlignmentArea().getContentArea().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getLabeledAlignmentModel().getMaxSequenceLength() - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
		//TODO Refactor nucleotide independent:
		// Paint output:
//		Map<String, Color> map = getOwner().getOwner().getColorSchema().getNucleotideColorMap();
//    Color[] bgColors = new Color[]{map.get("A"), map.get("T"), map.get("C"), map.get("G")};
//    
//		AlignmentAmbiguityNucleotideCompoundSet compoundSet =  
//				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet();
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//  	float x = firstIndex * getOwner().getOwner().getCompoundWidth() + getOwner().getOwner().getDataAreas().getGlobalMaxLengthBeforeStart();
//		float sequenceY = getHeight() - getOwner().getOwner().getCompoundHeight();
//		final float barWidth = getOwner().getOwner().getCompoundWidth() / 4; 
//		for (int i = firstIndex; i <= lastIndex; i++) {
//			// Paint bars:
//			float barX = x;
//			AmbiguityBaseScore score = getScore(i);
//			for (int j = 0; j < 4; j++) {
//				float barHeight = sequenceY * (float)score.getScoreByIndex(j);
//				g.setColor(bgColors[j]);
//				g.fill(new Rectangle2D.Float(barX, sequenceY - barHeight, barWidth, barHeight));
//				barX += barWidth;
//			}
//			
//			// Paint token:
//			SequenceArea.paintCompound(getOwner().getOwner(), event.getGraphics(), 
//					compoundSet.getCompoundForString("" + score.getConsensusBase()), x, sequenceY, false);
//			
//	    x += getOwner().getOwner().getCompoundWidth();
//    }
	}

	
	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM);
	}
	
	
	private void refreshConsensus() {
		scores.clear();  // Each score is recalculated the next time it is requested.
		assignSize();
	}

	
	@Override
	public void afterTokenChange(TokenChangeEvent e) {
		if (e.getSource().equals(getLabeledAlignmentModel())) {  // The owner always delegates this call, also if its sequence provider is not used.
			refreshConsensus();
		}
	}
	
	
	@Override
	public void afterSequenceRenamed(SequenceRenamedEvent e) {}
	
	
	@Override
	public void afterSequenceChange(SequenceChangeEvent e) {
		if (e.getSource().equals(getLabeledAlignmentModel())) {  // The owner always delegates this call, also if its sequence provider is not used.
			refreshConsensus();
		}
	}
	
	
	@Override
	public void afterProviderChanged(AlignmentModel previous,	AlignmentModel current) {
		refreshConsensus();
	}
}
