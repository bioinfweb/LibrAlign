/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations;


import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;



/**
 * A simple data area implementation that displays one line of text.
 * 
 * @author Ben St&ouml;ver
 * @since 0.7.0
 */
public class LabelDataArea extends DataArea {
	/** The factor between the component height and the space left before the start of the text. */
	private static final double LEFT_DISTANCE_FACTOR = 0.1; 
	
	
	private String text;
	private Font font;
	private Color textColor = SystemColor.menuText;
	private Color backgroundColor = SystemColor.menu;
	private boolean alignToScrollPosition;
	private boolean alignToFirstColumn;
	private boolean isRepainting = false;
	
	
	public LabelDataArea(AlignmentContentArea owner, AlignmentArea labeledArea) {
		this(owner, labeledArea, "");
	}
	
	
	public LabelDataArea(AlignmentContentArea owner, AlignmentArea labeledArea, String text) {
		this(owner, labeledArea, text, false, false);
	}
	
	
	public LabelDataArea(AlignmentContentArea owner, AlignmentArea labeledArea, String text, boolean alignToFirstColumn, 
			boolean alignToScrollPosition) {
		
		super(owner, labeledArea);
		this.text = text;
		this.alignToFirstColumn = alignToFirstColumn;
		this.alignToScrollPosition = alignToScrollPosition;
		font = getLabeledAlignmentArea().getPaintSettings().getTokenHeightFont();
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text must not be null.");
		}
		else if (!this.text.equals(text)) {
			this.text = text;
			repaint();
		}
	}


	/**
	 * Returns the font used to display the text in this data area.
	 * <p>
	 * Note that the size of the returned font applies to 100 % zoom.
	 * 
	 * @return the font object for 100 % zoom
	 */
	public Font getFont() {
		return font;
	}


	public void setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("font must not be null.");
		}
		else if (!this.font.equals(font)) {
			this.font = font;
			getOwner().getOwner().assignSizeToAll();  // A changed font height would mean a changes component height.
			repaint();
		}
	}


	/**
	 * Determines whether the start of the text of this component should move if the component is scrolled
	 * horizontally.
	 * 
	 * @return {@code true} if the start of the text moves within the component to stay visible during scrolling
	 *         or {@code false} if it remains at a fixed position
	 */
	public boolean isAlignToScrollPosition() {
		return alignToScrollPosition;
	}


	public void setAlignToScrollPosition(boolean alignToScrollPosition) {
		if (this.alignToScrollPosition != alignToScrollPosition) {
			this.alignToScrollPosition = alignToScrollPosition;
			repaint();
		}
	}


	/**
	 * Determines whether the start of the text is displayed at the left end of the component or at the left 
	 * end of the first column of the alignment. (These two positions are different if data areas occupy space
	 * left of the alignment.)
	 * <p>
	 * Note that the text may start even further right, if {@link #isAlignToScrollPosition()} is set too, 
	 * depending on the scroll position.
	 * 
	 * @return {@code true} if the text should start above the first column or {@code false} if it always 
	 *         starts at the left of the component
	 */
	public boolean isAlignToFirstColumn() {
		return alignToFirstColumn;
	}


	public void setAlignToFirstColumn(boolean alignToFirstColumn) {
		if (this.alignToFirstColumn != alignToFirstColumn) {
			this.alignToFirstColumn = alignToFirstColumn;
			repaint();
		}
	}


	public Color getTextColor() {
		return textColor;
	}


	public void setTextColor(Color textColor) {
		if (textColor == null) {
			throw new IllegalArgumentException("textColor must not be null.");
		}
		else if (!this.textColor.equals(textColor)) {
			this.textColor = textColor;
			repaint();
		}
	}


	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		if (backgroundColor == null) {
			throw new IllegalArgumentException("backgroundColor must not be null.");
		}
		else if (!this.backgroundColor.equals(backgroundColor)) {
			this.backgroundColor = backgroundColor;
			repaint();
		}
	}


	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.SEQUENCE, DataAreaListType.BOTTOM);
	}

	
	private double calculateHorizontalShift() {
		double result = 0;
		if (isAlignToFirstColumn()) {
			result = getOwner().getOwner().getSizeManager().getGlobalMaxLengthBeforeStart();  // Considers the current zoom factor.
		}
		if (isAlignToScrollPosition()) {
			result = Math.max(result, getOwner().getOwner().getVisibleRectangle().getMinX());
		}
		return result;
	}
	
	
	@Override
	public void paintPart(AlignmentPaintEvent e) {
		if (!isRepainting && isAlignToScrollPosition()) {
			isRepainting = true;
			repaint();  // Make sure the whole visible component is repainted, since otherwise parts of the moved text may be missing. (Does not repaint now but queues the event for later processing.)
		}
		else {
			try {
				Graphics2D g = e.getGraphics();
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		    // Paint background:
				g.setColor(getBackgroundColor());
				g.fill(e.getRectangle());
				
				// Paint text:
				g.setColor(getTextColor());
				Font font = getOwner().getOwner().getPaintSettings().zoomFont(getFont());
				g.setFont(font);
				g.drawString(getText(), (float)(calculateHorizontalShift() + LEFT_DISTANCE_FACTOR * getHeight()), 
						FontCalculator.getInstance().getAscent(font));
			}
			finally {
				isRepainting = false;
			}
		}
	}


	@Override
	public double getHeight() {
		return getLabeledAlignmentArea().getPaintSettings().getZoomY() * FontCalculator.getInstance().getTextHeightByFontSize(getFont());
		//TODO Adjust factor and declare as constant.
	}
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {}
	

	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}
	

	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {}

	
	@Override
	public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {}
}
