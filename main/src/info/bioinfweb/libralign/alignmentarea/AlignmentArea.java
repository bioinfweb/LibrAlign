/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.order.SequenceOrder;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainterList;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaModel;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;
import info.bioinfweb.libralign.editsettings.EditSettings;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentArea extends TICComponent implements AlignmentModelChangeListener, DataAreaModelListener {
	public static final double DEFAULT_CURSOR_LINE_WIDTH = 2;
	public static final int MIN_PART_AREA_HEIGHT = 5;
	
	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static final int DIVIDER_WIDTH = 2;
	

	private AlignmentModel<?> alignmentModel = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private float zoomX = 1f;
	private float zoomY = 1f;
	private DataAreaModel dataAreas = new DataAreaModel(this);
	private TokenPainterList tokenPainterList = new TokenPainterList(this);
	private Color cursorColor = Color.BLACK;
	private double cursorLineWidth = DEFAULT_CURSOR_LINE_WIDTH;
	private Color selectionColor = SystemColor.textHighlight;    //TODO Move cursor color, width and selection color to separate object?
	private EditSettings editSettings;
	private SelectionModel selection = new SelectionModel(this);

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
		
		if (hasContainer()) {
			editSettings = getContainer().getEditSettings();
		}
		else {
			editSettings = new EditSettings();
		}
		
		alignmentContentArea = new AlignmentContentArea(this);
		alignmentLabelArea = new AlignmentLabelArea(this);  // Must be called after alignmentContentArea has been created.

		dataAreas.addListener(this);
	}


	public boolean hasAlignmentModel() {
		return getAlignmentModel() != null;
	}
	
	
	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}
	
	
	/**
	 * Changes the sequence model used by this instance.
	 * 
	 * @param alignmentModel - the new data provider to use from now on
	 * @param moveListeners - Specify {@code true} here, if you want the {@link AlignmentModelChangeListener}s
	 *        attached to the current model to be moved to the specified {@code alignmentModel},
	 *        {@code false} if the listeners shall remain attached to the old model. (This instance
	 *        is also registered as a listener and is always moved to the new object, no matter which value is
	 *        specified here.)
	 * @return the previous model that has been replaced or {@code null} if there was no model before
	 */
	public AlignmentModel<?> setAlignmentModel(AlignmentModel<?> alignmentModel, 
			boolean moveListeners) {
		
		AlignmentModel<?> result = this.alignmentModel;
		if (!alignmentModel.equals(this.alignmentModel)) {
			if (this.alignmentModel != null) {
				if (moveListeners) {  // Move all listeners
					alignmentModel.getChangeListeners().addAll(this.alignmentModel.getChangeListeners());
					this.alignmentModel.getChangeListeners().clear();
				}
				else {  // Move this instance as the listener anyway:
					this.alignmentModel.getChangeListeners().remove(this);
					alignmentModel.getChangeListeners().add(this);
				}
			}
			
			this.alignmentModel = alignmentModel;
			getSequenceOrder().setSourceSequenceOrder();  // Update sequence names
			if (hasToolkitComponent()) {
				reinsertSubelements();
			}
			
      // Fire events for listener move after the process finished
			if (this.alignmentModel != null) {
				if (!this.alignmentModel.getChangeListeners().contains(this)) {  // Add this object as a listener if it was not already moved from the previous provider.
					this.alignmentModel.getChangeListeners().add(this);
				}
				
				if (moveListeners) {
					Iterator<AlignmentModelChangeListener> iterator = this.alignmentModel.getChangeListeners().iterator();
					while (iterator.hasNext()) {
						iterator.next().afterProviderChanged(result, this.alignmentModel);
					}
				}
				else {
					afterProviderChanged(result, this.alignmentModel);
				}
			}
		}
		getTokenPainterList().afterAlignmentModelChanged();
		
		return result;
	}
	
	
	public SequenceOrder getSequenceOrder() {
		return sequenceOrder;
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
		
		//assignPaintSize();
		//fireZoomChanged();
	}
	
	
	/**
	 * Returns the width of the column with the specified index.
	 * 
	 * @param columnIndex the index of the column to determine the width from
	 * @return the width of column in pixels
	 * @throws IllegalStateException if this instance does not have an alignment model
	 * @throws IndexOutOfBoundsException if the specified column does not exist in the alignment model
	 */
	public double getTokenWidth(int columnIndex) {
		if (!hasAlignmentModel()) {
			throw new IllegalStateException("There is no associated alignment model defined that specifies any columns.");
		}
		else if ((columnIndex < 0) || (columnIndex >= getAlignmentModel().getMaxSequenceLength())) {
			throw new IndexOutOfBoundsException("A column with the index " + columnIndex + " does not exist in the current model.");
		}
		else {
			return getTokenPainterList().painterByColumn(columnIndex).getPreferredWidth() * getZoomX();
		}
	}
	
	
	public double maxTokenWidth() {
		if (getTokenPainterList().isEmpty()) {
			return getTokenPainterList().getDefaultTokenPainter().getPreferredWidth() * getZoomX();
		}
		else {
			double result = 0;
			for (TokenPainter painter : getTokenPainterList()) {
				double width;
				if (painter == null) {
					width = getTokenPainterList().getDefaultTokenPainter().getPreferredWidth();
				}
				else {
					width = painter.getPreferredWidth();
				}
				result = Math.max(result, width);
			}
			return result * getZoomX();
		}
	}
	
	
	public double minTokenWidth() {
		if (getTokenPainterList().isEmpty()) {
			return getTokenPainterList().getDefaultTokenPainter().getPreferredWidth() * getZoomX();
		}
		else {
			double result = 0;
			for (TokenPainter painter : getTokenPainterList()) {
				double width;
				if (painter == null) {
					width = getTokenPainterList().getDefaultTokenPainter().getPreferredWidth();
				}
				else {
					width = painter.getPreferredWidth();
				}
				result = Math.min(result, width);
			}
			return result * getZoomX();
		}
	}
	
	
	/**
	 * Returns the height of tokens displayed in this alignment.
	 * 
	 * @return the height in pixels
	 */
	public double getTokenHeight() {
		if (!hasAlignmentModel()) {
			throw new IllegalStateException("There is no associated alignment model defined that specifies any columns.");
		}
		else {
			int index;
			if (getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				throw new InternalError("not implemented");
				// index = ?;   //TODO Which painter should define the height?
			}
			else {
				index = 0;
			}
			return getTokenPainterList().painterByColumn(index).getPreferredHeight() * getZoomY();
		}
	}
	
	
	public Font getTokenHeightFont() {
		return new Font(Font.SANS_SERIF, Font.PLAIN, 
				(int)Math.round(SingleColorTokenPainter.FONT_SIZE_FACTOR * getTokenHeight()));
	}
	
	
	/**
	 * Returns the data area model used by this instance containing all data areas attached 
	 * to the displayed alignment.
	 * 
	 * @return the model object managing all attached data areas 
	 */
	public DataAreaModel getDataAreas() {
		return dataAreas;
	}


	/**
	 * Returns the list of token painters to be used for the output of the data from the alignment model.
	 * If an concatenated alignment model is used, this list holds one painter for each part (column range),
	 * otherwise it will only contain one.
	 * <p>
	 * The size and the order of the list are updated automatically depending on alignment model changes (events). 
	 * 
	 * @return the token painter list
	 */
	public TokenPainterList getTokenPainterList() {
		return tokenPainterList;
	}


	public Color getCursorColor() {
		return cursorColor;
	}


	public void setCursorColor(Color cursorColor) {
		this.cursorColor = cursorColor;
	}


	public Color getSelectionColor() {
		return selectionColor;
	}


	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}


	public double getCursorLineWidth() {
		return cursorLineWidth;
	}


	public void setCursorLineWidth(double cursorLineWidth) {
		this.cursorLineWidth = cursorLineWidth;
	}


	public EditSettings getEditSettings() {
		return editSettings;
	}


	/**
	 * Returns the selection model used by this object.
	 */
	public SelectionModel getSelection() {
		return selection;
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


	/**
	 * If this area is part of an alignment area that is contained in a {@link MultipleAlignmentsContainer}
	 * than this methods calculates the maximum length of all sequences in all alignment areas contained in
	 * this container. Otherwise the return value is identical with {@code getSequenceProvider.getMaxSequenceLength()}.
	 * 
	 * @return a value >= 0
	 * @see AlignmentModel#getMaxSequenceLength()
	 */
	public int getGlobalMaxSequenceLength() {
		if (hasContainer()) {
			int result = 0;
			for (AlignmentArea alignmentArea : getContainer().getAlignmentAreas()) {
				AlignmentModel<?> provider = alignmentArea.getAlignmentModel();
				if (provider != null) {
					result = Math.max(result, provider.getMaxSequenceLength());
				}
			}
			return result;
		}
		else {
			return getAlignmentModel().getMaxSequenceLength();
		}
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
	
	
	/**
	 * Calls the {@code assignSize()} method of the contained {@link AlignmentContentArea}, {@link AlignmentLabelArea},
	 * their subcomponents and on this instance.
	 */
	public void assignSizeToAll() {
		if (hasToolkitComponent() && getContentArea().hasToolkitComponent() && getLabelArea().hasToolkitComponent()) {
			Iterator<AlignmentSubArea> iterator = getContentArea().getToolkitComponent().subAreaIterator();
			while (iterator.hasNext()) {
				AlignmentSubArea area = iterator.next();
				area.assignSize();
				area.getLabelSubArea().assignSize();
			}
			getContentArea().assignSize();
			getLabelArea().assignSize();
			assignSize();
		}
	}
	
	
	
	/**
	 * Reinserts subelements in the contained label and content areas if they already have created a toolkit specific component.
	 */
	public void reinsertSubelements() {
		if (getContentArea().hasToolkitComponent()) {
			getContentArea().getToolkitComponent().reinsertSubelements();
		}
		if (getLabelArea().hasToolkitComponent()) {
			getLabelArea().getToolkitComponent().reinsertSubelements();
		}
	}
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		if (e.getSource().equals(getAlignmentModel())) {
			getLabelArea().setLocalMaxWidthRecalculate();  // Needs to be called before assignSizeToAll().
			getSequenceOrder().refreshFromSource();
			if (hasToolkitComponent()) {
				reinsertSubelements();
			}
		}
		assignSizeToAll();
		//repaint();  //TODO Does this have to be called?
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterSequenceChange(e);
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		if (e.getSource().equals(getAlignmentModel())) {
			getLabelArea().setLocalMaxWidthRecalculate();  // Needs to be called before assignSizeToAll().
		}
		assignSizeToAll();  // Other label areas might also have to adopt their width.
		getDataAreas().getSequenceDataChangeListener().afterSequenceRenamed(e);
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		assignSize();  // Needs to happen first (otherwise the child elements get cut off in SWT probably because they are only painted as far as they are visible in the parent component even if the parent will be resized later on).
		if (getContentArea().hasToolkitComponent() && e.getSource().equals(getAlignmentModel())) {
			getContentArea().getToolkitComponent().assignSequenceAreaSize(e.getSequenceID());
		}
		getDataAreas().getSequenceDataChangeListener().afterTokenChange(e);
	}


	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {
		getLabelArea().setLocalMaxWidthRecalculate();  // Needs to be called before assignSizeToAll().
		assignSizeToAll();  //TODO reinsertSubements()?
		repaint();  //TODO Needed?
		//TODO Remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterProviderChanged(previous, current);
	}


	@Override
	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
		if (hasToolkitComponent()) {
			if (e.getSource().equals(getDataAreas())) {
				getDataAreas().setLocalMaxLengthBeforeStartRecalculate();
				reinsertSubelements();
			}
			assignSizeToAll();
		}
	}


	@Override
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
		//TODO implement (Possibly delegate to dataAreaInsertedRemoved()?)
	}
}
