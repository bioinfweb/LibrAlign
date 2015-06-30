/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SwingAlignmentArea;
import info.bioinfweb.tic.SwingComponentFactory;
import info.bioinfweb.tic.toolkit.AbstractSwingComponent;

import javax.swing.BoxLayout;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;



/**
 * Swing specific GUI component that contains the three scroll panes that make up an alignment area.
 * 
 * @author Ben St&oumol;ver
 * @since 0.1.0
 */
public class SwingMultipleAlignmentsContainer extends AbstractSwingComponent implements ToolkitSpecificMultipleAlignmentsContainer {
	public static final int NEEDED_BORDER_WIDTH = 1;
	
	
	private final AdjustmentListener SCROLL_SYNC_LISTENER = new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					SwingComponentFactory factory = SwingComponentFactory.getInstance();
					for (int i = 0; i < getIndependentComponent().getAlignmentAreas().size(); i++) {
						JScrollBar scrollBar = ((SwingAlignmentArea)factory.getSwingComponent(
								getIndependentComponent().getAlignmentAreas().get(i))).getHorizontalScrollBar(); 
						if (scrollBar != e.getSource()) {
							scrollBar.getModel().setValue(e.getValue());
						}
					}
					// If the operation would only be performed outside valueIsAdjusting the other scroll panes 
					// would not be moved while the scroll bar is dragged. 
				}
			};

			
	private List<JSplitPane> splitPanes = new ArrayList<JSplitPane>();
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the alignment area class that uses this instance to display its contents 
	 *        in a Swing GUI
	 */
	public SwingMultipleAlignmentsContainer(MultipleAlignmentsContainer independentComponent) {
		super(independentComponent);
		initGUI();
	}
	
	
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						getIndependentComponent().redistributeHeight();
						//TODO Implement distributeNewHeight() in future versions to save user defined splitter positions during resize
					}
				});
		
		adoptChildAreas();
	}
	
	
	@Override
	public MultipleAlignmentsContainer getIndependentComponent() {
		return (MultipleAlignmentsContainer)super.getIndependentComponent();
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
  	SwingAlignmentArea result = (SwingAlignmentArea)SwingComponentFactory.getInstance().getSwingComponent(
  			getIndependentComponent().getAlignmentAreas().get(index));
  	result.setHideHorizontalScrollBar(hideHorizintalScrollBar);
  	return result;
	}
	
	
	@Override
	public void adoptChildAreas() {
		// Remove components and listeners:
		removeAll();
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		for (int i = 0; i < getIndependentComponent().getAlignmentAreas().size(); i++) {
			((SwingAlignmentArea)factory.getSwingComponent(getIndependentComponent().getAlignmentAreas().get(i))).
					getHorizontalScrollBar().removeAdjustmentListener(SCROLL_SYNC_LISTENER); 
		}		
		
		// Create split panes and add components:
		if (getIndependentComponent().getAlignmentAreas().size() > 1) {
		  createSplitPanes(getIndependentComponent().getAlignmentAreas().size() - 1);

		  for (int i = 0; i < splitPanes.size() - 1; i++) {
				splitPanes.get(i).setTopComponent(createAlignmentArea(i, true));
			}
		  
		  JSplitPane lastSplitPane = splitPanes.get(splitPanes.size() - 1);
		  lastSplitPane.setTopComponent(createAlignmentArea(getIndependentComponent().getAlignmentAreas().size() - 2, true));
		  lastSplitPane.setBottomComponent(createAlignmentArea(getIndependentComponent().getAlignmentAreas().size() - 1, false));
		  
		  add(splitPanes.get(0));
		}
		else {
			splitPanes.clear();
			if (getIndependentComponent().getAlignmentAreas().size() == 1) {
				add(SwingComponentFactory.getInstance().getSwingComponent(getIndependentComponent().getAlignmentAreas().get(0)));
		  }
		}

		// Add scroll synchronize listeners:
		for (int i = 0; i < getIndependentComponent().getAlignmentAreas().size(); i++) {
			((SwingAlignmentArea)factory.getSwingComponent(getIndependentComponent().getAlignmentAreas().get(i))).
					getHorizontalScrollBar().addAdjustmentListener(SCROLL_SYNC_LISTENER); 
		}		
	}


  @Override
	public int getAvailableHeight() {
  	int result = 0;
  	for (AlignmentArea alignmentArea : getIndependentComponent().getAlignmentAreas()) {
			result += ((SwingAlignmentArea)alignmentArea.getToolkitComponent()).getViewport().getHeight();
		}
  	return result;
	}


	@Override
	public int getNeededHeight(int alignmentIndex) {
		return getIndependentComponent().getAlignmentAreas().get(alignmentIndex).getContentArea().getSize().height +
				NEEDED_BORDER_WIDTH;
	}


	@Override
	public void setDividerLocations(int[] heights) {
  	for (int i = 0; i < splitPanes.size(); i++) {
  		JSplitPane splitPane = splitPanes.get(i);
  		splitPane.setDividerLocation(heights[i]);
			splitPane.validate();  // If this is not called, splitPanes contained in other split panes that shrink the area they are contained in, ignore the new divider location.
		}
  	// The last value in heights is not used by this method.
	}


//	/**
//	 * Distributes the height that has become available since the last call of this method to the head, content, 
//	 * and bottom area. This method can also be used if the available height was reduces since the last call.
//	 */
//	public void distributeNewHeight() {
//		
//	}
}
