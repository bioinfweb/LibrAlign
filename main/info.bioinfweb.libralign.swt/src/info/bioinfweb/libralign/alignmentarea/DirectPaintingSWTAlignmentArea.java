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


import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.SWTAlignmentLabelArea;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.DirectPaintingSWTScrollContainer;

import java.awt.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;



/**
 * A toolkit-specific alignment area that implements scrolling via direct painting for <i>SWT</i>.
 * <p>
 * In contrast to {@link ScrollContainerSWTAlignmentArea} this implementation uses 
 * {@link DirectPaintingSWTAlignmentContentScroller} instead of {@link ScrolledComposite}, which makes
 * use of {@link DirectPaintingSWTScrollContainer} from <i>TIC</i>. {@link ScrolledComposite} currently 
 * (2017) has limitations under <i>Windows</i> and <i>Linux</i> in the maximum size of the scrolled 
 * content. This implementation is offered as an alternative to overcome these limitations. The
 * methods to provide scrolling functionality are delegated to the nested 
 * {@link DirectPaintingSWTAlignmentContentScroller} instance.
 * <p>
 * In contrast to the {@link ScrollContainerSWTAlignmentArea} and its <i>Swing</i> counterpart this
 * implementation will not create a toolkit component with its subcomponents for 
 * {@link AlignmentContentArea} that is then scrolled but will paint the contents of the <i>TIC</i>
 * component directly with an offset according to the current scroll position.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 * @see AlignmentArea
 * @see ScrollContainerSWTAlignmentArea
 */
public class DirectPaintingSWTAlignmentArea extends AbstractSWTAlignmentArea implements ToolkitSpecificAlignmentArea {
	private DirectPaintingSWTAlignmentContentScroller contentScroller;

	
	public DirectPaintingSWTAlignmentArea(AlignmentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style);
	}
	
	
	@Override
	public AlignmentArea getIndependentComponent() {
		return (AlignmentArea)super.getIndependentComponent();
	}


	@Override
	protected Scrollable createContentScroller(Composite container, final SWTAlignmentLabelArea labelArea) {
		contentScroller = new DirectPaintingSWTAlignmentContentScroller(getIndependentComponent(), container, SWT.NO_BACKGROUND); 
		return contentScroller;
	}


	@Override
	public void setScrollOffset(int x, int y) {
		contentScroller.setScrollOffset(x, y);
	}


	@Override
	public Rectangle getVisibleRectangle() {
		return contentScroller.getVisibleRectangle();
	}


	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		// TODO implement
	}
}
