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


import java.awt.Dimension;

import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;

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
public class SWTAlignmentArea extends Composite {
	private AlignmentArea owner;
	private DataAreaListType position;
	
	private Composite labelContainer;
	private ScrolledComposite labelScroller;
  private AlignmentLabelArea labelArea;
  private SWTScrolledCompositeResizeListener labelResizeListener;
  private Composite contentContainer;
  private ScrolledComposite contentScroller;
  private SWTCoreAlignmentArea contentArea;
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
	public SWTAlignmentArea(AlignmentArea owner, Composite parent, DataAreaListType position, 
			boolean hideHorizontalScrollBar) {
		
		super(parent, SWT.NONE);
		this.owner = owner;
		this.position = position;
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
		labelArea = new AlignmentLabelArea(getOwner(), getPosition());
		labelScroller.setContent(labelArea.createSWTWidget(labelScroller, SWT.NONE));
		labelResizeListener = new SWTScrolledCompositeResizeListener(labelContainer, labelScroller, true, 
				hideHorizontalScrollBar); 
		labelContainer.addControlListener(labelResizeListener);  // Must not be called before both fields are initialized.
		
		// Alignment area part components:
		contentContainer = new Composite(this, SWT.NONE);
		contentContainer.setLayoutData(createGridData(true));
		contentScroller = new ScrolledComposite(contentContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contentScroller.setAlwaysShowScrollBars(true);
		contentArea = new SWTCoreAlignmentArea(contentScroller, SWT.NONE);
		contentScroller.setContent(contentArea);
		contentResizeListener = new SWTScrolledCompositeResizeListener(contentContainer, contentScroller, false, 
				hideHorizontalScrollBar); 
		contentContainer.addControlListener(contentResizeListener);  // Must not be called before both fields are initialized.
				
		// Synchronize vertical scrolling:
		ScrolledCompositeSyncListener verticalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{labelScroller, contentScroller}, false);
		verticalSyncListener.registerToAll();		

		// Link label area:
		labelArea.setAlignmentPartArea(contentArea);
		contentArea.addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						labelArea.assignSize();
					}
				});
		
		// Ensure correct width of label components:
		((Composite)labelArea.getToolkitComponent()).addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						Dimension size = labelArea.getSize();
  					int borderWidth = 2 * (labelContainer.getBorderWidth() + labelScroller.getBorderWidth());
						GridData data = (GridData)labelContainer.getLayoutData();
						data.widthHint = size.width + borderWidth;
						data.heightHint = size.height + borderWidth;
						labelContainer.setLayoutData(data);
						labelContainer.setSize(data.widthHint, data.heightHint);
					}
				});
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	public DataAreaListType getPosition() {
		return position;
	}


	public Composite getLabelContainer() {
		return labelContainer;
	}


	public ScrolledComposite getLabelScroller() {
		return labelScroller;
	}


	public AlignmentLabelArea getLabelArea() {
		return labelArea;
	}


	public Composite getContentContainer() {
		return contentContainer;
	}


	public ScrolledComposite getContentScroller() {
		return contentScroller;
	}


	public SWTCoreAlignmentArea getContentArea() {
		return contentArea;
	}
	
	
	public boolean isHideHorizontalScrollBars() {
		return contentResizeListener.isHideHorizontalBar();
	}
	
	
	public void setHideHorizontalScrollBars(boolean hide) {
		labelResizeListener.setHideHorizontalBar(hide);
		contentResizeListener.setHideHorizontalBar(hide);
	}
}
