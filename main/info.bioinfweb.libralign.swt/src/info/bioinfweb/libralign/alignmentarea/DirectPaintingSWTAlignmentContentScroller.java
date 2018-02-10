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
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.InputEventForwarder;
import info.bioinfweb.libralign.alignmentarea.content.ToolkitSpecificAlignmentContentArea;
import info.bioinfweb.tic.toolkit.DirectPaintingSWTScrollContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;



/**
 * Extends {@link DirectPaintingSWTScrollContainer} from <i>TIC</i> with some <i>LibrAlign</i> specific
 * functionality to allow direct scrolling of an {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 * @see DirectPaintingSWTAlignmentArea
 */
public class DirectPaintingSWTAlignmentContentScroller extends DirectPaintingSWTScrollContainer 
		implements ToolkitSpecificAlignmentContentArea {
	
	public DirectPaintingSWTAlignmentContentScroller(AlignmentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style | SWT.NO_BACKGROUND |	SWT.NO_REDRAW_RESIZE, ticComponent.getContentArea());
		
		// Ensure that all key and mouse events will be forwarded to respective AlignmentSubAreas:
		InputEventForwarder forwarder = new InputEventForwarder(ticComponent.getContentArea()); 
		ticComponent.getContentArea().addKeyListener(forwarder);
		ticComponent.getContentArea().addMouseListener(forwarder);
		ticComponent.getContentArea().addMouseWheelListener(forwarder);
		
		getVerticalBar().setIncrement(10);  //TODO Specify values depending on the token width and height later and update it during zooming.
		getVerticalBar().setPageIncrement(50);
		getHorizontalBar().setIncrement(10);
		getHorizontalBar().setPageIncrement(50);
	}
	
	
	@Override
	public AlignmentArea getIndependentComponent() {
		return (AlignmentArea)super.getIndependentComponent();
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
