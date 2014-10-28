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
package info.bioinfweb.libralign;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentareacomponents.SWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentareacomponents.SequenceArea;
import info.bioinfweb.libralign.alignmentareacomponents.SwingAlignmentContentArea;
import info.bioinfweb.libralign.alignmentareacomponents.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.ToolkitSpecificAlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.DataAreaLocation;
import info.bioinfweb.libralign.dataarea.DataAreaModel;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;
import info.bioinfweb.libralign.editsettings.EditSettings;
import info.bioinfweb.libralign.editsettings.WorkingMode;
import info.bioinfweb.libralign.selection.SelectionChangeEvent;
import info.bioinfweb.libralign.selection.SelectionListener;
import info.bioinfweb.libralign.selection.SelectionModel;
import info.bioinfweb.libralign.sequenceorder.SequenceOrder;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;



/**
 * Toolkit independent component displaying the contents (not the labels) of a single alignment and its attaches 
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
public class AlignmentContentArea extends TICComponent implements SequenceDataChangeListener, DataAreaModelListener {
	public static final int COMPOUND_WIDTH = 10;
	public static final int COMPOUND_HEIGHT = 14;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.PLAIN;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;


	private AlignmentArea owner;
	private SequenceDataProvider<?> sequenceProvider = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private EditSettings editSettings;
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Initial value should be adjusted when the data type of the specified provider is known.
	private SelectionModel selection = new SelectionModel(this);
	private DataAreaModel dataAreas = new DataAreaModel(this);
	private float zoomX = 1f;
	private float zoomY = 1f;
	private int compoundWidth = COMPOUND_WIDTH;
	private int compoundHeight = COMPOUND_HEIGHT;	
	private Font compoundFont = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	
	
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
		if (owner.hasContainer()) {
			editSettings = owner.getContainer().getEditSettings();
		}
		else {
			editSettings = new EditSettings();
		}

		dataAreas.addListener(this);
  }


	public AlignmentArea getOwner() {
		return owner;
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
				if (!this.sequenceProvider.getChangeListeners().contains(this)) {  // Add this object as a listener if it was not already moved from the previous provider.
					this.sequenceProvider.getChangeListeners().add(this);
				}
				
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


	public EditSettings getEditSettings() {
		return editSettings;
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
	 * Returns the rectangle in the paint coordinate system of scrolled area displaying the sequences, that contains
	 * all cells currently occupied by the alignment cursor. 
	 * <p>
	 * If the last row has associated data areas, the height of these areas is also included in the rectangle. 
	 * 
	 * @return a rectangle with paint coordinates
	 */
	public Rectangle getCursorRectangle() {
		int y = paintYByRow(getSelection().getCursorRow());
		Rectangle result = new Rectangle(paintXByColumn(getSelection().getCursorColumn()), y,
				getCompoundWidth(), paintYByRow(getSelection().getCursorRow() + getSelection().getCursorHeight()) - y); 
		if (getSelection().getCursorRow() + getSelection().getCursorHeight() - 1 == getSequenceProvider().getSequenceCount() - 1) {
			result.height += getCompoundHeight();  // Add height of the last row, because the return value of paintYByRow(maxIndex + 1) is equal to paintYByRow(maxIndex).
		}
		return result; 
	}
	
	
	@Override
	public Dimension getSize() {
		Dimension result = new Dimension();
		result.width = getDataAreas().getMaxLengthBeforeStart() + getSequenceProvider().getMaxSequenceLength() * getCompoundWidth();
		result.height = getSequenceProvider().getSequenceCount() * getCompoundHeight() + getDataAreas().getVisibleAreaHeight();
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
		getSelection().addSelectionListener(new SelectionListener() {
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
		AlignmentSubArea subArea = getToolkitComponent().getAreaByY(y);
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
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		getSequenceOrder().refreshFromSource();
		if (hasToolkitComponent()) {
			getToolkitComponent().reinsertSubelements();
		}
		assignSize();
		//repaint();  //TODO Does this have to be called?
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterSequenceChange(e);
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		System.out.println("afterSequenceRenamed");
		// TODO Repaint label area and possibly change its width.
		getDataAreas().getSequenceDataChangeListener().afterSequenceRenamed(e);
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		getToolkitComponent().assignSequenceAreaSize(e.getSequenceID());  // Needs to happen before assignSize().	
		assignSize();
		repaint();  // In some cases multiple paint events may be fired, because the selection or the component size were also changed.
		getDataAreas().getSequenceDataChangeListener().afterTokenChange(e);
	}


	@Override
	public <T, U> void afterProviderChanged(SequenceDataProvider<T> previous, SequenceDataProvider<U> current) {
		assignSize();
		repaint();
		//TODO Remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterProviderChanged(previous, current);
	}


	@Override
	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
		if (hasToolkitComponent()) {
			getToolkitComponent().reinsertSubelements();
		}
	}


	@Override
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
		//TODO implement
	}
}
