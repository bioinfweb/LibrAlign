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


import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;

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
public class SwingAlignmentOverviewArea extends JComponent implements ToolkitSpecificAlignmentOverviewArea {
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	
	private JScrollPane headScrollPane;
	private JScrollPane contentScrollPane;
	private JScrollPane bottomScrollPane;
	private JSplitPane topSplitPane;
	private JSplitPane bottomSplitPane;
	private SwingAlignmentPartArea headArea;
	private SwingAlignmentPartArea contentArea;
	private SwingAlignmentPartArea bottomArea;
	private AlignmentLabelArea headLabelArea;
	private AlignmentLabelArea contentLabelArea;
	private AlignmentLabelArea bottomLabelArea;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the alignment area class that uses this instance to display its contents 
	 *        in a Swing GUI
	 */
	public SwingAlignmentOverviewArea(AlignmentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		sequenceAreaMap = new SequenceAreaMap(independentComponent);
		initGUI();
	}
	
	
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		headScrollPane = new JScrollPane();
		headScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		headArea = new SwingAlignmentPartArea();
		headScrollPane.setViewportView(headArea);
		headLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.TOP); 
		headScrollPane.setRowHeaderView(headLabelArea.createSwingComponent());

		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentArea = new SwingAlignmentPartArea();
		contentScrollPane.setViewportView(contentArea);
		contentLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.SEQUENCE); 
		contentScrollPane.setRowHeaderView(contentLabelArea.createSwingComponent());
		
		bottomScrollPane = new JScrollPane();
		bottomArea = new SwingAlignmentPartArea();
		bottomScrollPane.setViewportView(bottomArea);
		bottomLabelArea = new AlignmentLabelArea(getIndependentComponent(), DataAreaListType.BOTTOM); 
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
				// If the operation would only be performed outside valueIsAdjusting the other scroll panes would not be moved while the scroll bar is dragged. 
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


	public SwingAlignmentPartArea getHeadArea() {
		return headArea;
	}


	public SwingAlignmentPartArea getContentArea() {
		return contentArea;
	}


	public SwingAlignmentPartArea getBottomArea() {
		return bottomArea;
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public ToolkitSpecificAlignmentPartArea getPartArea(DataAreaListType position) {
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
}
