/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 \* Copyright (C) 2014 - 2016  Ben Stöver
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


import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.SWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;
import info.bioinfweb.libralign.alignmentarea.label.SWTAlignmentLabelArea;
import info.bioinfweb.libralign.multiplealignments.SWTMultipleAlignmentsContainer;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.AbstractSWTComposite;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class SWTAlignmentArea extends AbstractSWTComposite implements ToolkitSpecificAlignmentArea {
	private final AlignmentArea owner;

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
	public SWTAlignmentArea(AlignmentArea owner, Composite parent, int style) {
		super(owner, parent, style);
		this.owner = owner;
		initGUI(false);
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
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				layout();
			}
		});
		SWTComponentFactory factory = SWTComponentFactory.getInstance();

		// Label components:
		labelContainer = new Composite(this, SWT.NONE);
		labelContainer.setLayoutData(createGridData(false));
		labelScroller = new ScrolledComposite(labelContainer,	SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		labelScroller.setAlwaysShowScrollBars(true);
		SWTAlignmentLabelArea labelArea =  // Will not create any subelements of labelArea because the content area has not yet been created.
				(SWTAlignmentLabelArea)factory.getSWTComponent(getIndependentComponent().getLabelArea(), labelScroller, SWT.NONE);
		labelScroller.setContent(labelArea);
		labelResizeListener = new SWTScrolledCompositeResizeListener(labelContainer, labelScroller, true,
				hideHorizontalScrollBar);
		labelContainer.addControlListener(labelResizeListener);  // Must not be called before both fields are initialized.

		// Alignment area part components:
		contentContainer = new Composite(this, SWT.NONE);
		contentContainer.setLayoutData(createGridData(true));
		contentScroller = new ScrolledComposite(contentContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contentScroller.setAlwaysShowScrollBars(true);
		SWTAlignmentContentArea contentArea =	(SWTAlignmentContentArea)factory.getSWTComponent(
				getIndependentComponent().getContentArea(), contentScroller, SWT.NONE);
		contentScroller.setContent(contentArea);
		contentScroller.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {  // Sets the focus to an according sequence area, if the user clicks outside of the content area and moves the cursor.
				if (getIndependentComponent().hasAlignmentModel() && (getIndependentComponent().getAlignmentModel().getSequenceCount() > 0)) {
    			AlignmentContentArea contentArea = getIndependentComponent().getContentArea();
    			int row = contentArea.rowByPaintY(event.y);
    			SequenceArea sequenceArea = contentArea.getToolkitComponent().getSequenceAreaByID(
    					getIndependentComponent().getSequenceOrder().idByIndex(row));
    			if (sequenceArea != null) {
    				getIndependentComponent().getSelection().setNewCursorPosition(contentArea.columnByPaintX(event.x), row);
    				((Composite)sequenceArea.getToolkitComponent()).setFocus();
    			}
				}
			}
		});
		contentResizeListener = new SWTScrolledCompositeResizeListener(contentContainer, contentScroller, false,
				hideHorizontalScrollBar);
		contentContainer.addControlListener(contentResizeListener);  // Must not be called before both fields are initialized.

		// Update label area:
		labelArea.reinsertSubelements();

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
		ControlListener labelAreaResizeListener = new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						Dimension size = getIndependentComponent().getLabelArea().getSize();
						GridData data = (GridData)labelContainer.getLayoutData();
						data.widthHint = size.width + 2 * (labelContainer.getBorderWidth() + labelScroller.getBorderWidth());
						data.heightHint = getClientArea().height;  // AlignmentLabelArea will be scrolled vertically.
						labelContainer.setLayoutData(data);
						layout();
					}
				};
		labelArea.addControlListener(labelAreaResizeListener);
		labelAreaResizeListener.controlResized(null);  // Apply initial size to container.

		// Set correct size to recently created SWT components:
		getIndependentComponent().assignSizeToAll();
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


	/**
	 * Returns the height of the horizontal content scroll bar.
	 * <p>
	 * This method is used internally by {@link SWTMultipleAlignmentsContainer#getNeededHeight(int)}.
	 *
	 * @return the height in pixels
	 */
	public int getHorizontalScrollbarHeight() {
		return getContentScroller().getHorizontalBar().getSize().y;
	}
}
