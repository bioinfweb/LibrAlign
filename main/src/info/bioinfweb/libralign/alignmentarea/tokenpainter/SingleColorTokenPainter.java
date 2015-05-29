/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;



/**
 * Token painter that paints the string representation of a token centered into a rectangle filled with a 
 * specified background color.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class SingleColorTokenPainter extends AbstractTokenPainter implements TokenPainter {
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;
	
	
	private Map<String, Color> backgroundColorMap = new TreeMap<String, Color>();
	private Color defaultBackgroundColor = Color.GRAY;
	private Color fontColor = Color.GRAY;
	private String fontName = Font.SANS_SERIF;
	private int fontStyle = Font.PLAIN;
	private Font currentFont = new Font(Font.SANS_SERIF, Font.PLAIN, 0);  // Initial value that would only be used if the first area is equal to the initial value of currentArea. (In that case no text would be painted.) 
	private Rectangle2D currentArea = new Rectangle2D.Double(0, 0, 0, 0);
	

	
	public Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}


	public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}


	public Color getFontColor() {
		return fontColor;
	}


	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}


	public String getFontName() {
		return fontName;
	}


	public void setFontName(String fontName) {
		this.fontName = fontName;
	}


	public int getFontStyle() {
		return fontStyle;
	}


	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}


	public Map<String, Color> getBackgroundColorMap() {
		return backgroundColorMap;
	}
	
	
	public Color backgroundColorByRepresentation(String tokenRepresentation, Color selectionColor) {
		Color result = getBackgroundColorMap().get(tokenRepresentation);
		if (result == null) {
			result = getDefaultBackgroundColor();
		}
		if (selectionColor != null) {
			result = GraphicsUtils.blend(result, selectionColor);
		}
		return result;
	}
	
	
	protected Font calculateFont(Graphics2D g, Rectangle2D area, String text) {
		if ((currentArea.getWidth() == area.getWidth()) && (currentArea.getHeight() == area.getHeight())) {  // Save calculation time.
			return currentFont;
		}
		else {
			Font result = FontCalculator.fontToFitRectangle(area, FONT_SIZE_FACTOR, text, getFontName(), getFontStyle(), MIN_FONT_SIZE);
			if (result != null) {
				currentArea = area;
				currentFont = result;
			}
			return result;
		}
	}
	
	
	protected void paintText(Graphics2D g, Rectangle2D area, String text, Color selectionColor) {
		Font font = calculateFont(g, area, text);
		if (font != null) {
			Color fontColor = getFontColor();
			if (selectionColor != null) {
				fontColor = GraphicsUtils.blend(fontColor, selectionColor);
			}
			g.setColor(fontColor);
			g.setFont(font);
			GraphicsUtils.drawStringInRectangle(g, area, text);
		}
	}
	

	@Override
	protected void doPaintToken(AlignmentArea alignmentArea, int sequenceID,	int columnIndex, Object token,
			String tokenRepresentation, Graphics2D g, Rectangle2D area, Color selectionColor) {

		g.setColor(backgroundColorByRepresentation(tokenRepresentation, selectionColor));
		g.fill(area);
		paintText(g, area, tokenRepresentation, selectionColor);
	}
}
