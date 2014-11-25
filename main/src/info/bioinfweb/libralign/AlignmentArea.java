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
package info.bioinfweb.libralign;


import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentareacomponents.SWTAlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.SwingAlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.label.AlignmentLabelArea;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentArea extends TICComponent {
	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static final int DIVIDER_WIDTH = 2;
	
	public static final int MIN_PART_AREA_HEIGHT = 5;
	
	
	private MultipleAlignmentsContainer container = null;
	private AlignmentContentArea alignmentContentArea;
	private AlignmentLabelArea alignmentLabelArea;
	private boolean allowVerticalScrolling = true;
	private Rectangle lastCursorRectangle = null;
	
	
	/**
	 * Creates a new instance of this class that shall not part of a {@link MultipleAlignmentsContainer}.
	 * <p>
	 * If you want to create an alignment area that shall be inserted into a {@link MultipleAlignmentsContainer}
	 * use {@link #AlignmentArea(MultipleAlignmentsContainer)} instead.
	 */
	public AlignmentArea() {
		this(null);
	}
	
	
	/**
	 * Creates a new instance of this class to be inserted into a {@link MultipleAlignmentsContainer}.
	 * 
	 * @param container - the container where the returned instance will be contained in
	 */
	public AlignmentArea(MultipleAlignmentsContainer container) {
		super();
		this.container = container;
		alignmentContentArea = new AlignmentContentArea(this);
		alignmentLabelArea = new AlignmentLabelArea(this);
	}


	/**
	 * Returns the container this object is contained in.
	 * 
	 * @return the container instance or {@code null} if this instance is used as a stand-alone component.
	 */
	public MultipleAlignmentsContainer getContainer() {
		return container;
	}
	
	
	public boolean hasContainer() {
		return getContainer() != null;
	}


	public AlignmentContentArea getContentArea() {
		return alignmentContentArea;
	}


	public AlignmentLabelArea getLabelArea() {
		return alignmentLabelArea;
	}


	public boolean isAllowVerticalScrolling() {
		return allowVerticalScrolling;
	}


	public void setAllowVerticalScrolling(boolean allowVerticalScrolling) {
		this.allowVerticalScrolling = allowVerticalScrolling;
		//TODO redistribute size
	}


	public void scrollCursorToVisible() {
		Rectangle visibleRectangle = getToolkitComponent().getVisibleAlignmentRect();
		Rectangle currentRectangle = getContentArea().getCursorRectangle();
		Rectangle scrollRectangle = new Rectangle(currentRectangle);
		int dy = currentRectangle.height - visibleRectangle.height;
		if ((dy > 0) && (lastCursorRectangle != null)) {
			scrollRectangle.height -= dy;
			if (lastCursorRectangle.y == currentRectangle.y) {  // Not moved upwards (= downwards).
				scrollRectangle.y += dy;
			}
		}
		getToolkitComponent().scrollAlignmentRectToVisible(scrollRectangle);
		lastCursorRectangle = currentRectangle;
	}
	
	
	@Override
	protected JComponent doCreateSwingComponent() {
		return new SwingAlignmentArea(this);
	}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		return new SWTAlignmentArea(parent, style, this, false);  // Possible hiding of horizontal scroll bar needs to be adjusted later on.
	}


	@Override
	public SWTAlignmentArea createSWTWidget(Composite parent, int style) {
		return (SWTAlignmentArea)super.createSWTWidget(parent, style);
	}


	@Override
	public SwingAlignmentArea createSwingComponent() {
		return (SwingAlignmentArea)super.createSwingComponent();
	}


	@Override
	public ToolkitSpecificAlignmentArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentArea)super.getToolkitComponent();
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.


	/**
	 * Returns the size of the underlying toolkit specific component. If no component has been created yet,
	 * (0, 0) is returned.
	 * 
	 * @see info.bioinfweb.commons.tic.TICComponent#getSize()
	 */
	@Override
	public Dimension getSize() {
		switch (getCurrentToolkit()) {
			case SWING:
				return ((JComponent)getToolkitComponent()).getPreferredSize();  //TODO correct size?
			case SWT:
				Point point = ((Composite)getToolkitComponent()).getSize();
				return new Dimension(point.x, point.y);
			default:
			  return new Dimension(0, 0);
		}
	}
}
