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
package info.bioinfweb.libralign.dataarea.implementations.consensus;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;

import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.dataarea.ModelBasedDataArea;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * A data area that displays the consensus sequence of an alignment.
 *
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class ConsensusSequenceArea extends ModelBasedDataArea<ConsensusSequenceModel> {
	public static final float DEFAULT_HEIGHT_FACTOR = 3f;

	
	/**
	 * Creates a new instance of this class that uses the sequence data provider of the specified
	 * alignment area.
	 *
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param model the model providing the data to be displayed by this area
	 * @throws IllegalArgumentException if {@code owner} or {@code model} is {@code null}
	 */
	public ConsensusSequenceArea(AlignmentContentArea owner, ConsensusSequenceModel model) {
		this(owner, owner.getOwner(), model);
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
	 * @param model the model providing the data to be displayed by this area
	 * @throws IllegalArgumentException if {@code owner} or {@code model} is {@code null}
	 */
	public ConsensusSequenceArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea, ConsensusSequenceModel model) {
		super(owner, labeledAlignmentArea, model);
		
		model.addModelListener(new ConsensusSequenceModelListener() {
			@Override
			public void afterConsensusUpdated(GenericEventObject<ConsensusSequenceModel> event) {
				assignSize();
				repaint();
			}
		});
	}

	
	@Override
	public double getHeight() {
		return DEFAULT_HEIGHT_FACTOR * getLabeledAlignmentArea().getPaintSettings().getTokenHeight();
	}


	@Override
	public void paintPart(AlignmentPaintEvent event) {
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
			for (FractionInfo fraction : getModel().getFractions(column)) {
				// Paint background:
				g.setColor(painter.getColor(fraction.getRepresentation()));
				double height = getHeight() * fraction.getFraction();
				Rectangle2D area = new Rectangle2D.Double(x, y, width, height); 
				g.fill(area);
				
				// Paint token representation:
				Font font = FontCalculator.getInstance().fontToFitRectangle(area, SingleColorTokenPainter.FONT_SIZE_FACTOR, 
						fraction.getRepresentation(), Font.SANS_SERIF, Font.PLAIN, SingleColorTokenPainter.MIN_FONT_SIZE);
				if (font != null) {
					g.setColor(Color.BLACK);  //TODO parameterize?
					g.setFont(font);
					GraphicsUtils.drawStringInRectangle(g, area, fraction.getRepresentation());
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
	public Set<DataListType> validLocations() {
		return EnumSet.of(DataListType.TOP, DataListType.BOTTOM);
	}
}
