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
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.libralign.alignmentarea.content.DirectSWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.SWTAlignmentLabelArea;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.scrolling.ScrollEvent;
import info.bioinfweb.tic.toolkit.scrolling.ScrollListener;

import java.awt.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;



public class DirectSWTAlignmentArea extends AbstractSWTAlignmentArea implements ToolkitSpecificAlignmentArea {
	public DirectSWTAlignmentArea(AlignmentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style);
	}
	
	
	@Override
	public AlignmentArea getIndependentComponent() {
		return (AlignmentArea)super.getIndependentComponent();
	}


	@Override
	protected Scrollable createContentScroller(Composite container, final SWTAlignmentLabelArea labelArea) {
		DirectSWTAlignmentContentArea result = (DirectSWTAlignmentContentArea)SWTComponentFactory.getInstance().getSWTComponent(
				getIndependentComponent().getContentArea(), container, SWT.NO_BACKGROUND, false);
		
		// Synchronize vertical scrolling:
		result.getScrollListeners().add(new ScrollListener() {
			@Override
			public void controlScrolled(ScrollEvent event) {
				labelArea.setVerticalScrollPosition(-event.getSource().getScrollOffsetY());
			}
		});

		return result;
	}


	@Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		((DirectSWTAlignmentContentArea)getIndependentComponent().getContentArea().getToolkitComponent()).scrollRectToVisible(
				new org.eclipse.swt.graphics.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
	}


	@Override
	public Rectangle getVisibleAlignmentRect() {
		return ((DirectSWTAlignmentContentArea)getIndependentComponent().getContentArea().getToolkitComponent()).getVisibleAlignmentRect();
	}


	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		// TODO implement
	}
}
