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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.jfree.experimental.swt.SWTGraphics2D;



/**
 * The SWT widget displaying a {@link PaintableArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class LibrAlignSWTWidget extends Canvas implements PaintListener {
	private PaintableArea paintableArea;

	
	public LibrAlignSWTWidget(Composite parent, int style, PaintableArea paintableArea) {
		super(parent, style);
	}


	public PaintableArea getPaintableArea() {
		return paintableArea;
	}


	@Override
	public void paintControl(PaintEvent e) {
		SWTGraphics2D g = new SWTGraphics2D(e.gc);
		try {
			paintableArea.paint(g);
		}
		finally {
			g.dispose();
		}
	}
}
