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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;



/**
 * The Swing component rendering the top central or bottom part of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SwingAlignmentArea extends JScrollPane implements ToolkitSpecificAlignmentArea {
	private static Action VOID_ACTION = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {}
			};


	private AlignmentArea independentComponent;
	
	
	public SwingAlignmentArea(AlignmentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		init();
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
		
		setViewportView(getIndependentComponent().getContentArea().createSwingComponent());
		setRowHeaderView(getIndependentComponent().getLabelArea().createSwingComponent());
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
	//@Transient
	public Dimension getPreferredSize() {
		//TODO AWT tree lock?;
		Dimension result = new Dimension(0, 0);
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Dimension componentSize = components[i].getPreferredSize();
			result.width = Math.max(result.width, componentSize.width);
			result.height += componentSize.height;
		}
		return result;
	}


	@Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		((SwingAlignmentContentArea)
				getIndependentComponent().getContentArea().getToolkitComponent()).scrollRectToVisible(rectangle);
	}


	@Override
	public Rectangle getVisibleAlignmentRect() {
		return ((SwingAlignmentContentArea)
				getIndependentComponent().getContentArea().getToolkitComponent()).getVisibleRect();
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