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

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.tic.TICPaintEvent;



/**
 * Event object used by {@link AlignmentSubArea} that provides information on the area of a component
 * displaying a part of an alignment to be painted, the graphics context and tool methods.
 * <p>
 * The coordinates used in {@link #getRectangle()} and {@link #getGraphics()} are relative to the left
 * most pixel of {@link #getFirstColumn()}. Therefore the x-coordinate 0 does not reference the left most
 * pixel of the whole component but the left most pixel of the area currently to be repainted. Relative
 * coordinates are used to avoid overflows when painting very long sequences.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class AlignmentPaintEvent extends TICPaintEvent {
	private AlignmentArea parentAlignmentArea;
	private int firstColumn;
	private int lastColumn;
	
	
	public AlignmentPaintEvent(Object source, AlignmentArea parentAlignmentArea, int firstColumn, int lastColumn, 
			Graphics2D graphics, Rectangle rectangle) {
		
		super(source, graphics, rectangle);
		this.parentAlignmentArea = parentAlignmentArea;
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
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
	 * Returns the index after the right most alignment alignment column in the area to be (at least 
	 * partly) painted.
	 * 
	 * @return the index of the first visible alignment column (Column indices start with 0.)
	 */
	public int getFirstColumn() {
		return firstColumn;
	}
	

	/**
	 * Returns the index of the left most alignment column in the area to be (at least partly) painted.
	 * 
	 * @return the index of the first visible alignment column (Column indices start with 0.)
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
