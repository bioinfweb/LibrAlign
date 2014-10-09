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


import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSplitPane;



/**
 * Swing specific GUI component that contains the three scroll panes that make up an alignment area.
 * 
 * @author Ben St&oumol;ver
 * @since 0.1.0
 */
public class SwingMultipleAlignmentsContainer extends JComponent implements ToolkitSpecificMultipleAlignmentsContainer {
	private static Action VOID_ACTION = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {}
			};
	
	
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	
	private JScrollPane headScrollPane;
	private JScrollPane contentScrollPane;
	private JScrollPane bottomScrollPane;
	private JSplitPane topSplitPane;
	private JSplitPane bottomSplitPane;
	private SwingAlignmentArea headArea;
	private SwingAlignmentArea contentArea;
	private SwingAlignmentArea bottomArea;
	private AlignmentLabelArea headLabelArea;
	private AlignmentLabelArea contentLabelArea;
	private AlignmentLabelArea bottomLabelArea;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the alignment area class that uses this instance to display its contents 
	 *        in a Swing GUI
	 */
	public SwingMultipleAlignmentsContainer(AlignmentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		sequenceAreaMap = new SequenceAreaMap(independentComponent);
		initGUI();
	}
	
	
	@Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		getContentArea().scrollRectToVisible(rectangle);
	}


	@Override
	public Rectangle getVisibleAlignmentRect() {
		return getContentArea().getVisibleRect();
	}


	/**
	 * Removes the arrow key bindings, so that the alignment cursor can be moved with these keys instead without
	 * scrolling.
	 * 
	 * @param scrollPane - the the scroll pane to remove the key bindings from
	 */
	private void removeArrowKeyBindings(JScrollPane scrollPane) {
	  scrollPane.getActionMap().put("unitScrollLeft", VOID_ACTION);  // remove() does not work here.
	  scrollPane.getActionMap().put("unitScrollUp", VOID_ACTION);
		scrollPane.getActionMap().put("unitScrollRight", VOID_ACTION);
	  scrollPane.getActionMap().put("unitScrollDown", VOID_ACTION);
	}
	
	
	private JScrollPane createScrollPane() {
		JScrollPane result = new JScrollPane();
		result.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		result.setBorder(null);
		removeArrowKeyBindings(result);
		return result;
	}
	
	
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						redistributeHeight();
						//TODO Implement distributeNewHeight() in future versions to save user defined splitter positions during resize
					}
				});
		
		headScrollPane = createScrollPane();
		headArea = new SwingAlignmentArea();
		headScrollPane.setViewportView(headArea);
		headLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.TOP); 
		headLabelArea.setAlignmentPartArea(headArea);
		headScrollPane.setRowHeaderView(headLabelArea.createSwingComponent());

		contentScrollPane = createScrollPane();
		contentArea = new SwingAlignmentArea();
		contentScrollPane.setViewportView(contentArea);
		contentLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.SEQUENCE);
		contentLabelArea.setAlignmentPartArea(contentArea);
		contentScrollPane.setRowHeaderView(contentLabelArea.createSwingComponent());
		
		bottomScrollPane = createScrollPane();
		bottomScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bottomArea = new SwingAlignmentArea();
		bottomScrollPane.setViewportView(bottomArea);
		bottomLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.BOTTOM); 
		bottomLabelArea.setAlignmentPartArea(bottomArea);
		bottomScrollPane.setRowHeaderView(bottomLabelArea.createSwingComponent());
		
		AdjustmentListener listener = new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (headScrollPane.getHorizontalScrollBar() != e.getSource()) {
							headScrollPane.getHorizontalScrollBar().getModel().setValue(e.getValue());
						}
						if (contentScrollPane.getHorizontalScrollBar() != e.getSource()) {
							contentScrollPane.getHorizontalScrollBar().getModel().setValue(e.getValue());
						}
						if (bottomScrollPane.getHorizontalScrollBar() != e.getSource()) {
							bottomScrollPane.getHorizontalScrollBar().getModel().setValue(e.getValue());
						}
						// If the operation would only be performed outside valueIsAdjusting the other scroll panes 
						// would not be moved while the scroll bar is dragged. 
					}
				};
		headScrollPane.getHorizontalScrollBar().addAdjustmentListener(listener);
		contentScrollPane.getHorizontalScrollBar().addAdjustmentListener(listener);
		bottomScrollPane.getHorizontalScrollBar().addAdjustmentListener(listener);
		// If one model for all scroll bars is set the scroll bar disappears after the first move. (TODO Why?)		
		
		topSplitPane = new JSplitPane();
		topSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		bottomSplitPane = new JSplitPane();
		bottomSplitPane.setResizeWeight(1.0);
		bottomSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		bottomSplitPane.setDividerSize(AlignmentArea.DIVIDER_WIDTH);

		topSplitPane.setTopComponent(headScrollPane);
		topSplitPane.setBottomComponent(bottomSplitPane);
		topSplitPane.setDividerSize(AlignmentArea.DIVIDER_WIDTH);
		
		bottomSplitPane.setTopComponent(contentScrollPane);
		bottomSplitPane.setBottomComponent(bottomScrollPane);

		
		reinsertSubelements();
		add(topSplitPane);
	}
	
	
	public JScrollPane getHeadScrollPane() {
		return headScrollPane;
	}
	
	
	public JScrollPane getContentScrollPane() {
		return contentScrollPane;
	}
	
	
	public JScrollPane getBottomScrollPane() {
		return bottomScrollPane;
	}
	
	
	public JSplitPane getTopSplitPane() {
		return topSplitPane;
	}
	
	
	public JSplitPane getBottomSplitPane() {
		return bottomSplitPane;
	}


	public SwingAlignmentArea getHeadArea() {
		return headArea;
	}


	public SwingAlignmentArea getContentArea() {
		return contentArea;
	}


	public SwingAlignmentArea getBottomArea() {
		return bottomArea;
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public ToolkitSpecificAlignmentArea getPartArea(DataAreaListType position) {
		switch (position) {
			case TOP:
				return getHeadArea();
			case SEQUENCE:
				return getContentArea();
			default:
				return getBottomArea();
		}
	}


	@Override
	public SequenceArea getSequenceAreaByID(int sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	@Override
	public void reinsertSubelements() {
		// Head elements:
		getHeadArea().removeAll();
		getHeadArea().addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		
		// Content elements:
		getContentArea().removeAll();
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			getContentArea().add(sequenceAreaMap.get(id).createSwingComponent());
			getContentArea().addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}

		// Bottom elements:
		getBottomArea().removeAll();
		getBottomArea().addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
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
	public void redistributeHeight() {
  	int overallHeight = headScrollPane.getViewport().getHeight() + 
  			contentScrollPane.getViewport().getHeight() + bottomScrollPane.getViewport().getHeight();
  	int neededHeight = headArea.getHeight() + contentArea.getHeight() + bottomArea.getHeight();
		double headHeight = headArea.getHeight();
		double contentHeight = contentArea.getHeight();
		double bottomHeight = bottomArea.getHeight();
		if (bottomScrollPane.getHorizontalScrollBar().isVisible()) {
			overallHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
			neededHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
			bottomHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
		}
		
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
  			headHeight = headArea.getHeight() * reduceFactor;
  		}
 			contentHeight = contentArea.getHeight() * reduceFactor;
  		if (bottomHeight == 0) {
  			bottomHeight = bottomArea.getHeight() * reduceFactor;
  			if (bottomScrollPane.getHorizontalScrollBar().isVisible()) {
  				bottomHeight += bottomScrollPane.getHorizontalScrollBar().getHeight();
  			}
  		}
  	}

  	topSplitPane.setDividerLocation(headHeight / (double)overallHeight);
		bottomSplitPane.setDividerLocation(contentHeight / (double)(overallHeight - headHeight));
	}


//	/**
//	 * Distributes the height that has become available since the last call of this method to the head, content, 
//	 * and bottom area. This method can also be used if the available height was reduces since the last call.
//	 */
//	public void distributeNewHeight() {
//		
//	}
}
