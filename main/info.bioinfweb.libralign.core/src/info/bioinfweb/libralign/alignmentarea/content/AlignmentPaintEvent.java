/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.tic.TICPaintEvent;



/**
 * Event object used by {@link AlignmentSubArea} that provides information on the area of a component
 * displaying a part of an alignment to be painted, the graphics context and tool methods.
 * <p>
 * The coordinates used in {@link #getRectangle()} and {@link #getGraphics()} are relative to the left
 * most pixel of {@link #getFirstColumn()}. Therefore the x-coordinate 0 does not reference the left most
 * pixel of the whole component but the left most pixel first alignment column to be repainted. Relative
 * coordinates are used to avoid overflows when painting very long sequences.
 * <p>
 * If the rectangle to be painted contains parts of the area left of the first alignment column, the 
 * return value of {@link #getFirstColumn()} will always be be 0 (even of the first column itself is not 
 * visible). Coordinates of {@link #getRectangle()} will still be relative to the left pixel of the column 
 * 0 and its x-coordinate will therefore be negative. The coordinate system used in {@link #getGraphics()}
 * will be identical so that painting can be performed using respective negative x-coordinates.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class AlignmentPaintEvent extends TICPaintEvent {
	private AlignmentArea parentAlignmentArea;
	private int firstColumn;
	private int lastColumn;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the object that triggered the event
	 * @param parentAlignmentArea the alignment area containing the component to be repainted
	 * @param firstColumn the first column of the alignment that shall be (partly) repainted (If only space left of the 
	 *        alignment shall be repainted, this value should still be 0.)
	 * @param lastColumn the index after the last column that shall be (partly) repainted (If only space right of the 
	 *        alignment shall be repainted, this value should still be equal to the number of columns in the alignment.)
	 * @param graphics the swing graphics context used to repaint the component
	 * @param rectangle the rectangle that has to be repainted with coordinates relative to the left most pixel of 
	 *        {@code firstColumn}
	 *        
	 * @throws IllegalArgumentException if {@code source}, {@code parentAlignmentArea}, {@code graphics} or {@code rectangle} are 
	 *         {@code null} or if {@code firstColumn} or {@code lastColumn} are below 0 
	 */
	public AlignmentPaintEvent(Object source, AlignmentArea parentAlignmentArea, int firstColumn, int lastColumn, 
			Graphics2D graphics, Rectangle rectangle) {  //TODO Better use Rectangle2D.Double. (See also comment below.)
		
		super(source, graphics, rectangle);  //TODO Values should be stored with double precision, to allow values greater than Integer.MAX_VALUE. The best solution would be to use the base class Rectangle2D rectangle in the TICPaintEvent, which would result in an API change in TIC.
		if (parentAlignmentArea == null) {
			throw new IllegalArgumentException("The parent alignment area must not be null.");
		}
		else if (firstColumn < 0) {
			throw new IllegalArgumentException("The first column must not be below 0. (" + firstColumn + ")");
		}
		else if (lastColumn < 0) {
			throw new IllegalArgumentException("The last column must not be below 0. (" + lastColumn + ")");
		}
		else {
			this.parentAlignmentArea = parentAlignmentArea;
			this.firstColumn = firstColumn;
			this.lastColumn = lastColumn;
		}
	}


	/**
	 * Returns the alignment area that contains the component that triggered this event.
	 * 
	 * @return the parent alignment area
	 */
	public AlignmentArea getParentAlignmentArea() {
		return parentAlignmentArea;
	}


	/**
	 * Returns the index of the left most alignment column in the area to be (at least partly) painted.
	 * <p>
	 * If only space left of the alignment shall be repainted, this method will still return 0, even if the first 
	 * column is not part if the area to be repainted. (The x-coordinate of {@link #getRectangle()} will be 
	 * negative in such cases.)
	 * 
	 * @return the index of the first visible alignment column (The first column of an alignment has the index 0.)
	 */
	public int getFirstColumn() {
		return firstColumn;
	}
	

	/**
	 * Returns the index after the right most alignment alignment column in the area to be (at least partly) 
	 * painted.
	 * <p>
	 * If only space right of the alignment shall be repainted, this method will always return the index after
	 * the last column of the alignment and coordinates returned by {@link #getRectangle()} will be relative to 
	 * it, even if the last column is not part if the area to be repainted.
	 * 
	 * @return the index of the first visible alignment column (The first column of an alignment has the index 0.)
	 */
	public int getLastColumn() {
		return lastColumn;
	}
	
	
	/**
	 * Returns the column containing the specified relative x-coordinate. If the coordinate lies behind 
	 * the last column of the whole alignment, the number of columns + 1 is returned. This method takes 
	 * the current horizontal zoom factor into account.
	 *
	 * @param x the paint coordinate relative to the first column to be currently painted as returned
	 *        by {@link #getFirstColumn()}
	 * @return the absolute alignment column
	 */
	public int columnByRelativePaintX(int x) {
		if (getParentAlignmentArea().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
		}
		else {
			return Math.max(0, Math.min(getParentAlignmentArea().getGlobalMaxSequenceLength(),
					(int)(x / getParentAlignmentArea().getPaintSettings().getTokenWidth(0) + getFirstColumn())));  //TODO Catch IllegalStateException?
		}
	}
	
	
	/**
	 * Returns the left most relative x-coordinate of the area the specified column fills up.
	 * This method takes the current horizontal zoom factor into account.
	 *
	 * @param column the column painted at the returned x-position
	 * @return an x-coordinate relative to to the first column to be currently painted as returned
	 *         by {@link #getFirstColumn()}
	 */
	public int relativePaintXByColumn(int column) {
	  if (getParentAlignmentArea().hasAlignmentModel()) {
  		if (getParentAlignmentArea().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
  			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
  		}
  		else {
  			return (int)Math.round((column - getFirstColumn()) *
  					getParentAlignmentArea().getPaintSettings().getTokenPainterList().painterByColumn(0).getPreferredWidth() *
  					getParentAlignmentArea().getPaintSettings().getZoomX());  //TODO Catch IllegalStateException?
  		}
	  }
	  else {
	    throw new IllegalStateException("Column dependent paint positions can only be calculated if an alignment model is defined.");
	  }
	}
}
