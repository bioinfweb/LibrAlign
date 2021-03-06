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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.awt.Color;
import java.awt.Font;
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
	public static final double DEFAULT_WIDTH = 10;  // For one character.
	public static final double DEFAULT_HEIGHT = 14;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;
	
	
	private Map<String, Color> backgroundColorMap = new TreeMap<String, Color>();
	private Color defaultBackgroundColor = Color.GRAY;
	private Color fontColor = Color.BLACK;
	private String fontName = Font.SANS_SERIF;
	private int fontStyle = Font.PLAIN;
	private Font currentFont = new Font(Font.SANS_SERIF, Font.PLAIN, 0);  // Initial value that would only be used if the first area is equal to the initial value of currentArea. (In that case no text would be painted.) 
	private Rectangle2D currentArea = new Rectangle2D.Double(0, 0, 0, 0);
	private double preferredWidth = DEFAULT_WIDTH;

	
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
	
	
	/**
	 * Returns the background color associated with the specified token as stored in {@link #getBackgroundColorMap()}.
	 * 
	 * @param tokenRepresentation the string representation of the token
	 * @return the associated background color or the same as {@link #getDefaultBackgroundColor()} if no such color is defined
	 */
	@Override
	public Color getColor(String tokenRepresentation) {
		Color result = getBackgroundColorMap().get(tokenRepresentation);
		if (result == null) {
			result = getDefaultBackgroundColor();
		}
		return result;
	}


	/**
	 * Returns the same color as {@link #getColor(String)} but blends it with a selection color, if one is specified.
	 * 
	 * @param tokenRepresentation the string representation of the token
	 * @param selectionColor the color of the selection in the associated alignment area (may be {@code null})
	 * @return the color to fill the background of a token with
	 */
	public Color backgroundColorByRepresentation(String tokenRepresentation, Color selectionColor) {
		Color result = getColor(tokenRepresentation);
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
			Font result = FontCalculator.getInstance().fontToFitRectangle(area, FONT_SIZE_FACTOR, text, 
					getFontName(), getFontStyle(), MIN_FONT_SIZE);
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
	protected void doPaintToken(AlignmentArea alignmentArea, String sequenceID,	int columnIndex, Object token,
			String tokenRepresentation, Graphics2D g, Rectangle2D area, Color selectionColor) {

		g.setColor(backgroundColorByRepresentation(tokenRepresentation, selectionColor));
		g.fill(area);
		paintText(g, area, tokenRepresentation, selectionColor);
	}
	
	
	public void calculatePreferredWidth(TokenSet<?> tokenSet) {
		preferredWidth = FontCalculator.getInstance().getTextWidthToTextHeigth(getFontName(), getFontStyle(), 
				StringUtils.repeat("m", tokenSet.maxRepresentationLength()), (float)(getPreferredHeight() * FONT_SIZE_FACTOR));
	}
	
	
	@Override
	public double getPreferredWidth() {
		return preferredWidth;
	}


	@Override
	public double getPreferredHeight() {
		return DEFAULT_HEIGHT;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((backgroundColorMap == null) ? 0 : backgroundColorMap.hashCode());
		result = prime * result
				+ ((currentArea == null) ? 0 : currentArea.hashCode());
		result = prime * result
				+ ((currentFont == null) ? 0 : currentFont.hashCode());
		result = prime
				* result
				+ ((defaultBackgroundColor == null) ? 0 : defaultBackgroundColor
						.hashCode());
		result = prime * result + ((fontColor == null) ? 0 : fontColor.hashCode());
		result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
		result = prime * result + fontStyle;
		long temp;
		temp = Double.doubleToLongBits(preferredWidth);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleColorTokenPainter other = (SingleColorTokenPainter) obj;
		if (backgroundColorMap == null) {
			if (other.backgroundColorMap != null)
				return false;
		} else if (!backgroundColorMap.equals(other.backgroundColorMap))
			return false;
		if (currentArea == null) {
			if (other.currentArea != null)
				return false;
		} else if (!currentArea.equals(other.currentArea))
			return false;
		if (currentFont == null) {
			if (other.currentFont != null)
				return false;
		} else if (!currentFont.equals(other.currentFont))
			return false;
		if (defaultBackgroundColor == null) {
			if (other.defaultBackgroundColor != null)
				return false;
		} else if (!defaultBackgroundColor.equals(other.defaultBackgroundColor))
			return false;
		if (fontColor == null) {
			if (other.fontColor != null)
				return false;
		} else if (!fontColor.equals(other.fontColor))
			return false;
		if (fontName == null) {
			if (other.fontName != null)
				return false;
		} else if (!fontName.equals(other.fontName))
			return false;
		if (fontStyle != other.fontStyle)
			return false;
		if (Double.doubleToLongBits(preferredWidth) != Double.doubleToLongBits(other.preferredWidth))
			return false;
		return true;
	}
}
