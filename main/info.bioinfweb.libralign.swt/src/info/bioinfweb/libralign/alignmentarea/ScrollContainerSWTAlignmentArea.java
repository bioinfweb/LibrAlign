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


import info.bioinfweb.libralign.alignmentarea.label.SWTAlignmentLabelArea;
import info.bioinfweb.libralign.multiplealignments.SWTMultipleAlignmentsContainer;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.ScrolledCompositeToolkitComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;



/**
 * <i>SWT</i> component displaying the head, content, or bottom area of an alignment area. This class also implements
 * the necessary scrolling functionality and offers workarounds for missing respective functionality in <i>SWT</i> 
 * compared to <i>Swing</i>.
 * <p>
 * This class is meant for internal use by {@link AlignmentArea} and should not be instantiated directly.
 *
 * @author Ben St&ouml;ver
 * @since 0.2.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class ScrollContainerSWTAlignmentArea extends AbstractSWTAlignmentArea implements ToolkitSpecificAlignmentArea, 
		ScrolledCompositeToolkitComponent {
	
	private ScrolledComposite contentScroller;
	
	
	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner the alignment area using this component
	 * @param parent the parent SWT component
	 * @param position the position of this component in {@code owner}
	 * @param hideHorizontalScrollBar Specify {@code true} here if you want no horizontal scroll bar to be displayed
	 *        in this part of the alignment are or {@code false} otherwise.
	 */
	public ScrollContainerSWTAlignmentArea(AlignmentArea owner, Composite parent, int style) {
		super(owner, parent, style);
	}


	@Override
	public ScrolledComposite getScrolledComposite() {
		return contentScroller;
	}


	@Override
	protected Scrollable createContentScroller(Composite container, final SWTAlignmentLabelArea labelArea) {
		contentScroller = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contentScroller.setAlwaysShowScrollBars(true);
		Composite contentArea = SWTComponentFactory.getInstance().getSWTComponent(
				getIndependentComponent().getContentArea(), contentScroller, SWT.NONE, true);
		
		contentScroller.setContent(contentArea);
		return contentScroller;
	}


	/**
	 * Returns the height of the horizontal content scroll bar.
	 * <p>
	 * This method is used internally by {@link SWTMultipleAlignmentsContainer#getNeededHeight(int)}.
	 *
	 * @return the height in pixels
	 */
	public int getHorizontalScrollbarHeight() {
		//TODO Avoid NPE when contentScroller was not yet created?
		return contentScroller.getHorizontalBar().getSize().y;
	}
}
