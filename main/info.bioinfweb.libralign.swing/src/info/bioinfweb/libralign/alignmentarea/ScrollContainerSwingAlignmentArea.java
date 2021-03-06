/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;
import info.bioinfweb.tic.SwingComponentFactory;
import info.bioinfweb.tic.toolkit.JScrollPaneToolkitComponent;
import info.bioinfweb.tic.toolkit.SwingComponentTools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;



/**
 * The Swing component rendering the top central or bottom part of an {@link AlignmentArea}.
 * <p>
 * This class is meant for internal use by {@link AlignmentArea} and should not be instantiated directly.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class ScrollContainerSwingAlignmentArea extends JScrollPane implements ToolkitSpecificAlignmentArea, JScrollPaneToolkitComponent {
	private static Action VOID_ACTION = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {}
	};


	private AlignmentArea independentComponent;
	
	
	public ScrollContainerSwingAlignmentArea(AlignmentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		init();
		SwingComponentTools.registerScrollEventForwarders(this);
		
		//TODO Is this really needed? If so, also add it to SWT again. (It was removed from AbstractSWTAlignmentArea.java in r869.)
//		addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {  // Sets the focus to an according sequence area, if the user clicks outside of the content area and moves the cursor.
//				if (getIndependentComponent().hasAlignmentModel() && (getIndependentComponent().getAlignmentModel().getSequenceCount() > 0)) {
//					AlignmentContentArea contentArea = getIndependentComponent().getContentArea();
//					int row = contentArea.rowByPaintY(e.getY());
//					SequenceArea sequenceArea = contentArea.getSequenceAreaByID(
//							getIndependentComponent().getSequenceOrder().idByIndex(row));
//					if (sequenceArea != null) {
//						getIndependentComponent().getSelection().setNewCursorPosition(contentArea.columnByPaintX(e.getX()), row);
//						((JComponent)sequenceArea.getToolkitComponent()).requestFocusInWindow();
//					}
//				}
//			}
//		});
	}
	

	@Override
	public JComponent getSwingComponent() {
		return this;
	}


	@Override
	public JScrollPane getScrollPane() {
		return this;
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	private void init() {
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		setBorder(null);
		removeArrowKeyBindings();
		
		setViewportView(SwingComponentFactory.getInstance().getSwingComponent(getIndependentComponent().getContentArea()));
		setRowHeaderView(SwingComponentFactory.getInstance().getSwingComponent(getIndependentComponent().getLabelArea()));  // Swing component of content area must already exist when this is called.
	}


	/**
	 * Removes the arrow key bindings, so that the alignment cursor can be moved with these keys instead without
	 * scrolling.
	 */
	private void removeArrowKeyBindings() {
		getActionMap().put("unitScrollLeft", VOID_ACTION);  // remove() does not work here.
	  getActionMap().put("unitScrollUp", VOID_ACTION);
		getActionMap().put("unitScrollRight", VOID_ACTION);
	  getActionMap().put("unitScrollDown", VOID_ACTION);
	}
	
	
	@Override
	public Dimension getMaximumSize() {
		return SwingComponentTools.getMaximumSize(this, super.getMaximumSize());
	}


	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		if (hideHorizontalScrollBar) {
			setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		else {
			setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
	}
}
