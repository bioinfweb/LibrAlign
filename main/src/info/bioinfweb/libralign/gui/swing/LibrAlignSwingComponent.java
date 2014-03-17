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
package info.bioinfweb.libralign.gui.swing;


import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import info.bioinfweb.libralign.gui.LibrAlignGUIElement;
import info.bioinfweb.libralign.gui.LibrAlignPaintEvent;
import info.bioinfweb.libralign.gui.PaintableArea;



/**
 * The Swing component displaying a {@link PaintableArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class LibrAlignSwingComponent extends JComponent implements LibrAlignGUIElement {  //TODO Does JPanel have to be used here?
	private PaintableArea paintableArea;

	
	public LibrAlignSwingComponent(PaintableArea paintableArea) {
		super();
		this.paintableArea = paintableArea;
	}


	@Override
	public void paint(Graphics graphics) {
		paintableArea.paint(new LibrAlignPaintEvent(this, (Graphics2D)graphics, getVisibleRect()));
	}


	@Override
	public PaintableArea getPaintableArea() {
		return paintableArea;
	}
}
