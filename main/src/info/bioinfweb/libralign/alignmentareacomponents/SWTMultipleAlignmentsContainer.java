/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.selection.AlignmentCursor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;



/**
 * The SWT component rendering all parts of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTMultipleAlignmentsContainer extends Composite implements ToolkitSpecificMultipleAlignmentsContainer {
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	private SashForm sashForm;
	private SWTAlignmentArea headComponent;
	private SWTAlignmentArea contentComponent;
	private SWTAlignmentArea bottomComponent;
	

	public SWTMultipleAlignmentsContainer(Composite parent, int style, AlignmentArea independentComponent) {
		super(parent, style);
		this.independentComponent = independentComponent;		
		init();
	}
	
	
	private void init() {
		super.setLayout(new FillLayout(SWT.HORIZONTAL));
		addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						redistributeHeight();
					}
				});

		// Create components:
		sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setSashWidth(AlignmentArea.DIVIDER_WIDTH);
		headComponent = new SWTAlignmentArea(getIndependentComponent(), sashForm, DataAreaListType.TOP, true);
		contentComponent = new SWTAlignmentArea(getIndependentComponent(), sashForm, DataAreaListType.SEQUENCE, true);
		bottomComponent = new SWTAlignmentArea(getIndependentComponent(), sashForm, DataAreaListType.BOTTOM, false);
		sashForm.setWeights(new int[]{1, 2, 1});  //TODO Adjust
		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
		reinsertSubelements();
		
		// Synchronize horizontal scrolling:
		ScrolledCompositeSyncListener horizontalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{headComponent.getContentScroller(), contentComponent.getContentScroller(), 
						bottomComponent.getContentScroller()}, true);
		horizontalSyncListener.registerToAll();
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public void repaint() {
		redraw();
	}

	
	@Override
	public ToolkitSpecificAlignmentArea getPartArea(DataAreaListType position) {
		switch (position) {
			case TOP:
				return headComponent.getContentArea();
			case SEQUENCE:
				return contentComponent.getContentArea();
			default:
				return bottomComponent.getContentArea();
		}
	}


	@Override
	public SequenceArea getSequenceAreaByID(int sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	@Override
	public void reinsertSubelements() {
		// Head elements:
		headComponent.getContentArea().removeAll();
		headComponent.getContentArea().addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		headComponent.getContentArea().assignSize();
		
		// Content elements:
		contentComponent.getContentArea().removeAll();
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			sequenceAreaMap.get(id).createSWTWidget(contentComponent.getContentArea(), SWT.NONE);
			sequenceAreaMap.get(id).assignSize();
			contentComponent.getContentArea().addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}
		contentComponent.getContentArea().assignSize();

		// Bottom elements:
		bottomComponent.getContentArea().removeAll();
		bottomComponent.getContentArea().addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
		bottomComponent.getContentArea().assignSize();

    //assignSize();
		//layout(true, true);  //TODO Necessary?
	}


	@Override
	public void redistributeHeight() {
		// Horizontal scroll bar in SWT is always visible (difference to Swing implementation).
		int horzScrollBarHeight = bottomComponent.getContentScroller().getHorizontalBar().getSize().y;
  	int overallHeight = getSashForm().getSize().y;  //headComponent.getPartScroller().getSize().y +	contentComponent.getPartScroller().getSize().y + bottomComponent.getPartScroller().getSize().y + horzScrollBarHeight;
  	final int verticalBorderWidth = 3;  // The additional width used up by component borders in each part of the SashForm.  //TODO Can this be reduced?
		int headHeight = headComponent.getContentArea().getHeight() + verticalBorderWidth;
		int contentHeight = contentComponent.getContentArea().getHeight() + verticalBorderWidth;
		int bottomHeight = bottomComponent.getContentArea().getHeight() + verticalBorderWidth + horzScrollBarHeight;
  	int neededHeight = headHeight + contentHeight + bottomHeight;
		
  	if (overallHeight < neededHeight) {
//  		if (getIndependentComponent().isScrollHeadArea()) {
//  			headHeight = 0;
//  		}
//  		if (getIndependentComponent().isScrollBottomArea()) {
//  			bottomHeight = 0;
//  		}
  		
  		if (headHeight + bottomHeight + AlignmentArea.MIN_PART_AREA_HEIGHT > overallHeight) {
  			headHeight = 0;
  			bottomHeight = 0;
  		}
  		else {
  			neededHeight -= (headHeight + bottomHeight);
  		}
  		
  		double availableHeight = overallHeight - (headHeight + bottomHeight);
  		double reduceFactor = (double)availableHeight / (double)neededHeight;
  		if (headHeight == 0) {
  			headHeight = (int)Math.round(headComponent.getContentArea().getHeight() * reduceFactor);
  		}
 			contentHeight = (int)Math.round(contentComponent.getContentArea().getHeight() * reduceFactor);
  		if (bottomHeight == 0) {
  			bottomHeight = (int)Math.round((bottomComponent.getContentArea().getHeight() + horzScrollBarHeight) * reduceFactor);
  		}
  		
  		headHeight = Math.max(1, headHeight);
  		contentHeight = Math.max(1, contentHeight);
  		bottomHeight = Math.max(1, bottomHeight);
  	}
  	else {
  		bottomHeight = Math.max(1, overallHeight - headHeight - contentHeight);  // Assign remaining space to bottom part.
  	}
  	getSashForm().setWeights(new int[]{headHeight, contentHeight , bottomHeight});
	}


  @Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		org.eclipse.swt.graphics.Point origin = contentComponent.getContentScroller().getOrigin();
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
		
		contentComponent.getContentScroller().setOrigin(x, y);
	}


	@Override
	public Rectangle getVisibleAlignmentRect() {
		org.eclipse.swt.graphics.Rectangle clientArea = contentComponent.getContentScroller().getClientArea();
		org.eclipse.swt.graphics.Point origin = contentComponent.getContentScroller().getOrigin();
		return new Rectangle(origin.x, origin.y, clientArea.width, clientArea.height);
	}


	//TODO Overwriting this methods is probably not necessary.
//	@Override
//	public Point computeSize(int wHint, int hHint, boolean changed) {
//		// TODO Auto-generated method stub
//		return super.computeSize(arg0, arg1, arg2);
//	}
//
//
//	@Override
//	public void layout(boolean arg0) {
//		// TODO Auto-generated method stub
//		super.layout(arg0);
//	}


	/**
	 * Does nothing since this component only uses its own layout. 
	 */
	@Override
	public void setLayout(Layout layout) {}
	
	
	public SashForm getSashForm() {
		return sashForm;
	}
}
