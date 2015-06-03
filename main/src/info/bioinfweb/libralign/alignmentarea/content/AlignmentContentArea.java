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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.DataAreaLocation;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * Toolkit independent component displaying the contents (not the labels) of a single alignment and its attached 
 * data areas.
 * <p>
 * This component is used a child component of {@link AlignmentArea} and in contrast to {@link AlignmentArea}
 * does not contain a scroll container, but acts as the scrolled component. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * 
 * @see AlignmentArea
 * @see AlignmentLabelArea
 */
public class AlignmentContentArea extends TICComponent {
	private AlignmentArea owner;
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * If {@code code} is part of a {@link MultipleAlignmentsContainer} the shared edit settings of this container
	 * will automatically be used by the returned instance.
	 * 
	 * @param owner - the alignment area component that will be containing the return instance
	 */
	public AlignmentContentArea(AlignmentArea owner) {
		super();
		this.owner = owner;
  }


	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the rectangle in the paint coordinate system of scrolled area displaying the sequences, that contains
	 * all cells currently occupied by the alignment cursor. 
	 * <p>
	 * If the last row has associated data areas, the height of these areas is also included in the rectangle. 
	 * 
	 * @return a rectangle with paint coordinates
	 */
	public Rectangle getCursorRectangle() {
		SelectionModel selection = getOwner().getSelection();
		int y = paintYByRow(selection.getCursorRow());
		int width = 0;
		if (selection.getCursorColumn() < getOwner().getAlignmentModel().getMaxSequenceLength()) {  // There is no more token behind the cursor, if it is located at the right end of the alignment. 
			width = (int)Math.round(getOwner().getPaintSettings().getTokenWidth(selection.getCursorColumn()));
		}
		Rectangle result = new Rectangle(paintXByColumn(selection.getCursorColumn()), y, width, 
				paintYByRow(selection.getCursorRow() + selection.getCursorHeight()) - y); 
		if (selection.getCursorRow() + selection.getCursorHeight() - 1 == 
				getOwner().getAlignmentModel().getSequenceCount() - 1) {
			
			result.height += getOwner().getPaintSettings().getTokenHeight();  // Add height of the last row, because the return value of paintYByRow(maxIndex + 1) is equal to paintYByRow(maxIndex).
		}
		return result; 
	}
	
	
	@Override
	public Dimension getSize() {
		Dimension result = new Dimension();
		result.width = paintXByColumn(getOwner().getGlobalMaxSequenceLength());  //TODO Test if this is equivalent to previous implementation.
		result.height = getOwner().getDataAreas().getVisibleAreaHeight();
		if (getOwner().hasAlignmentModel()) {
			result.height += getOwner().getAlignmentModel().getSequenceCount() * getOwner().getPaintSettings().getTokenHeight();
		}
		return result;
	}

	
	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.	


	@Override
	protected JComponent doCreateSwingComponent() {
		SwingAlignmentContentArea result = new SwingAlignmentContentArea(this);
		addCursorScrollListener();
		return result;
	}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		SWTAlignmentContentArea result = new SWTAlignmentContentArea(parent, style, this);
		addCursorScrollListener();
		return result;
	}


	@Override
	public ToolkitSpecificAlignmentContentArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentContentArea)super.getToolkitComponent();
	}
	
	
	private void addCursorScrollListener() {
		getOwner().getSelection().addSelectionListener(new SelectionListener() {
					@Override
					public void selectionChanged(SelectionChangeEvent event) {
						getOwner().scrollCursorToVisible();
					}
				});
	}
	
	
	/**
	 * Returns the column containing the specified x coordinate.
	 *  
	 * @param x - the paint coordinate
	 * @return the alignment column
	 */
	public int columnByPaintX(int x) {
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
		}
		else {
			return Math.max(0, Math.min(getOwner().getGlobalMaxSequenceLength() - 1,
					(int)((x - getOwner().getDataAreas().getGlobalMaxLengthBeforeStart()) / getOwner().getPaintSettings().getTokenWidth(0))));  //TODO Catch IllegalStateException?
		}
	}


	/**
	 * Returns the left most x-coordinate of the area the specified column is painted in relative to the
	 * component on which the sequences are painted. Use this method to convert between cell indices and 
	 * paint coordinates.
	 * 
	 * @param column - the column painted at the returned x-position
	 * @return a value >= 0
	 */
	public int paintXByColumn(int column) {
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
		}
		else {
			return (int)((column /*- 1*/) * getOwner().getPaintSettings().getTokenWidth(0)) + getOwner().getDataAreas().getGlobalMaxLengthBeforeStart();  //TODO Catch IllegalStateException?
		}
	}


	/**
	 * Returns the row index of the sequence displayed at the specified y coordinate considering the current order
	 * of sequences. If a data area is displayed at the specified position, the row of the associated sequence is returned
	 * anyway.
	 * 
	 * @param y - the y coordinate relative to the alignment part area containing the sequence areas
	 * @return a valid sequence position.
	 */
	public int rowByPaintY(int y) {
		AlignmentSubArea subArea = getToolkitComponent().getAreaByY(y);
		if (subArea instanceof DataArea) {
			DataAreaLocation location = ((DataArea)subArea).getList().getLocation();
			if (location.getListType().equals(DataAreaListType.SEQUENCE)) {
				subArea = getToolkitComponent().getSequenceAreaByID(location.getSequenceID());
			}
		}
		if (subArea instanceof SequenceArea) {
			return getOwner().getSequenceOrder().indexByID(((SequenceArea)subArea).getSeqenceID());
		}
		else if (y < 0) {
			return 0;
		}
		else {
			return getOwner().getAlignmentModel().getSequenceCount() - 1;
		}
	}
	

	/**
	 * Returns the top most y-coordinate of the area the specified row is painted in relative to the
	 * component on which the sequences are painted. Use this method to convert between cell indices and 
	 * paint coordinates.
	 * <p>
	 * If an index lower than zero or greater than the highest index is specified the y-coordinate of the first
	 * or the last sequence is returned accordingly.
	 * 
	 * @param row - the row painted at the returned x-position
	 * @return a value >= 0
	 */
	public int paintYByRow(int row) {
		row = Math.max(0, Math.min(getOwner().getAlignmentModel().getSequenceCount() - 1, row));
    return getToolkitComponent().getSequenceAreaByID(getOwner().getSequenceOrder().idByIndex(row)).getLocationInParent().y;		
	}
	
	
	/**
	 * Calculates the y coordinate relative to the alignment content area, which contains the specified sequence area.
	 * 
	 * @param sequenceArea - the sequence area where {@code relativeY} belongs to 
	 * @param relativeY - the y coordinate relative to {@code sequenceArea}
	 * @return the y coordinate relative to the parent instance of {@link ToolkitSpecificAlignmentArea}
	 * @throws IllegalStateException if neither or Swing or a SWT component has been created for the specified sequence
	 *         area before the call of this method
	 */
	public int alignmentPartY(SequenceArea sequenceArea, int relativeY) {
		switch (sequenceArea.getCurrentToolkit()) {  // SequenceAreas need to be direct children of the ToolkitSpecificAlignmentPartAreas for this method to work.
			case SWING:
				return ((Component)sequenceArea.getToolkitComponent()).getLocation().y + relativeY;
			case SWT:
				return ((Composite)sequenceArea.getToolkitComponent()).getLocation().y + relativeY;
			default:
				throw new IllegalStateException("No Swing or SWT component of the specfied sequence area has yet been created.");
		}
	}
}
