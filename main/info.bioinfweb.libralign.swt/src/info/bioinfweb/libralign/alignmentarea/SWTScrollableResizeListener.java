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


import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;



/**
 * Helper class that cuts off the horizontal scroll bar from an <i>SWT</i> {@link Scrollable} to provide
 * functionality comparable to <i>Swing</i> components.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class SWTScrollableResizeListener implements ControlListener {
	private Composite container;
	private Scrollable scrollable;
	private boolean hideVerticalBar;
	private boolean hideHorizontalBar;
	
	
	public SWTScrollableResizeListener(Composite container, Scrollable scrollable, 
			boolean hideVerticalBar, boolean hideHorizontalBar) {
		
		super();
		this.container = container;
		this.scrollable = scrollable;
		this.hideVerticalBar = hideVerticalBar;
		this.hideHorizontalBar = hideHorizontalBar;
	}


	public Composite getContainer() {
		return container;
	}


	public Scrollable getScrollable() {
		return scrollable;
	}


	public boolean isHideVerticalBar() {
		return hideVerticalBar;
	}


	public boolean isHideHorizontalBar() {
		return hideHorizontalBar;
	}


	public void setHideHorizontalBar(boolean hideHorizontalBar) {
		this.hideHorizontalBar = hideHorizontalBar;
		controlResized(null);  // Resize child components. (Event object is currently not used in controlResized().) 
	}


	@Override
	public void controlMoved(ControlEvent e) {}

	
	@Override
	public void controlResized(ControlEvent e) {
		Rectangle bounds = container.getBounds();		
		int width = bounds.width;
		if (isHideVerticalBar()) {
			width += scrollable.getVerticalBar().getSize().x + scrollable.getBorderWidth();
		}
		int height = bounds.height;
		if (isHideHorizontalBar()) {
			height += scrollable.getHorizontalBar().getSize().y + scrollable.getBorderWidth(); 
		}
		scrollable.setBounds(0, 0, width, height);
	}
}
