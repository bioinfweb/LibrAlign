/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



/**
 * A data area that displays the consensus sequence of an alignment.
 *
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class ConsensusSequenceArea extends DataArea {
	public static final float DEFAULT_HEIGHT_FACTOR = 3f;

	
	private static class FractionInfo {
		public String representation;
		public double fraction;
		
		public FractionInfo(String representation, double fraction) {
			super();
			this.representation = representation;
			this.fraction = fraction;
		}
	}
	

	private final Map<Integer, List<FractionInfo>> fractionsMap;


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
	 * @param labeledAlignmentArea the alignment area containing the alignment model that shall provide the
	 *        source data for the consensus sequence (Should only be different from {@code owner.getOwner()}
	 *        if the new instance will be placed in a different alignment area than the sequence data in a
	 *        scenario with a {@link MultipleAlignmentsContainer}.)
	 * @throws IllegalArgumentException if {@code sequenceDataProvider} is {@code null}
	 */
	public ConsensusSequenceArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea) {
		super(owner, labeledAlignmentArea);
		fractionsMap = new TreeMap<Integer, List<FractionInfo>>();  // Saves scores between change events of the provider.
		assignSize();
	}

	
	private <T> String getRepresentation(AlignmentModel<T> model, String sequenceID, int column) {
		if (model.getSequenceLength(sequenceID) > column) {  // Sequences may have different lengths.
			T token = model.getTokenAt(sequenceID, column);
			if (!model.getTokenSet().isGapToken(token)) {
				return model.getTokenSet().representationByToken(token);
			}
		}
		return Character.toString(SequenceUtils.GAP_CHAR);  //TODO Is the missing data token also relevant here?
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<FractionInfo> getFractions(int column) {
		List<FractionInfo> fractions = fractionsMap.get(column);
		if (fractions == null) {
			fractions = new ArrayList<ConsensusSequenceArea.FractionInfo>();
			
			AlignmentModel model = getLabeledAlignmentModel();
			TokenSet tokenSet = model.getTokenSet();
			Iterator<String> iterator = model.sequenceIDIterator();
			if (tokenSet.getType().isNucleotide() || tokenSet.getType().equals(CharacterStateSetType.AMINO_ACID)) {
				Map<Character, Double> frequencies;
				
				if (tokenSet.getType().isNucleotide()) {
					char[] tokens = new char[model.getSequenceCount()];
					int row = 0;
					while (iterator.hasNext()) {
						tokens[row] =	getRepresentation(model, iterator.next(), column).charAt(0);
						row++;
					}
					frequencies = SequenceUtils.nucleotideFrequencies(tokens);
				}
				else {  // Amino acid
					String[] tokens = new String[model.getSequenceCount()];
					int row = 0;
					while (iterator.hasNext()) {
						tokens[row] =	getRepresentation(model, iterator.next(), column);
						row++;
					}
					frequencies = SequenceUtils.aminoAcidFrequencies(tokens);
				}
				
				for (Character c : frequencies.keySet()) {
					fractions.add(new FractionInfo(c.toString(), frequencies.get(c)));
				}
			}
			else {  //TODO Implement special treatment (e.g. calculating the mean value) for discrete values one day?
		  	Map<String, Double> frequencies = new TreeMap<String, Double>();
		  	double sum = 0.0;
		  	while (iterator.hasNext()) {
					String sequenceID = iterator.next();
					if (model.getSequenceLength(sequenceID) > column) {  // Sequences may have different lengths.
						Object token = model.getTokenAt(sequenceID, column);
						if (!tokenSet.isGapToken(token)) {
							String representation = tokenSet.representationByToken(token);
							Double frequency = frequencies.get(representation);
							if (frequency == null) {
								frequency = 0.0;
							}
							frequencies.put(representation, frequency + 1);
							sum += 1;
						}
					}
		  	}
		  	
		  	for (String representation : frequencies.keySet()) {
					fractions.add(new FractionInfo(representation, frequencies.get(representation) / sum));
				}
			}
			
			fractionsMap.put(column, fractions);
		}
		return fractions;
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

		// Paint output:
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  	double x = getLabeledAlignmentArea().getContentArea().paintXByColumn(firstIndex);
		for (int column = firstIndex; column <= lastIndex; column++) {
			// Paint bars:
			TokenPainter painter = getLabeledAlignmentArea().getPaintSettings().getTokenPainterList().painterByColumn(column);
			double y = 0;
			double width = getLabeledAlignmentArea().getPaintSettings().getTokenWidth(column);
			List<FractionInfo> factions = getFractions(column);
			for (FractionInfo fraction : factions) {
				// Paint background:
				g.setColor(painter.getColor(fraction.representation));
				double height = getHeight() * fraction.fraction;
				Rectangle2D area = new Rectangle2D.Double(x, y, width, height); 
				g.fill(area);
				
				// Paint token representation:
				Font font = FontCalculator.getInstance().fontToFitRectangle(area, SingleColorTokenPainter.FONT_SIZE_FACTOR, 
						fraction.representation, Font.SANS_SERIF, Font.PLAIN, SingleColorTokenPainter.MIN_FONT_SIZE);
				if (font != null) {
					g.setColor(Color.BLACK);  //TODO parameterize?
					g.setFont(font);
					GraphicsUtils.drawStringInRectangle(g, area, fraction.representation);
				}
				
				y += height;
			}

			// Paint token:

//			SequenceArea.paintCompound(getOwner().getOwner(), event.getGraphics(),
//					compoundSet.getCompoundForString("" + score.getConsensusBase()), x, sequenceY, false);

	    x += width;
    }
	}


	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM);
	}
	
	
	/**
	 * Returns the string representation of the most frequent token at the specified column.
	 * It the specified column contains no tokens, the gap representation is returned.
	 * 
	 * @return the string representation of a token from {@link #getLabeledAlignmentModel()}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public String getConsensusToken(int column) {
		List<FractionInfo> fractions = getFractions(column);
		TokenSet tokenSet = getLabeledAlignmentModel().getTokenSet();
  	String result = tokenSet.representationByToken(tokenSet.getGapToken());  // This way makes sure that a custom token representation is used. 
  	double max = -1;
  	for (FractionInfo fraction : fractions) {
			if (fraction.fraction > max) {
				result = fraction.representation;
				max = fraction.fraction;
			}
		}
  	return result;
	}


	private void refreshConsensus() {
		fractionsMap.clear();  // Each score is recalculated the next time it is requested.
		assignSize();
		repaint();
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
