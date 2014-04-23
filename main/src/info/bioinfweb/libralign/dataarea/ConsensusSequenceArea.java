/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.dataarea;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.bio.AmbiguityBaseScore;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.SequenceArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;



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
	private int height;
	
	
	public ConsensusSequenceArea(AlignmentArea owner) {
		super(owner);
		createMap();
		scores = new TreeMap<Integer, AmbiguityBaseScore>();  // Saves scores between change events of the provider.
		height = Math.round(DEFAULT_HEIGHT_FACTOR * owner.getCompoundHeight());
		owner.getSequenceProvider().getChangeListeners().add(new SequenceDataChangeListener() {
					@Override
					public void afterTokenChange(TokenChangeEvent e) {
						scores.clear();  // Each scores is recalculated the next time it is requested. 
					}
					
					@Override
					public void afterSequenceRenamed(SequenceRenamedEvent e) {}
					
					@Override
					public void afterSequenceChange(SequenceChangeEvent e) {
						scores.clear();
					}
					
					@Override
					public void afterProviderChanged(SequenceDataProvider previous,	SequenceDataProvider current) {
						scores.clear();
					}
				});
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
			if (getOwner().hasSequenceProvider()) {
				SequenceDataProvider provider = getOwner().getSequenceProvider();
				Iterator<Integer> iterator = provider.sequenceIDIterator();
				while (iterator.hasNext()) {
					int id = iterator.next();
					if (provider.getSequenceLength(id) > column) {
						AmbiguityBaseScore addend = mapByBase.get(((NucleotideCompound)provider.getTokenAt(id, column)).getBase());
						if (addend != null) {
							score.add(addend);
						}
					}
				}
				score.rescale(1);
			}
		}
		return score;
	}
	
	
	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
		//TODO send height change
		repaint();
	}


	@Override
	public void paint(TICPaintEvent event) {
		int firstIndex = Math.max(0, getOwner().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getOwner().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getOwner().getSequenceProvider().getMaxSequenceLength() - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
		Map<String, Color> map = getOwner().getColorSchema().getNucleotideColorMap();
    Color[] bgColors = new Color[]{map.get("A"), map.get("T"), map.get("C"), map.get("G")};
    
		AlignmentAmbiguityNucleotideCompoundSet compoundSet =  
				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet();
		Graphics2D g = event.getGraphics();
  	float x = firstIndex * getOwner().getCompoundWidth();
		float sequenceY = 2 * getOwner().getCompoundHeight();
		final float barWidth = getOwner().getCompoundWidth() / 4; 
		for (int i = firstIndex; i <= lastIndex; i++) {
			float barX = x;
			AmbiguityBaseScore score = getScore(i);
			for (int j = 0; j < 4; j++) {
				float barHeight = sequenceY * (float)score.getScoreByIndex(j);
				g.setColor(bgColors[j]);
				g.fill(new Rectangle2D.Float(barX, sequenceY - barHeight, barWidth, barHeight));
				barX += barWidth;
			}
			
			SequenceArea.paintCompound(getOwner(), event.getGraphics(), 
					compoundSet.getCompoundForString("" + score.getConsensusBase()), x, sequenceY, false);	
	    x += getOwner().getCompoundWidth();
    }
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(
				getOwner().getCompoundWidth() * getOwner().getSequenceProvider().getMaxSequenceLength(), 
				getHeight());
	}
}