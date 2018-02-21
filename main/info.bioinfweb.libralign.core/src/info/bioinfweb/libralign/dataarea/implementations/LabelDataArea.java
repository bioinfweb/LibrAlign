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


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;

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



/**
 * A simple data area implementation that displays one line of text.
 * 
 * @author Ben St&ouml;ver
 * @since 0.7.0
 */
public class LabelDataArea extends DataArea {
	private String text;
	private Font font;
	private Color textColor = SystemColor.menuText;
	private Color backgroundColor = SystemColor.menu;
	
	
	public LabelDataArea(AlignmentContentArea owner, AlignmentArea labeledArea) {
		this(owner, labeledArea, "");
	}
	
	
	public LabelDataArea(AlignmentContentArea owner, AlignmentArea labeledArea, String text) {
		super(owner, labeledArea);
		this.text = text;
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

	
	@Override
	public void paintPart(AlignmentPaintEvent e) {
		Graphics2D g = e.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Paint background:
    Rectangle2D visibleRect = e.getRectangle();
		g.setColor(getBackgroundColor());
		g.fill(visibleRect);
		
		// Paint text:
		g.setColor(getTextColor());
		g.drawString(getText(), 3f, (float)(0.7 * getHeight()));
	}


	@Override
	public double getHeight() {
		return 1.2 * getLabeledAlignmentArea().getPaintSettings().getZoomY() * FontCalculator.getInstance().getTextHeightByFontSize(getFont());
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
