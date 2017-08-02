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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.TICPaintEvent;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;



/**
 * Event object used by {@link AlignmentSubArea} that provides information on the area of a component
 * displaying a part of an alignment to be painted and the graphics context.
 * <p>
 * The coordinates used in {@link #getRectangle()} and {@link #getGraphics()} are relative to the left
 * most pixel of first column of the alignment. Therefore the x-coordinate 0 does necessarily not reference 
 * the left most pixel of the whole component if data areas use space left of the alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class AlignmentPaintEvent extends TICPaintEvent {
	private static final long serialVersionUID = 1L;
	
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
	 * @param rectangle the rectangle that has to be repainted with coordinates relative to the left most pixel of the
	 *        first column of the alignment
	 *        
	 * @throws IllegalArgumentException if {@code source}, {@code parentAlignmentArea}, {@code graphics} or {@code rectangle} are 
	 *         {@code null} or if {@code firstColumn} or {@code lastColumn} are below 0 
	 */
	public AlignmentPaintEvent(Object source, AlignmentArea parentAlignmentArea, int firstColumn, int lastColumn, 
			Graphics2D graphics, Rectangle2D rectangle) {
		
		super(source, graphics, rectangle);
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


//	/**
//	 * Returns the rectangle that needs to be repainted. Coordinates are relative to the left most alignment column (column 0).
//	 * <p>
//	 * In contrast to {@link TICPaintEvent} this class always uses instances of {@link Rectangle2D.Double} to make sure that 
//	 * large alignments (possibly using coordinates higher than {@link Integer#MAX_VALUE}) can be painted.
//	 * 
//	 * @return the area to be painted 
//	 */
//	@Override
//	public Rectangle2D getRectangle() {
//		return (Rectangle2D)super.getRectangle();
//	}


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
}
