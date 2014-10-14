/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentareacomponents;


import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;



/**
 * Helper class that cuts off the horizontal scroll bar from a SWT {@link ScrolledComposite} to simulate
 * Swing functionality.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTScrolledCompositeResizeListener implements ControlListener {
	private Composite container;
	private ScrolledComposite scrolledComposite;
	private boolean hideVerticalBar;
	private boolean hideHorizontalBar;
	
	
	public SWTScrolledCompositeResizeListener(Composite container, ScrolledComposite scrolledComposite, 
			boolean hideVerticalBar, boolean hideHorizontalBar) {
		
		super();
		this.container = container;
		this.scrolledComposite = scrolledComposite;
		this.hideVerticalBar = hideVerticalBar;
		this.hideHorizontalBar = hideHorizontalBar;
	}


	public Composite getContainer() {
		return container;
	}


	public ScrolledComposite getScrolledComposite() {
		return scrolledComposite;
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
		System.out.println(width + " " + isHideVerticalBar());
		if (isHideVerticalBar()) {
			width += scrolledComposite.getVerticalBar().getSize().x + scrolledComposite.getBorderWidth();
		}
		int height = bounds.height;
		if (isHideHorizontalBar()) {
			height += scrolledComposite.getVerticalBar().getSize().x + scrolledComposite.getBorderWidth(); 
		}
		scrolledComposite.setBounds(0, 0, width, height);
	}
}
