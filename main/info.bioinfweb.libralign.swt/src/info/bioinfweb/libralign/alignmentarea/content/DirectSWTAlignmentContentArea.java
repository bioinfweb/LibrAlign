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


import info.bioinfweb.tic.toolkit.DefaultScrolledSWTComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;



public class DirectSWTAlignmentContentArea extends DefaultScrolledSWTComposite implements ToolkitSpecificAlignmentContentArea {
	//TODO Assign size probably needs to be suppressed in the independent component?

	public DirectSWTAlignmentContentArea(AlignmentContentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style | SWT.NO_BACKGROUND |	SWT.NO_REDRAW_RESIZE);
	}
 
	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return (AlignmentContentArea)super.getIndependentComponent();
	}


	public java.awt.Rectangle getVisibleAlignmentRect() {
		Rectangle r = getVisibleRectangle();
		return new java.awt.Rectangle(r.x, r.y, r.width, r.height);
	}


	@Override
	public void reinsertSubelements() {}  // Nothing to do.


	@Override
	public boolean hasSubcomponents() {
		return false;
	}


	@Override
	public void repaintSequences() {
		repaint();
	}
}
