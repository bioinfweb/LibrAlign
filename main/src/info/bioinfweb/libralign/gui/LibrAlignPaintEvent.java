/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.gui;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.EventObject;



/**
 * Event that notifies implementations of {@link PaintableArea} that a part of their associated
 * components have to be repainted. 
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class LibrAlignPaintEvent extends EventObject {
  private Graphics2D graphics;
  private Rectangle rectangle;
  
  
	public LibrAlignPaintEvent(Object source, Graphics2D graphics,
			Rectangle rectangle) {
		super(source);
		this.graphics = graphics;
		this.rectangle = rectangle;
	}


	/**
	 * Returns the graphic context to paint on.
	 * 
	 * @return the {@link Graphics2D} object of the associated swing component or the adapter class of
	 *         the associated SWT component
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}


	/**
	 * Returns the rectangle that needs to be repainted.
	 * 
	 * @return the area to be painted determined the associated Swing or SWT class 
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}
}
