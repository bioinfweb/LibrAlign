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
package info.bioinfweb.libralign.alignmentarea;


import java.awt.Dimension;
import java.awt.Rectangle;

import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.alignmentarea.content.SWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;



/**
 * SWT component displaying the head, content, or bottom area of an alignment area. This class also implements
 * the necessary scrolling functionality and offers workarounds for missing Swing functionality in SWT.
 * <p>
 * This class is meant for internal use by {@link AlignmentArea} and should not be instantiated directly.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTAlignmentArea extends Composite implements ToolkitSpecificAlignmentArea {
	private AlignmentArea owner;
	
	private Composite labelContainer;
	private ScrolledComposite labelScroller;
	private SWTScrolledCompositeResizeListener labelResizeListener;
	private Composite contentContainer;
	private ScrolledComposite contentScroller;
	private SWTScrolledCompositeResizeListener contentResizeListener;
  
  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area using this component
	 * @param parent - the parent SWT component
	 * @param position - the position of this component in {@code owner}
	 * @param hideHorizontalScrollBar - Specify {@code true} here if you want no horizontal scroll bar to be displayed
	 *        in this part of the alignment are or {@code false} otherwise.
	 */
	public SWTAlignmentArea(Composite parent, int style, AlignmentArea owner, boolean hideHorizontalScrollBar) {
		super(parent, style);
		this.owner = owner;
		initGUI(hideHorizontalScrollBar);
	}
	
	
	private GridData createGridData(boolean grabExcessHorizontalSpace) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		return gridData;
	}
	
	
	private void initGUI(boolean hideHorizontalScrollBar) {
		// Set layout:
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		// Label components:
		labelContainer = new Composite(this, SWT.NONE);
		labelContainer.setLayoutData(createGridData(false));
		labelScroller = new ScrolledComposite(labelContainer,	SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		labelScroller.setAlwaysShowScrollBars(true);
		AlignmentLabelArea labelArea = getIndependentComponent().getLabelArea();
		labelScroller.setContent(labelArea.createSWTWidget(labelScroller, SWT.NONE));  // Will not create any subelements of labelArea because the content area has not yet been created.
		labelResizeListener = new SWTScrolledCompositeResizeListener(labelContainer, labelScroller, true, 
				hideHorizontalScrollBar);
		labelContainer.addControlListener(labelResizeListener);  // Must not be called before both fields are initialized.
		
		// Alignment area part components:
		contentContainer = new Composite(this, SWT.NONE);
		contentContainer.setLayoutData(createGridData(true));
		contentScroller = new ScrolledComposite(contentContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contentScroller.setAlwaysShowScrollBars(true);
		SWTAlignmentContentArea contentArea =
				(SWTAlignmentContentArea)getIndependentComponent().getContentArea().createSWTWidget(contentScroller, SWT.NONE);
		contentScroller.setContent(contentArea);
		contentResizeListener = new SWTScrolledCompositeResizeListener(contentContainer, contentScroller, false, 
				hideHorizontalScrollBar); 
		contentContainer.addControlListener(contentResizeListener);  // Must not be called before both fields are initialized.
		
		// Update label area:
		labelArea.getToolkitComponent().reinsertSubelements();
		
		// Synchronize vertical scrolling:
		ScrolledCompositeSyncListener verticalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{labelScroller, contentScroller}, false);
		verticalSyncListener.registerToAll();		

		// Link label area:
		contentArea.addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						getIndependentComponent().getLabelArea().assignSize();
					}
				});
		
		// Ensure correct width of label components:
		((Composite)labelArea.getToolkitComponent()).addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						Dimension size = getIndependentComponent().getLabelArea().getSize();
						int borderWidth = 2 * (labelContainer.getBorderWidth() + labelScroller.getBorderWidth());
						GridData data = (GridData)labelContainer.getLayoutData();
						data.widthHint = size.width + borderWidth;
						data.heightHint = size.height + borderWidth;
						labelContainer.setLayoutData(data);
						labelContainer.setSize(data.widthHint, data.heightHint);
					}
				});
	}


	public ScrolledComposite getContentScroller() {
		return contentScroller;
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return owner;
	}


	@Override
	public void repaint() {
		redraw();
	}


	@Override
	public int getHeight() {
		return getSize().y;
	}


	public boolean isHideHorizontalScrollBar() {
		return contentResizeListener.isHideHorizontalBar();
	}
	
	
	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		labelResizeListener.setHideHorizontalBar(hideHorizontalScrollBar);
		contentResizeListener.setHideHorizontalBar(hideHorizontalScrollBar);
 	}


	@Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		org.eclipse.swt.graphics.Point origin = getContentScroller().getOrigin();
		Rectangle visibleRect = getVisibleAlignmentRect();
		
		int x = origin.x;  // Do not scroll
		if (rectangle.x < visibleRect.x) {
			x = rectangle.x;  // Scroll left
		}
		else if (rectangle.x + rectangle.width > visibleRect.x + visibleRect.width) {
			x = rectangle.x + rectangle.width - visibleRect.width;  // Scroll right 
		}
		
		int y = origin.y;  // Do not scroll
		if (rectangle.y < visibleRect.y) {
			y = rectangle.y;  // Scroll up
		}
		if (rectangle.y + rectangle.height > visibleRect.y + visibleRect.height) {
			y = rectangle.y + rectangle.height - visibleRect.height;  // Scroll down
		}
		
		getContentScroller().setOrigin(x, y);
	}
  
  
	@Override
	public Rectangle getVisibleAlignmentRect() {
		org.eclipse.swt.graphics.Rectangle clientArea = getContentScroller().getClientArea();
		org.eclipse.swt.graphics.Point origin = getContentScroller().getOrigin();
		return new Rectangle(origin.x, origin.y, clientArea.width, clientArea.height);
	}
}
