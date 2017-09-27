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
import info.bioinfweb.tic.scrolling.TICScrollEvent;
import info.bioinfweb.tic.scrolling.TICScrollListener;
import info.bioinfweb.tic.toolkit.AbstractSWTComposite;

import java.awt.Dimension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;



/**
 * Implements shared functionality of {@link ScrollContainerSWTAlignmentArea} and {@link DirectPaintingSWTAlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 * @see AlignmentArea
 * @see ScrollContainerSWTAlignmentArea
 * @see DirectPaintingSWTAlignmentArea
 */
public abstract class AbstractSWTAlignmentArea extends AbstractSWTComposite implements ToolkitSpecificAlignmentArea {
	private SWTAlignmentLabelArea labelArea;
	private Composite contentContainer;
	private Scrollable contentScroller;
	private SWTScrollableResizeListener contentResizeListener;


	public AbstractSWTAlignmentArea(AlignmentArea independentComponent, Composite parent, int style) {
		super(independentComponent, parent, style);
		initGUI(false);  // Value will be adjusted using the setter later.
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
		
		// Label component:
		labelArea = (SWTAlignmentLabelArea)SWTComponentFactory.getInstance().getSWTComponent(
				getIndependentComponent().getLabelArea(), this, SWT.NONE);
		labelArea.setLayoutData(createGridData(false));

		// Alignment area part components:
		contentContainer = new Composite(this, SWT.NONE);
		contentContainer.setLayoutData(createGridData(true));
		contentScroller = createContentScroller(contentContainer);
		contentResizeListener = new SWTScrollableResizeListener(contentContainer, contentScroller, false,
				hideHorizontalScrollBar);
		contentContainer.addControlListener(contentResizeListener);  // Must not be called before both fields are initialized.

		// Update label area:
		labelArea.reinsertSubelements();

		// Synchronize vertical scrolling:
		// (This is done here, since it is not necessary in Swing, where JScrollPane handles this automatically.)
		getIndependentComponent().getScrollListeners().add(new TICScrollListener() {
			@Override
			public void contentScrolled(TICScrollEvent event) {
				labelArea.setVerticalScrollPosition(getIndependentComponent().getScrollOffsetY());
			}
		});

		// Link label area:
		contentScroller.addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						getIndependentComponent().getLabelArea().assignSize();
					}
				});

		// Ensure correct width of label components:
		ControlListener labelAreaResizeListener = new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {  //TODO Is this still needed or are the present calls of assignSize() sufficient for the directly scrolled component that is now used?
						Dimension size = getIndependentComponent().getLabelArea().getSize();
						GridData data = (GridData)labelArea.getLayoutData();
						data.widthHint = size.width + 2 * labelArea.getBorderWidth();
						data.heightHint = getClientArea().height;  // AlignmentLabelArea will be scrolled vertically.
						labelArea.setLayoutData(data);
						layout();
					}
				};
		labelArea.addControlListener(labelAreaResizeListener);
		labelAreaResizeListener.controlResized(null);  // Apply initial size to container.

		// Set correct size to recently created SWT components:
		getIndependentComponent().assignSizeToAll();
	}
	
	
	protected abstract Scrollable createContentScroller(Composite container);

	
	@Override
	public AlignmentArea getIndependentComponent() {
		return (AlignmentArea)super.getIndependentComponent();
	}


	public boolean isHideHorizontalScrollBar() {
		return contentResizeListener.isHideHorizontalBar();
	}


	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		contentResizeListener.setHideHorizontalBar(hideHorizontalScrollBar);
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
