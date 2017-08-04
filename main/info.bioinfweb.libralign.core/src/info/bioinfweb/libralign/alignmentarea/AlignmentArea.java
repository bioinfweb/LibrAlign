/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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


import info.bioinfweb.libralign.actions.AlignmentActionProvider;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.ToolkitSpecificAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.order.SequenceOrder;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsListener;
import info.bioinfweb.libralign.alignmentarea.paintsettings.TokenPainterReplacedEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.ZoomChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreasModel;
import info.bioinfweb.libralign.dataarea.DataAreasModelListener;
import info.bioinfweb.libralign.editsettings.EditSettings;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;



/**
 * Instances of this class act as GUI components that display a multiple sequence alignment including attached data areas.
 * <p>
 * {@code AlignmentArea} is one of the major GUI components in <i>LibrAlign</i>. It acts as a view in the model view 
 * controller paradigm and works closely together with an instance of {@link AlignmentModel} which provides the data to 
 * be displayed.
 * <p>
 * It can be used as stand-alone components are can be contained in a {@link MultipleAlignmentsContainer}, which would be 
 * returned by {@link #getContainer()} in this case. Instances of this class are {@link TICComponent}s. To use this class 
 * in a GUI application, a toolkit specific version of it can be created using {@code SwingComponentFactory} or 
 * {@code SWTComponentFactory} from <a href="http://bioinfweb.info/TIC"><i>TIC</i></a>. See the
 * <a href="http://bioinfweb.info/LibrAlign/Documentation/wiki/Working_with_toolkits"><i>LibrAlign</i> documentation</a> 
 * for details.
 * <p>
 * {@code AlignmentArea} has the following key properties:
 * <ul>
 *   <li>{@link #getSequenceOrder()} determines the order in which sequences are displayed.</li>
 *   <li>{@link #getPaintSettings()} determines the way the contents are displayed.</li>
 *   <li>{@link #getEditSettings()} determines if and how a user can edit the contents of this area.</li>
 *   <li>{@link #getActionProvider()} provides the business logic for manipulating the contents of the associated 
 *       model in response to user inputs depending on the current selection.</li>
 *   <li>{@link #getSelection()} determines the cells of the alignment that are currently selected and the alignment 
 *       cursor position.</li>
 * </ul>
 * <p>
 * The implementation of {@code AlignmentArea} is distributed across {@link #getContentArea()} which displays the
 * sequences and data areas and {@link #getLabelArea()} which displays there labels (on the left).
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 * 
 * @see MultipleAlignmentsContainer
 * @see AlignmentContentArea
 * @see AlignmentLabelArea
 * @see AlignmentModel
 */
public class AlignmentArea extends TICComponent implements AlignmentModelChangeListener, DataAreasModelListener {
	public static final int MIN_PART_AREA_HEIGHT = 5;
	
	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static final int DIVIDER_WIDTH = 2;
	

	private AlignmentModel<?> alignmentModel = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private DataAreasModel dataAreas = new DataAreasModel(this);
	private PaintSettings paintSettings;
	private EditSettings editSettings;
	private SelectionModel selection;
	private AlignmentActionProvider<Object> actionProvider = new AlignmentActionProvider<Object>(this);

	private MultipleAlignmentsContainer container = null;
	private AlignmentContentArea alignmentContentArea;
	private AlignmentLabelArea alignmentLabelArea;
	private boolean allowVerticalScrolling = true;
	private Rectangle lastCursorRectangle = null;
	
	private PaintSettingsListener PAINT_SETTINGS_LISTERNER = new PaintSettingsListener() {
		private void updateSize() {
			getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
			assignSizeToAll();
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("cursorColor") || event.getPropertyName().equals("cursorLineWidth") ||
					event.getPropertyName().equals("selectionColor")) {
				
				if (event.getPropertyName().equals("cursorLineWidth")) {
					getDataAreas().setLocalMaxLengthBeforeAfterRecalculate();
				}
				repaint();
			}
		}
		
		@Override
		public void zoomChange(ZoomChangeEvent event) {
			updateSize();
			repaint();  // Necessary in SWT if the size did not change.
		}

		@Override
		public void tokenPainterReplaced(TokenPainterReplacedEvent event) {
			updateSize();
			repaint();  // Necessary in SWT if the size did not change.
		}
		
		@Override
		public void tokenPainterListChange(PaintSettingsEvent event) {
			updateSize();
			repaint();  // Necessary in SWT if the size did not change.
		}
	};
	
	
	/**
	 * Creates a new instance of this class that shall not part of a {@link MultipleAlignmentsContainer} that uses no
	 * subcomponents for each sequence and data area.
	 * <p>
	 * If you want to create an alignment area that shall be inserted into a {@link MultipleAlignmentsContainer}
	 * use {@link #AlignmentArea(MultipleAlignmentsContainer, boolean))} instead.
	 */
	public AlignmentArea() {
		this(null, false);
	}
	
	
	/**
	 * Creates a new instance of this class that shall not part of a {@link MultipleAlignmentsContainer}.
	 * <p>
	 * If you want to create an alignment area that shall be inserted into a {@link MultipleAlignmentsContainer}
	 * use {@link #AlignmentArea(MultipleAlignmentsContainer)} instead.
	 * 
	 * @param useSubcomponents Specify {@code true} here, if a separate subcomponent should be used for each 
	 *        sequence and data area or {@code false} to draw all on separate components. (Using separate
	 *        components allows to use custom toolkit specific components provided by custom data areas but
	 *        does not allow to display long sequences, especially with <i>SWT</i> under <i>Windows</i> and
	 *        <i>Linux</i>.)
	 */
	public AlignmentArea(boolean useSubcomponents) {
		this(null, useSubcomponents);
	}
	
	
	/**
	 * Creates a new instance of this class to be inserted into a {@link MultipleAlignmentsContainer} that uses no
	 * subcomponents for each sequence and data area.
	 * 
	 * @param container the container where the returned instance will be contained in
	 */
	public AlignmentArea(MultipleAlignmentsContainer container) {
		this(container, false);
	}
	
	
	/**
	 * Creates a new instance of this class to be inserted into a {@link MultipleAlignmentsContainer}.
	 * 
	 * @param container the container where the returned instance will be contained in
	 * @param useSubcomponents Specify {@code true} here, if a separate subcomponent should be used for each 
	 *        sequence and data area or {@code false} to draw all on separate components. (Using separate
	 *        components allows to use custom toolkit specific components provided by custom data areas but
	 *        does not allow to display long sequences, especially with <i>SWT</i> under <i>Windows</i> and
	 *        <i>Linux</i>.)
	 */
	public AlignmentArea(MultipleAlignmentsContainer container, boolean useSubcomponents) {
		super();
		this.container = container;
		
		if (hasContainer()) {
			editSettings = getContainer().getEditSettings();
		}
		else {
			editSettings = new EditSettings();
		}
		
		selection = new SelectionModel(this);
		selection.addSelectionListener(new SelectionListener() {
					@Override
					public void selectionChanged(SelectionChangeEvent event) {
						scrollCursorToVisible();
					}
				});
		
		alignmentContentArea = new AlignmentContentArea(this, useSubcomponents);  // The selection object must already have been created here.  
		alignmentLabelArea = new AlignmentLabelArea(this);  // Must be called after alignmentContentArea has been created.

		dataAreas.addListener(this);
		
		paintSettings = new PaintSettings(this);
		paintSettings.addListener(PAINT_SETTINGS_LISTERNER);
		
	}


	/**
	 * Allows to determine whether this instance is currently associated with any alignment model.
	 * 
	 * @return {@code true} if an associated alignment model is present, {@code false} if no alignment model is specified.
	 */
	public boolean hasAlignmentModel() {
		return getAlignmentModel() != null;
	}
	
	
	/**
	 * Returns the alignment model providing the data displayed by this area.
	 * 
	 * @return the associated alignment model
	 */
	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}
	
	
	/**
	 * Returns the paint settings object associated with this instance. The returned object contains properties that
	 * control the way the contents of this area are displayed.
	 * 
	 * @return the paint settings object used by this component
	 */
	public PaintSettings getPaintSettings() {
		return paintSettings;
	}


	/**
	 * Changes the alignment model providing the data for this instance.
	 * 
	 * @param alignmentModel the new alignment model to use from now on
	 * @param moveListeners Specify {@code true} here, if you want the {@link AlignmentModelChangeListener}s
	 *        attached to the current model to be moved to the specified {@code alignmentModel},
	 *        {@code false} if the listeners shall remain attached to the old model. (This instance
	 *        is also registered as a listener and is always moved to the new object, no matter which value is
	 *        specified here.)
	 * @return the previous model that has been replaced or {@code null} if there was no model before
	 */
	public AlignmentModel<?> setAlignmentModel(AlignmentModel<?> alignmentModel, boolean moveListeners) {
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
			updateSubelements();
			
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
		getPaintSettings().getTokenPainterList().afterAlignmentModelChanged();
		
		return result;
	}
	
	
	/**
	 * Returns the sequence order object the determines the order in which the sequences of the associated
	 * alignment model are displayed in this instance.
	 * 
	 * @return the associated sequence order object
	 */
	public SequenceOrder getSequenceOrder() {
		return sequenceOrder;
	}


	/**
	 * Returns the data area model used by this instance containing all data areas attached 
	 * to the displayed alignment.
	 * 
	 * @return the model object managing all attached data areas 
	 */
	public DataAreasModel getDataAreas() {
		return dataAreas;
	}


	/**
	 * Returns the edit setting object used by this instance. Edit settings are properties that influence
	 * the way a user can edit the contents of an alignment area and the data areas it contains.
	 * 
	 * @return the associated edit settings object
	 */
	public EditSettings getEditSettings() {
		return editSettings;
	}


	/**
	 * Returns the selection model used by this instance.
	 * 
	 * @return the associated selection model
	 */
	public SelectionModel getSelection() {
		return selection;
	}

	
	/**
	 * Tool object that implements business logic methods to manipulate the alignment model. All operations will
	 * be performed on the current model of this alignment area.
	 * 
	 * @return the instance associated with this alignment area
	 */
	public AlignmentActionProvider<Object> getActionProvider() {
		return actionProvider;
	}


	/**
	 * Returns the container this object is contained in.
	 * 
	 * @return the container instance or {@code null} if this instance is used as a stand-alone component.
	 */
	public MultipleAlignmentsContainer getContainer() {
		return container;
	}
	
	
	/**
	 * Determines whether this instance is contained inside a {@link MultipleAlignmentsContainer}.
	 * 
	 * @return {@code true} if this area is part of a parent container or {@code false} if it is used as a 
	 *         stand-alone component 
	 */
	public boolean hasContainer() {
		return getContainer() != null;
	}


	/**
	 * Returns the alignment content area used internally by this instance. Content areas display the sequences and 
	 * data area, while data areas display their labels.
	 * 
	 * @return the alignment content area contained in this instance
	 */
	public AlignmentContentArea getContentArea() {
		return alignmentContentArea;
	}


	/**
	 * Returns the alignment label area used internally by this instance. Content areas display the sequences and 
	 * data area, while data areas display their labels.
	 * 
	 * @return the alignment label area contained in this instance
	 */
	public AlignmentLabelArea getLabelArea() {
		return alignmentLabelArea;
	}


	/**
	 * Determines whether this area should be displayed with its whole height insight a {@link MultipleAlignmentsContainer}
	 * if there is enough space available.
	 * 
	 * @return {@code true} if this area does not need to be displayed with its whole width, {@code false} if scrolling should
	 *         be avoided if possible
	 */
	public boolean isAllowVerticalScrolling() {
		return allowVerticalScrolling;
	}


	/**
	 * Allows to specify whether this area should be displayed with its whole height insight a {@link MultipleAlignmentsContainer}
	 * if there is enough space available.
	 * 
	 * @param allowVerticalScrolling - Specify {@code true} here, if this area does not need to be displayed with its 
	 *         whole width or {@code false} if scrolling should be avoided if possible.
	 */
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
	
	
	/**
	 * Calculates the needed with to label the associated alignment. Note that the actual width of this
	 * component is calculated using {@link #getGlobalMaximumNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public double getLocalMaximumNeededAlignmentWidth() {
		if (hasAlignmentModel()) {
			if (getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				throw new InternalError("not implemented");
			}
			else {
				int length = getAlignmentModel().getMaxSequenceLength(); 
				if (length > 0) {
					return length * getPaintSettings().getTokenWidth(0);
				}
			}
		}
		return 0;
	}
	
	
	public double getLocalMaxNeededWidth() {
		return getDataAreas().getGlobalMaxLengthBeforeStart() + getLocalMaximumNeededAlignmentWidth() + 
				getDataAreas().getLocalMaxLengthAfterEnd();
	}
	
	
	/**
	 * Returns the maximum needed width to display all alignment columns with the according token painters and the
	 * current zoom factor and the space before and after the alignment possibly occupied by data areas calculated 
	 * over all alignments contained in the parent {@link MultipleAlignmentsContainer}.
	 * 
	 * @return a value >= 0
	 */
	public double getGlobalMaxNeededWidth() {
		double result = 0;
		if (hasContainer()) {
			for (AlignmentArea alignmentArea : getContainer().getAlignmentAreas()) {
				result = Math.max(result, alignmentArea.getLocalMaxNeededWidth());
			}
		}
		else {
			result = getLocalMaxNeededWidth();
		}
		return result;
	}
	
	
	public void scrollCursorToVisible() {
		//TODO Refactor to also support direct painting without subcomponents.
		if (hasToolkitComponent()) {
			Rectangle visibleRectangle = getToolkitComponent().getVisibleAlignmentRect();
			Rectangle currentRectangle = getContentArea().getCursorRectangle().getBounds();  // Rounding takes place here.
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
	}
	
	
	@Override
	protected String getSwingComponentClassName() {
		return "info.bioinfweb.libralign.alignmentarea.ScrollContainerSwingAlignmentArea";
	}


	@Override
	protected String getSWTComponentClassName() {
		return "info.bioinfweb.libralign.alignmentarea.ScrollContainerSWTAlignmentArea";
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
		// Just returns the size already set to the toolkit component because the size of an alignment area is determined just by the layout manager and not dependent of the alignment and data area size.
		if (hasToolkitComponent()) {
			return getToolkitComponent().getToolkitSize();
		}
		else {
			return new Dimension(0, 0);
		}
	}
	
	
	/**
	 * Calls the {@code assignSize()} method of the contained {@link AlignmentContentArea}, {@link AlignmentLabelArea},
	 * their subcomponents and on this instance.
	 */
	public void assignSizeToAll() {
		if (hasToolkitComponent() && getContentArea().hasToolkitComponent() && getLabelArea().hasToolkitComponent()) {
			Iterator<AlignmentLabelSubArea> iterator = getLabelArea().getToolkitComponent().subAreaIterator();
			while (iterator.hasNext()) {
				iterator.next().assignSize();;
			}
			
			if (getContentArea().isUseSubcomponents()) {
				Iterator<TICComponent> contentIterator = ((ToolkitSpecificAlignmentContentArea)getContentArea().getToolkitComponent()).subAreaIterator();
				while (contentIterator.hasNext()) {
					contentIterator.next().assignSize();;
				}
			}
			
			getContentArea().assignSize();
			getLabelArea().assignSize();
			assignSize();
		}
	}
	
	
	
	/**
	 * Reinserts subelements in the contained label and content areas if they already have created a toolkit specific component.
	 */
	public void updateSubelements() {
		getContentArea().updateSubelements();
		if (getLabelArea().hasToolkitComponent()) {
			getLabelArea().getToolkitComponent().reinsertSubelements();
		}
	}
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		if (e.getSource().equals(getAlignmentModel())) {
			getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
			getSequenceOrder().refreshFromSource();
			updateSubelements();
		}
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterSequenceChange(e);
		assignSizeToAll();
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		if (e.getSource().equals(getAlignmentModel())) {
			getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
		}
		getDataAreas().getSequenceDataChangeListener().afterSequenceRenamed(e);
		assignSizeToAll();  // Other label areas might also have to adopt their width.
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		getDataAreas().getSequenceDataChangeListener().afterTokenChange(e);
		assignSizeToAll();
		if (getContentArea().hasToolkitComponent()) {
			if (getContentArea().isUseSubcomponents()) {
				((ToolkitSpecificAlignmentContentArea)getContentArea().getToolkitComponent()).repaintSequenceAreas();  // Necessary when neither the selection changes nor the size of the sequence areas changed (e.g. when deleting right in a sequence with an attached pherogram with space before and after the alignment).
				//TODO Wouldn't it be sufficient, if only the affected sequence area gets repainted?
			}
			else {
				throw new InternalError("Not implemented.");
				//TODO Implement.
			}
		}
	}


	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {
		getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
		//TODO Remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
		getDataAreas().getSequenceDataChangeListener().afterProviderChanged(previous, current);
		assignSizeToAll();  //TODO reinsertSubements()?
	}


	@Override
	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
		if (hasToolkitComponent()) {
			if (e.getSource().equals(getDataAreas())) {
				getDataAreas().setLocalMaxLengthBeforeAfterRecalculate();
				updateSubelements();
			}
			assignSizeToAll();
		}
	}


	@Override
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
		//TODO implement (Possibly delegate to dataAreaInsertedRemoved()?)
	}
}
