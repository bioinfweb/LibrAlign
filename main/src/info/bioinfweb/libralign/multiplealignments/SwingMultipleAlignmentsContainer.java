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
package info.bioinfweb.libralign.multiplealignments;


import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SwingAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;



/**
 * Swing specific GUI component that contains the three scroll panes that make up an alignment area.
 * 
 * @author Ben St&oumol;ver
 * @since 0.1.0
 */
public class SwingMultipleAlignmentsContainer extends JComponent implements ToolkitSpecificMultipleAlignmentsContainer {
	private final AdjustmentListener SCROLL_SYNC_LISTENER = new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					for (int i = 0; i < getIndependentComponent().size(); i++) {
						JScrollBar scrollBar = getIndependentComponent().get(i).createSwingComponent().getHorizontalScrollBar(); 
						if (scrollBar != e.getSource()) {
							scrollBar.getModel().setValue(e.getValue());
						}
					}
					// If the operation would only be performed outside valueIsAdjusting the other scroll panes 
					// would not be moved while the scroll bar is dragged. 
				}
			};

			
	private MultipleAlignmentsContainer independentComponent;
	private List<JSplitPane> splitPanes = new ArrayList<JSplitPane>();
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the alignment area class that uses this instance to display its contents 
	 *        in a Swing GUI
	 */
	public SwingMultipleAlignmentsContainer(MultipleAlignmentsContainer independentComponent) {
		super();
		this.independentComponent = independentComponent;
		initGUI();
	}
	
	
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		addComponentListener(new ComponentAdapter() {
//					@Override
//					public void componentResized(ComponentEvent e) {
//						redistributeHeight();
//						//TODO Implement distributeNewHeight() in future versions to save user defined splitter positions during resize
//					}
//				});
		
		adoptChildAreas();
	}
	
	
	/**
	 * Creates the specified number of nested split panes. (If the list contains more split panes than {@code count}
	 * the additional ones are removed.)
	 * 
	 * @param count - the number of split panes to be created
	 */
	private void createSplitPanes(int count) {
		if (splitPanes.size() < count) {
			count -= splitPanes.size();
			for (int i = 0; i < count; i++) {
				JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				splitPane.setDividerSize(AlignmentArea.DIVIDER_WIDTH);
				splitPanes.add(splitPane);
			}
		}
		else if (splitPanes.size() > count) {
			count = splitPanes.size() - count;
			for (int i = 0; i < count; i++) {
				splitPanes.remove(splitPanes.size() - 1);
			}
		}
		
		for (int i = 0; i < splitPanes.size() - 1; i++) {
			JSplitPane splitPane = splitPanes.get(i);
			splitPane.setTopComponent(null);  //TODO necessary?
			splitPane.setBottomComponent(splitPanes.get(i + 1));
		}

		JSplitPane splitPane = splitPanes.get(splitPanes.size() - 1);
		splitPane.setTopComponent(null);  //TODO necessary?
		splitPane.setBottomComponent(null);
	}


	private SwingAlignmentArea createAlignmentArea(int index, boolean hideHorizintalScrollBar) {
  	SwingAlignmentArea result = getIndependentComponent().get(index).createSwingComponent();
  	result.setHideHorizontalScrollBar(hideHorizintalScrollBar);
  	return result;
	}
	
	
	@Override
	public void adoptChildAreas() {
		// Remove components and listeners:
		removeAll();
		for (int i = 0; i < getIndependentComponent().size(); i++) {
			getIndependentComponent().get(i).createSwingComponent().getHorizontalScrollBar().removeAdjustmentListener(
					SCROLL_SYNC_LISTENER); 
		}		
		
		// Create split panes and add components:
		if (getIndependentComponent().size() > 1) {
		  createSplitPanes(getIndependentComponent().size() - 1);

		  for (int i = 0; i < splitPanes.size() - 1; i++) {
				splitPanes.get(i).setTopComponent(createAlignmentArea(i, true));
			}
		  
		  JSplitPane lastSplitPane = splitPanes.get(splitPanes.size() - 1);
		  lastSplitPane.setTopComponent(createAlignmentArea(getIndependentComponent().size() - 2, true));
		  lastSplitPane.setBottomComponent(createAlignmentArea(getIndependentComponent().size() - 1, false));
		  
		  add(splitPanes.get(0));
		}
		else {
			splitPanes.clear();
			if (getIndependentComponent().size() == 1) {
				add(getIndependentComponent().get(0).createSwingComponent());
		  }
		}

		// Add scroll synchronize listeners:
		for (int i = 0; i < getIndependentComponent().size(); i++) {
			getIndependentComponent().get(i).createSwingComponent().getHorizontalScrollBar().addAdjustmentListener(
					SCROLL_SYNC_LISTENER); 
		}		
	}


	@Override
	public MultipleAlignmentsContainer getIndependentComponent() {
		return independentComponent;
	}


//	@Override
//	@Transient
//	public Dimension getMinimumSize() {
//		if ((contentLabelArea != null) && (contentScrollPane != null) && (bottomScrollPane != null)) {
//			Dimension result = new Dimension(contentLabelArea.getSize().width + MIN_PART_AREA_WIDTH + 
//					contentScrollPane.getVerticalScrollBar().getWidth(), 
//					3 * MIN_PART_AREA_HEIGHT + bottomScrollPane.getHorizontalScrollBar().getHeight());  //TODO Add border widths
//			System.out.println("calculating value " + result + " " + getPreferredSize() + " " + getSize());
//			return result;
//		}
//		else {
//			System.out.println("returning default value");
//			return super.getMinimumSize();
//		}
//	}


	@Override
	public ToolkitSpecificAlignmentArea getPartArea(int alignmentIndex) {
		// TODO Auto-generated method stub
		return null;
	}


  @Override
	public void redistributeHeight() {
//  	int overallHeight = headScrollPane.getViewport().getHeight() + 
//  			contentScrollPane.getViewport().getHeight() + bottomScrollPane.getViewport().getHeight();
//  	int neededHeight = headArea.getHeight() + contentArea.getHeight() + bottomArea.getHeight();
//		double headHeight = headArea.getHeight();
//		double contentHeight = contentArea.getHeight();
//		double bottomHeight = bottomArea.getHeight();
//		if (bottomScrollPane.getHorizontalScrollBar().isVisible()) {
//			overallHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
//			neededHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
//			bottomHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
//		}
//		
//  	if (overallHeight < neededHeight) {
////  		if (getIndependentComponent().isScrollHeadArea()) {
////  			headHeight = 0;
////  		}
////  		if (getIndependentComponent().isScrollBottomArea()) {
////  			bottomHeight = 0;
////  		}
//  		
//  		if (headHeight + bottomHeight + AlignmentArea.MIN_PART_AREA_HEIGHT > overallHeight) {
//  			headHeight = 0;
//  			bottomHeight = 0;
//  		}
//  		else {
//  			neededHeight -= (headHeight + bottomHeight);
//  		}
//  		
//  		double availableHeight = overallHeight - (headHeight + bottomHeight);
//  		double reduceFactor = (double)availableHeight / (double)neededHeight;
//  		if (headHeight == 0) {
//  			headHeight = headArea.getHeight() * reduceFactor;
//  		}
// 			contentHeight = contentArea.getHeight() * reduceFactor;
//  		if (bottomHeight == 0) {
//  			bottomHeight = bottomArea.getHeight() * reduceFactor;
//  			if (bottomScrollPane.getHorizontalScrollBar().isVisible()) {
//  				bottomHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
//  			}
//  		}
//  	}
//
//  	topSplitPane.setDividerLocation(headHeight / (double)overallHeight);
//		bottomSplitPane.setDividerLocation(contentHeight / (double)(overallHeight - headHeight));
	}


//	/**
//	 * Distributes the height that has become available since the last call of this method to the head, content, 
//	 * and bottom area. This method can also be used if the available height was reduces since the last call.
//	 */
//	public void distributeNewHeight() {
//		
//	}
}
