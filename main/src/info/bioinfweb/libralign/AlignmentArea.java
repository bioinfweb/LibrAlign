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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentareacomponents.SWTAlignmentOverviewArea;
import info.bioinfweb.libralign.alignmentareacomponents.SequenceArea;
import info.bioinfweb.libralign.alignmentareacomponents.SwingAlignmentOverviewArea;
import info.bioinfweb.libralign.alignmentareacomponents.ToolkitSpecificAlignmentOverviewArea;
import info.bioinfweb.libralign.alignmentareacomponents.ToolkitSpecificAlignmentPartArea;
import info.bioinfweb.libralign.cursor.AlignmentCursor;
import info.bioinfweb.libralign.cursor.CursorChangeEvent;
import info.bioinfweb.libralign.cursor.CursorListener;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.DataAreaLocation;
import info.bioinfweb.libralign.dataarea.DataAreaModel;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;
import info.bioinfweb.libralign.selection.SelectionModel;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentArea extends TICComponent implements SequenceDataChangeListener {
	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static int DIVIDER_WIDTH = 2;
	public static final int MIN_PART_AREA_HEIGHT = 5;
	
	public static final int COMPOUND_WIDTH = 10;
	public static final int COMPOUND_HEIGHT = 14;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.PLAIN;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;

	
	private SequenceDataProvider<?> sequenceProvider = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private WorkingMode workingMode = WorkingMode.VIEW;  //TODO Should this better be part of the controller (key and mouse listener)?
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Initial value should be adjusted when the data type of the specified provider is known.
	private SelectionModel selection = new SelectionModel(this);
	private AlignmentCursor cursor = new AlignmentCursor(this);
	private Rectangle lastCursorRectangle = null;
	private DataAreaModel dataAreas = new DataAreaModel();
	private float zoomX = 1f;
	private float zoomY = 1f;
	private boolean scrollHeadArea = false;
	private boolean scrollBottomArea = false;
	private int compoundWidth = COMPOUND_WIDTH;
	private int compoundHeight = COMPOUND_HEIGHT;	
	private Font compoundFont = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	
	
	public AlignmentArea() {
		super();
		dataAreas.addListener(new DataAreaModelListener() {
					@Override
					public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
	}


	public boolean hasSequenceProvider() {
		return getSequenceProvider() != null;
	}
	
	
	public SequenceDataProvider<?> getSequenceProvider() {
		return sequenceProvider;
	}
	
	
	/**
	 * Changes the sequence provider used by this instance.
	 * 
	 * @param sequenceProvider - the new data provider to use from now on
	 * @param moveListeners - Specify {@code true} here, if you want the {@link SequenceDataChangeListener}s
	 *        attached to the current sequence provider to be moved to the specified {@code sequenceProvider},
	 *        {@code false} if the listeners shall remain attached to the old sequence provider. (This instance
	 *        is also registered as a listener and is always moved to the new object, no matter which value is
	 *        specified here.)
	 * @return the previous sequence provider that has been replaced or {@code null} if there was no provider 
	 *         before
	 */
	public SequenceDataProvider<?> setSequenceProvider(SequenceDataProvider<?> sequenceProvider, 
			boolean moveListeners) {
		
		SequenceDataProvider<?> result = this.sequenceProvider;
		if (!sequenceProvider.equals(this.sequenceProvider)) {
			if (this.sequenceProvider != null) {
				if (moveListeners) {  // Move all listeners
					sequenceProvider.getChangeListeners().addAll(this.sequenceProvider.getChangeListeners());
					this.sequenceProvider.getChangeListeners().clear();
				}
				else {  // Move this instance as the listener anyway:
					this.sequenceProvider.getChangeListeners().remove(this);
					sequenceProvider.getChangeListeners().add(this);
				}
			}
			
			this.sequenceProvider = sequenceProvider;
			getSequenceOrder().setSourceSequenceOrder();  // Update sequence names
			if (hasToolkitComponent()) {
				getToolkitComponent().reinsertSubelements();
			}
			
      // Fire events for listener move after the process finished
			if (this.sequenceProvider != null) {
				if (moveListeners) {
					Iterator<SequenceDataChangeListener> iterator = this.sequenceProvider.getChangeListeners().iterator();
					while (iterator.hasNext()) {
						iterator.next().afterProviderChanged(result, this.sequenceProvider);
					}
				}
				else {
					afterProviderChanged(result, this.sequenceProvider);
				}
			}
		}
		return result;
	}
	
	
	public SequenceOrder getSequenceOrder() {
		return sequenceOrder;
	}


	public SequenceColorSchema getColorSchema() {
		return colorSchema;
	}


	public WorkingMode getWorkingMode() {
		return workingMode;
	}
	
	
	public void setWorkingMode(WorkingMode workingMode) {
		this.workingMode = workingMode;
	}
	
	
	public AlignmentDataViewMode getViewMode() {
		return viewMode;
	}
	
	
	public void setViewMode(AlignmentDataViewMode viewMode) {
		this.viewMode = viewMode;
		//TODO repaint
	}


	/**
	 * Returns the selection model used by this object.
	 */
	public SelectionModel getSelection() {
		return selection;
	}

	
	/**
	 * Returns the alignment cursor.
	 */
	public AlignmentCursor getCursor() {
		return cursor;
	}


	/**
	 * Calculates the y coordinate relative to the alignment part area the specified sequence area is contained in.
	 * 
	 * @param sequenceArea - the sequence area where {@code relativeY} belongs to 
	 * @param relativeY - the y coordinate relative to {@code sequenceArea}
	 * @return the y coordinate relative to the parent instance of {@link ToolkitSpecificAlignmentPartArea}
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
				throw new IllegalStateException("No Swing or SWT component of the specfied sequence area has already been created.");
		}
	}
	
	
	/**
	 * Returns the data area model used by this object containing all data areas attached 
	 * to this alignment. 
	 */
	public DataAreaModel getDataAreas() {
		return dataAreas;
	}


	public float getZoomX() {
		return zoomX;
	}


	public float getZoomY() {
		return zoomY;
	}


	public void setZoomX(float zoomX) {
		setZoom(zoomX, getZoomY());
	}
	
	
	public void setZoomY(float zoomY) {
		setZoom(getZoomX(), zoomY);
	}
	
	
	public void setZoom(float zoomX, float zoomY) {
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		compoundWidth = Math.round(COMPOUND_WIDTH * zoomX);
		compoundHeight = Math.round(COMPOUND_HEIGHT * zoomY);
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}
	
	
	public boolean isScrollHeadArea() {
		return scrollHeadArea;
	}


	public void setScrollHeadArea(boolean scrollHeadArea) {
		this.scrollHeadArea = scrollHeadArea;
		//TODO update GUI
	}


	public boolean isScrollBottomArea() {
		return scrollBottomArea;
	}


	public void setScrollBottomArea(boolean scrollBottomArea) {
		this.scrollBottomArea = scrollBottomArea;
		//TODO update GUI
	}


	private void calculateFont() {
		compoundFont = new Font(FONT_NAME, FONT_STYLE, Math.round(Math.min(
				compoundHeight * (COMPOUND_WIDTH / COMPOUND_HEIGHT), compoundWidth) * FONT_SIZE_FACTOR));
	}

	
	/**
	 * Returns the current width of a compound depending on the current zoom factor.
	 * 
	 * @return a float value greater than zero
	 */
	public int getCompoundWidth() {
		return compoundWidth;
	}


	public void setCompoundWidth(int compoundWidth) {
		this.compoundWidth = compoundWidth;
		zoomX = compoundWidth / COMPOUND_WIDTH;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}


	/**
	 * Returns the current height of a compound depending on the current zoom factor.
	 * 
	 * @return a float value greater than zero
	 */
	public int getCompoundHeight() {
		return compoundHeight;
	}
	
	
	public void setCompoundHeight(int compoundHeight) {
		this.compoundHeight = compoundHeight;
		zoomY = compoundHeight / COMPOUND_HEIGHT;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}


	/**
	 * Indicates whether compounds should are printed as text.
	 * <p>
	 * If any zoom factor is to low, each compound is only painted as a rectangle without any text in it.
	 * Text output will not be done, if the font size would be below {@link #MIN_FONT_SIZE}.
	 * </p>
	 * 
	 * @return {@code true}, if any compound text will be painted, {@code false} otherwise
	 */
	public boolean isPaintCompoundText() {
		return getCompoundFont().getSize() >= MIN_FONT_SIZE;
	}
	
	
	public Font getCompoundFont() {
		return compoundFont;
	}
	
	
	/**
	 * Returns the column containing the specified x coordinate.
	 *  
	 * @param x - the paint coordinate
	 * @return the alignment column
	 */
	public int columnByPaintX(int x) {
		return Math.max(0, Math.min(getSequenceProvider().getMaxSequenceLength() - 1, 
				(int)((x - getDataAreas().getMaxLengthBeforeStart()) / getCompoundWidth())));
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
		return (int)((column - 1) * getCompoundWidth()) + getDataAreas().getMaxLengthBeforeStart();
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
		AlignmentSubArea subArea = getToolkitComponent().getPartArea(DataAreaListType.SEQUENCE).getAreaByY(y);
		if (subArea instanceof DataArea) {
			DataAreaLocation location = ((DataArea)subArea).getList().getLocation();
			if (location.getListType().equals(DataAreaListType.SEQUENCE)) {
				subArea = getToolkitComponent().getSequenceAreaByID(location.getSequenceID());
			}
		}
		if (subArea instanceof SequenceArea) {
			return getSequenceOrder().indexByID(((SequenceArea)subArea).getSeqenceID());
		}
		else if (y < 0) {
			return 0;
		}
		else {
			return getSequenceProvider().getSequenceCount() - 1;
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
		row = Math.max(0, Math.min(getSequenceProvider().getSequenceCount() - 1, row));
    return getToolkitComponent().getSequenceAreaByID(getSequenceOrder().idByIndex(row)).getLocationInParent().y;		
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
		int y = paintYByRow(getCursor().getRow());
		Rectangle result = new Rectangle(paintXByColumn(getCursor().getColumn()), y,
				getCompoundWidth(), paintYByRow(getCursor().getRow() + getCursor().getHeight()) - y); 
		if (getCursor().getRow() + getCursor().getHeight() - 1 == getSequenceProvider().getSequenceCount() - 1) {
			result.height += getCompoundHeight();  // Add height of the last row, because the return value of paintYByRow(maxIndex + 1) is equal to paintYByRow(maxIndex).
		}
		return result; 
	}
	
	
	public void scrollCursorToVisible() {
		Rectangle visibleRectangle = getToolkitComponent().getVisibleAlignmentRect();
		Rectangle currentRectangle = getCursorRectangle();
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
	
	
	private void addCursorScrollListener() {
		getCursor().addCursorListener(new CursorListener() {
					@Override
					public void cursorMovedResized(CursorChangeEvent event) {
						scrollCursorToVisible();
					}
				});
	}
	
	
	@Override
	protected JComponent doCreateSwingComponent() {
		SwingAlignmentOverviewArea result = new SwingAlignmentOverviewArea(this);
		addCursorScrollListener();
		return result;
	}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		SWTAlignmentOverviewArea result = new SWTAlignmentOverviewArea(parent, style, this);
		addCursorScrollListener();
		return result;
	}


	@Override
	public ToolkitSpecificAlignmentOverviewArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentOverviewArea)super.getToolkitComponent();
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
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T, U> void afterProviderChanged(SequenceDataProvider<T> previous,	SequenceDataProvider<U> current) {
		//TODO repaint
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
	}
}
