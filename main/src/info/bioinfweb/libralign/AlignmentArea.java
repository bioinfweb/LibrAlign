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


import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.graphics.DoubleDimension;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.commons.tic.toolkit.AbstractSWTWidget;
import info.bioinfweb.commons.tic.toolkit.AbstractSwingComponent;
import info.bioinfweb.libralign.alignmentprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.alignmentprovider.SequenceDataProvider;
import info.bioinfweb.libralign.alignmentprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.alignmentprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.alignmentprovider.events.TokenChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaModel;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;
import info.bioinfweb.libralign.selection.AlignmentCursor;
import info.bioinfweb.libralign.selection.SelectionModel;
import info.webinsel.util.Math2;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class AlignmentArea extends TICComponent implements SequenceDataChangeListener {
	public static final float COMPOUND_WIDTH = 10f;
	public static final float COMPOUND_HEIGHT = 14f;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.PLAIN;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;

	
	private SequenceDataProvider sequenceProvider = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private WorkingMode workingMode = WorkingMode.VIEW;  //TODO Should this better be part of the controller (key and mouse listener)?
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Initial value should be adjusted when the data type of the specified provider is known.
	private AlignmentCursor cursor = new AlignmentCursor();
	private SelectionModel selection = new SelectionModel(this);
	private DataAreaModel dataAreas = new DataAreaModel();
	private float zoomX = 1f;
	private float zoomY = 1f;
	private float compoundWidth = COMPOUND_WIDTH;
	private float compoundHeight = COMPOUND_HEIGHT;	
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	
	
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


	public boolean hasDataProvider() {
		return getSequenceProvider() != null;
	}
	
	
	public SequenceDataProvider getSequenceProvider() {
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
	public SequenceDataProvider setDataProvider(SequenceDataProvider sequenceProvider, boolean moveListeners) {
		SequenceDataProvider result = this.sequenceProvider;
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


	public AlignmentCursor getCursor() {
		return cursor;
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
		compoundWidth = COMPOUND_WIDTH * zoomX;
		compoundHeight = COMPOUND_HEIGHT * zoomY;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}
	
	
	private void calculateFont() {
		int fontSize = Math.round(Math.min(compoundHeight * (COMPOUND_WIDTH / COMPOUND_HEIGHT), compoundWidth) * 
				FONT_SIZE_FACTOR);  //TODO Equation correct?
		if (fontSize >= MIN_FONT_SIZE) {
			font = new Font(FONT_NAME, FONT_STYLE, fontSize);
		}
		else {
			font = null;
		}
	}

	
	/**
	 * Returns the current width of a compound depending on the current zoom factor.
	 * 
	 * @return a float value greater than zero
	 */
	public float getCompoundWidth() {
		return compoundWidth;
	}


	public void setCompoundWidth(float compoundWidth) {
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
	public float getCompoundHeight() {
		return compoundHeight;
	}
	
	
	public void setCompoundHeight(float compoundHeight) {
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
		return getCompoundFont() != null;
	}
	
	
	public Font getCompoundFont() {
		return font;
	}
	
	
	/**
	 * Returns the column containing the specified x coordinate.
	 *  
	 * @param x the paint coordinate
	 * @return the alignment column or <code>-1</code> id the coordinate is outside the alignment area.
	 */
	public int columnByPaintX(int x) {
		int result = (int)(x / getCompoundWidth());
		if (Math2.isBetween(result, 0, getSequenceProvider().getMaxSequenceLength() - 1)) {
			return result;
		}
		else {
			return -1;
		}
	}


	public int paintXByColumn(int column) {
		return (int)((column - 1) * getCompoundWidth());
	}


	@Override
	public JComponent createSwingComponent() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		//TODO Add current subcomponents
		return result;
	}



	@Override
	public Composite createSWTWidget(Composite parent, int style) {
		Composite result = new Composite(parent, style);  //TODO Should style really be passed through?
		result.setLayout(new FillLayout(SWT.VERTICAL));
		//TODO Add current subcomponents
		return result;
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Empty because toolkit specific components are provided


	/**
	 * Returns the size of the underlying toolkit specific component. If no component has been created yet,
	 * (0, 0) is returned.
	 * 
	 * @see info.bioinfweb.commons.tic.TICComponent#getSize()
	 */
	@Override
	public Dimension2D getSize() {
		switch (getCurrentToolkit()) {
			case SWING:
				return ((JComponent)getToolkitComponent()).getPreferredSize();  //TODO correct size?
			case SWT:
				Point point = ((Composite)getToolkitComponent()).getSize();
				return new DoubleDimension(point.x, point.y);
			default:
			  return new DoubleDimension(0, 0);
		}
	}
	
	
	@Override
	public void afterSequenceChange(SequenceChangeEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterSequenceRenamed(SequenceRenamedEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterTokenChange(TokenChangeEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterProviderChanged(SequenceDataProvider previous, SequenceDataProvider current) {
		//TODO repaint
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
	}
}
