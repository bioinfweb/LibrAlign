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
package info.bioinfweb.libralign.gui.swt;


import info.bioinfweb.libralign.PaintableArea;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DUtilities;



/**
 * Implementation of {@link IPaintable} used by {@link LibrAlignSWTWidget}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class J2D4SWTPaintable implements IPaintable {
	private PaintableArea paintableArea;
	
	
	public J2D4SWTPaintable(PaintableArea paintableArea) {
		super();
		this.paintableArea = paintableArea;
	}


	@Override
	public Rectangle2D getBounds(Control control) {
		return J2DUtilities.toRectangle2D(control.getBounds());  //TODO Replace this with values obtained from paintableArea
	}
	
	
	@Override
	public void paint(Control control, Graphics2D graphics) {
		paintableArea.paint(graphics);
	}

	
	@Override
	public void redraw(Control control, GC gc) {}


	public PaintableArea getPaintableArea() {
		return paintableArea;
	}
}
