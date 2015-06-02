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


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentDataViewMode;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceColorSchema;
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
	public static final int COMPOUND_WIDTH = 10;
	public static final int COMPOUND_HEIGHT = 14;
	
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.PLAIN;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;

	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static final int DIVIDER_WIDTH = 2;
	
	public static final int MIN_PART_AREA_HEIGHT = 5;
	
	
	private AlignmentModel<?> alignmentModel = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private float zoomX = 1f;
	private float zoomY = 1f;
	private int compoundWidth = COMPOUND_WIDTH;
	private int compoundHeight = COMPOUND_HEIGHT;	
	private Font compoundFont = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	private DataAreaModel dataAreas = new DataAreaModel(this);
	private SequenceColorSchema colorSchema = new SequenceColorSchema();  //TODO Remove
	private TokenPainterList tokenPainterList = new TokenPainterList(this);
	private TokenPainter defaultTokenPainter = new SingleColorTokenPainter();  // Currently all elements would be painted in the default color.
	private EditSettings editSettings;
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Remove
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


	public boolean hasSequenceProvider() {
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


	public Font getCompoundFont() {
		return compoundFont;
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


	public SequenceColorSchema getColorSchema() {  //TODO Remove
		return colorSchema;
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


	/**
	 * Returns the default token painter that is used to paint tokens from the alignment model if no according
	 * painter in defined in {@link #getTokenPainterList()} or the painter specified there is not able to paint
	 * the according token.
	 * 
	 * @return the default token painter instance used by this alignment area
	 */
	public TokenPainter getDefaultTokenPainter() {
		return defaultTokenPainter;
	}


	public EditSettings getEditSettings() {
		return editSettings;
	}


	public AlignmentDataViewMode getViewMode() {  //TODO remove
		return viewMode;
	}
	
	
	public void setViewMode(AlignmentDataViewMode viewMode) {  //TODO remove
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
