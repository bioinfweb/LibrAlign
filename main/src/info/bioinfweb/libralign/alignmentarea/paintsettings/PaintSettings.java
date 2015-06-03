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
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainterList;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;



public class PaintSettings {
	public static final double DEFAULT_CURSOR_LINE_WIDTH = 2;
	
	
	private AlignmentArea owner;
	private TokenPainterList tokenPainterList = new TokenPainterList(this);
	private Color cursorColor = Color.BLACK;
	private double cursorLineWidth = DEFAULT_CURSOR_LINE_WIDTH;
	private Color selectionColor = SystemColor.textHighlight;    //TODO Move cursor color, width and selection color to separate object?
	private double zoomX = 1;
	private double zoomY = 1;
	
	
	public PaintSettings(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentArea getOwner() {
		return owner;
	}
	
	
	/**
	 * Returns the list of token painters to be used for the output of the data from the alignment model.
	 * If an concatenated alignment model is used, this list holds one painter for each part (column range),
	 * otherwise it will only contain one.
	 * <p>
	 * The size and the order of the list are updated automatically depending on alignment model changes (events). 
	 * 
	 * @return the token painter list
	 */
	public TokenPainterList getTokenPainterList() {
		return tokenPainterList;
	}


	public Color getCursorColor() {
		return cursorColor;
	}


	public void setCursorColor(Color cursorColor) {
		this.cursorColor = cursorColor;
	}


	public Color getSelectionColor() {
		return selectionColor;
	}


	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}


	public double getCursorLineWidth() {
		return cursorLineWidth;
	}


	public void setCursorLineWidth(double cursorLineWidth) {
		this.cursorLineWidth = cursorLineWidth;
	}


	public double getZoomX() {
		return zoomX;
	}


	public double getZoomY() {
		return zoomY;
	}


	public void setZoomX(double zoomX) {
		setZoom(zoomX, getZoomY());
	}
	
	
	public void setZoomY(double zoomY) {
		setZoom(getZoomX(), zoomY);
	}
	
	
	public void setZoom(double zoomX, double zoomY) {
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		
		//assignPaintSize();
		//fireZoomChanged();
	}
	
	
	/**
	 * Returns the width of the column with the specified index.
	 * 
	 * @param columnIndex the index of the column to determine the width from
	 * @return the width of column in pixels
	 * @throws IllegalStateException if this instance does not have an alignment model
	 * @throws IndexOutOfBoundsException if the specified column does not exist in the alignment model
	 */
	public double getTokenWidth(int columnIndex) {
		if (!getOwner().hasAlignmentModel()) {
			throw new IllegalStateException("There is no associated alignment model defined that specifies any columns.");
		}
		else if ((columnIndex < 0) || (columnIndex >= getOwner().getAlignmentModel().getMaxSequenceLength())) {
			throw new IndexOutOfBoundsException("A column with the index " + columnIndex + " does not exist in the current model.");
		}
		else {
			return getTokenPainterList().painterByColumn(columnIndex).getPreferredWidth() * getZoomX();
		}
	}
	
	
	public double maxTokenWidth() {
		if (getTokenPainterList().isEmpty()) {
			return getTokenPainterList().getDefaultTokenPainter().getPreferredWidth() * getZoomX();
		}
		else {
			double result = 0;
			for (TokenPainter painter : getTokenPainterList()) {
				double width;
				if (painter == null) {
					width = getTokenPainterList().getDefaultTokenPainter().getPreferredWidth();
				}
				else {
					width = painter.getPreferredWidth();
				}
				result = Math.max(result, width);
			}
			return result * getZoomX();
		}
	}
	
	
	public double minTokenWidth() {
		if (getTokenPainterList().isEmpty()) {
			return getTokenPainterList().getDefaultTokenPainter().getPreferredWidth() * getZoomX();
		}
		else {
			double result = 0;
			for (TokenPainter painter : getTokenPainterList()) {
				double width;
				if (painter == null) {
					width = getTokenPainterList().getDefaultTokenPainter().getPreferredWidth();
				}
				else {
					width = painter.getPreferredWidth();
				}
				result = Math.min(result, width);
			}
			return result * getZoomX();
		}
	}
	
	
	/**
	 * Returns the height of tokens displayed in this alignment.
	 * 
	 * @return the height in pixels
	 */
	public double getTokenHeight() {
		if (!getOwner().hasAlignmentModel()) {
			throw new IllegalStateException("There is no associated alignment model defined that specifies any columns.");
		}
		else {
			int index;
			if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				throw new InternalError("not implemented");
				// index = ?;   //TODO Which painter should define the height?
			}
			else {
				index = 0;
			}
			return getTokenPainterList().painterByColumn(index).getPreferredHeight() * getZoomY();
		}
	}
	
	
	public Font getTokenHeightFont() {
		return new Font(Font.SANS_SERIF, Font.PLAIN, 
				(int)Math.round(SingleColorTokenPainter.FONT_SIZE_FACTOR * getTokenHeight()));
	}
}
