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


import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeAdapter;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.commons.collections.observable.ObservableList;
import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.libralign.actions.AlignmentActionProvider;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.content.ToolkitSpecificAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.order.SequenceOrder;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsListener;
import info.bioinfweb.libralign.alignmentarea.paintsettings.TokenPainterReplacedEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.ZoomChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.ColorOverlay;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaFactory;
import info.bioinfweb.libralign.dataarea.DataAreaListenerList;
import info.bioinfweb.libralign.dataarea.DataAreaVisibilityChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreasAdapter;
import info.bioinfweb.libralign.dataarea.DataAreasListener;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.editsettings.EditSettings;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelAdapter;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.events.DataModelChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.tic.scrolling.ScrollingTICComponent;



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
 * <a href="http://r.bioinfweb.info/LibrAlignToolkitDoc"><i>LibrAlign</i> documentation</a> for details.
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
 * <h3><a id="toolkit"></a>Creating toolkit components</h3>
 * Since <i>LibrAlign</i> works with both <i>Swing</i> and <i>SWT</i> all its GUI components are {@link TICComponent}s
 * and therefore allow to create <i>Swing</i> and <i>SWT</i> instances from them using the factory classes from 
 * <i>TIC</i>. (See the <a href="http://r.bioinfweb.info/LibrAlignToolkitDoc">documentation</a> for further details.)
 * <p>
 * <i>SWT</i> components used to display an alignment are available in two versions. One creates a subcomponent for 
 * each sequence and data area in the alignment. This allows e.g. to provide custom toolkit-specific components for
 * custom data area implementations (instead of just implementing the 
 * {@link DataArea#paintPart(info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent)} method), which may
 * be necessary in rare cases, e.g. to nest other GUI components in a data area. Using subcomponents in <i>SWT</i> 
 * has the disadvantage that they have a size limit of 2^15-1 px in <i>SWT</i> on many operating systems (currently
 * <i>Windows</i> or <i>Linux</i>). This width limit can easily be reached when displaying nucleotide or protein 
 * sequences and is due to limitations in the GUI libraries of these operating systems and cannot be fixed within 
 * <i>LibrAlign</i> or <i>SWT</i>.
 * <p>
 * As a workaround for that problem alternative <i>SWT</i> component implementations that do not create subcomponents 
 * for sequences and data areas are available, which paint the whole alignment directly on a shared component and 
 * allow to scroll alignments with a maximum width and height of {@link Integer#MAX_VALUE} px. (Which is much larger 
 * than the maximum in the first alternative.) These components are not able to make use of custom <i>SWT</i> 
 * components associated with custom or third party data areas. Only data areas that paint directly by implementing 
 * {@link DataArea#paintPart(info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent)} can be used in this 
 * case.
 * <p>
 * The strategy to be used can be set with an additional {@code boolean} parameter passed to the <i>TIC</i> factory
 * class when creating the <i>SWT</i> toolkit component of an instance of this class. (See 
 * <a href="http://r.bioinfweb.info/LibrAlignToolkitDoc">here</a> on how to create toolkit components with such a
 * factory.) If the parameter is omitted, no subcomponents will be created.
 * <pre>
 *   SWTComponentFactory.getInstance().getSWTComponent(someAlignmentArea, shell, SWT.NONE, true);  // Creates an SWT component <b>with</b> subcomponents.
 *   SWTComponentFactory.getInstance().getSWTComponent(someAlignmentArea, shell, SWT.NONE, false);  // Creates an SWT component <b>without</b> subcomponents.
 *   SWTComponentFactory.getInstance().getSWTComponent(someAlignmentArea, shell, SWT.NONE);  // Creates an SWT component <b>without</b> subcomponents.
 * </pre>
 * Since no such limitation exists in <i>Swing</i>, subcomponents will always be created there. This problem is 
 * only relevant for <i>SWT</i> under <i>Windows</i> or <i>Linux</i>. (You can determine whether an existing 
 * component has subcomponents or using {@link ToolkitSpecificAlignmentContentArea#hasSubcomponents()}.) 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 * 
 * @see MultipleAlignmentsContainer
 * @see AlignmentContentArea
 * @see AlignmentLabelArea
 * @see AlignmentModel
 * @see <a href="http://r.bioinfweb.info/LibrAlignToolkitDoc">LibrAlign documentation on working with toolkits</a> 
 */
public class AlignmentArea extends ScrollingTICComponent {
	public static final int MIN_PART_AREA_HEIGHT = 5;
	public static final String ALIGNMENT_MODEL_PROPERTY_NAME = "model";
	public static final String VERTICAL_SCROLLING_PROPERTY_NAME = "allowVerticalScrolling";
	
	/** Defines the width of the divider of the GUI components for the head, content, and bottom area. */ 
	public static final int DIVIDER_WIDTH = 2;
	

	private AlignmentModel<?> alignmentModel = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private DataAreaLists dataAreas;
	private DataAreaListenerList dataAreaListenerList;
	private SizeManager sizeManager = new SizeManager(this);
	private PaintSettings paintSettings;
	private EditSettings editSettings;
	private SelectionModel selection;
	private ObservableList<ColorOverlay> overlays;
	private DataAreaFactory dataAreaFactory = null;
	protected PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
	private AlignmentActionProvider<Object> actionProvider = new AlignmentActionProvider<Object>(this);

	private MultipleAlignmentsContainer container = null;
	private AlignmentContentArea alignmentContentArea;
	private AlignmentLabelArea alignmentLabelArea;
	private boolean allowVerticalScrolling = true;
	private Rectangle lastCursorRectangle = null;
	
	private final AlignmentModelListener<Object> alignmentModelListener = new AlignmentModelListener<Object>() {
		@Override
		public void afterSequenceChange(SequenceChangeEvent<Object> e) {
			if (e.getSource().equals(getAlignmentModel())) {
				if (e.getType().equals(ListChangeType.DELETION)) {
					getDataAreas().removeSequenceList(e.getSequenceID());  //TODO The model should remove the respective data models on its own and will fire event for them. There should be no need to remove data areas here.
				}

				getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
				getSequenceOrder().refreshFromSource();
				updateSubelements();
			}
			assignSizeToAll();
		}


		@Override
		public void afterSequenceRenamed(SequenceRenamedEvent<Object> e) {
			if (e.getSource().equals(getAlignmentModel())) {
				getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
			}
			assignSizeToAll();  // Other label areas might also have to adopt their width.
		}


		@Override
		public void afterTokenChange(TokenChangeEvent<Object> e) {
			getContentArea().setUpdateOngoing(true);
			try {
				assignSizeToAll();
				if (getContentArea().hasToolkitComponent()) {
					getContentArea().getToolkitComponent().repaintSequences();  // Necessary when neither the selection changes nor the size of the sequence areas changed (e.g. when deleting right in a sequence with an attached pherogram with space before and after the alignment).
				}
			}
			finally {
				getContentArea().setUpdateOngoing(false);  // Perform one single paint operation, if it was requested. (Otherwise in case of a shared directly painted component, separate repaint operations for all data areas and the sequences may happen.)
			}
		}


		@Override
		public void afterDataModelChange(DataModelChangeEvent<Object> event) {
			if (event.getType().equals(ListChangeType.DELETION) || event.getType().equals(ListChangeType.REPLACEMENT)) {
				//TODO Determine respective data areas and remove or unlink them.
				//     - Call getDataAreaFactory().removeDataArea(dataArea) only of this event is not the result of a sequence removal, since unlinking is not an option then.
			}
			if (hasDataAreaFactory() && (event.getType().equals(ListChangeType.INSERTION) || event.getType().equals(ListChangeType.REPLACEMENT))) {
				addDataAreas(event.getDataModel(), event.getSequenceID());
			}
		}


		private void addDataAreas(DataModel<?> model, String sequenceID) {
			List<DataAreaFactory.DataAreaResult> dataAreas = new ArrayList<DataAreaFactory.DataAreaResult>();
			getDataAreaFactory().createDataAreas(model, sequenceID, dataAreas);
			for (DataAreaFactory.DataAreaResult result : dataAreas) {
				getDataAreas().addDataArea(result.getDataArea(), result.getPosition(), sequenceID);
			}
		}
	};
	
	private final PaintSettingsListener paintSettingsListener = new PaintSettingsListener() {
		private void updateSize() {
			getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
			assignSizeToAll();
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("cursorColor") || event.getPropertyName().equals("cursorLineWidth") ||
					event.getPropertyName().equals("selectionColor")) {
				
				if (event.getPropertyName().equals("cursorLineWidth")) {
					getSizeManager().setLocalMaxLengthBeforeAfterRecalculate();
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
	 * Creates a new instance of this class that shall not part of a {@link MultipleAlignmentsContainer} and has its own
	 * instance of {@link EditSettings}
	 * <p>
	 * If you want to create an alignment area that shall be inserted into a {@link MultipleAlignmentsContainer}
	 * use {@link #AlignmentArea(MultipleAlignmentsContainer))} instead.
	 */
	public AlignmentArea() {
		this(null, new EditSettings());
	}
	
	
	/**
	 * Creates a new instance of this class that shall not part of a {@link MultipleAlignmentsContainer}.
	 * <p>
	 * If you want to create an alignment area that shall be inserted into a {@link MultipleAlignmentsContainer}
	 * use {@link #AlignmentArea(MultipleAlignmentsContainer))} instead.
	 * 
	 * @param editSettings the edit settings object to be used with the new instance
	 */
	public AlignmentArea(EditSettings editSettings) {
		this(null, editSettings);
	}
	
	
	/**
	 * Creates a new instance of this class to be inserted into a {@link MultipleAlignmentsContainer}.
	 * 
	 * @param container the container where the returned instance will be contained in
	 */
	public AlignmentArea(MultipleAlignmentsContainer container) {
		this(container, null);
	}
	
	
	/**
	 * Creates a new instance of this class to be inserted into a {@link MultipleAlignmentsContainer}.
	 * 
	 * @param container the container where the returned instance will be contained in
	 * @param editSettings the edit settings object to be used with the new instance (Must not be {@code null} if 
	 *        {@code container} is {@code null}.
	 */
	protected AlignmentArea(MultipleAlignmentsContainer container, EditSettings editSettings) {
		super();
		this.container = container;
		
		if (hasContainer()) {
			this.editSettings = getContainer().getEditSettings();
		}
		else {
			this.editSettings = editSettings;
		}
		if (this.editSettings == null) {
			throw new NullPointerException("The EditSettings object associated with an AlignmentArea must not be null.");
		}
		
		selection = new SelectionModel(this);
		selection.addSelectionListener(new SelectionListener<GenericEventObject<SelectionModel>>() {
					@Override
					public void selectionChanged(GenericEventObject<SelectionModel> event) {
						scrollCursorToVisible();
					}
				});
		
		overlays = new ObservableList<ColorOverlay>(new ArrayList<ColorOverlay>());
		overlays.addListChangeListener(new ListChangeAdapter<ColorOverlay>() {
			@Override
			public void afterElementsRemoved(ListRemoveEvent<ColorOverlay, ColorOverlay> event) {
				repaint();
			}
			
			@Override
			public void afterElementsAdded(ListAddEvent<ColorOverlay> event) {
				repaint();
			}
			
			@Override
			public void afterElementReplaced(ListReplaceEvent<ColorOverlay> event) {
				repaint();
			}
		});
		
		alignmentContentArea = new AlignmentContentArea(this);  // The selection object must already have been created here.
		alignmentLabelArea = new AlignmentLabelArea(this);  // Must be called after alignmentContentArea has been created.

		dataAreaListenerList = new DataAreaListenerList();
		dataAreas = new DataAreaLists(this, dataAreaListenerList);
		dataAreaListenerList.add(new DataAreasAdapter() {
			@SuppressWarnings("unchecked")
			private void react(EventObject event) {
				if (hasToolkitComponent()) {
					if (((DataList<AlignmentArea, DataArea>)event.getSource()).getOwner() == getDataAreas()) {
						getSizeManager().setLocalMaxLengthBeforeAfterRecalculate();
						updateSubelements();
					}
					assignSizeToAll();
				}
			}
			
			@Override
			public void afterVisibilityChanged(DataAreaVisibilityChangeEvent event) {
				react(event);
			}

			@Override
			public void afterElementsAdded(ListAddEvent<DataArea> event) {
				react(event);
			}

			@Override
			public void afterElementReplaced(ListReplaceEvent<DataArea> event) {
				react(event);
			}

			@Override
			public void afterElementsRemoved(ListRemoveEvent<DataArea, DataArea> event) {
				react(event);
			}
		});
		
		paintSettings = new PaintSettings(this);
		paintSettings.addListener(paintSettingsListener);
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
	 * Changes the alignment model providing the data for this instance and moves the listener of this view to the new model.
	 * 
	 * @param alignmentModel the new alignment model to use from now on
	 * @param moveListeners Specify {@code true} here, if you want the {@link AlignmentModelListener}s
	 *        attached to the current model to be moved to the specified {@code alignmentModel},
	 *        {@code false} if the listeners shall remain attached to the old model. (This instance
	 *        is also registered as a listener and is always moved to the new object, no matter which value is
	 *        specified here.)
	 * @return the previous model that has been replaced or {@code null} if there was no model before
	 */
	public AlignmentModel<?> setAlignmentModel(AlignmentModel<?> alignmentModel) {
		AlignmentModel<?> result = this.alignmentModel;
		if (alignmentModel != this.alignmentModel) {
			if (this.alignmentModel != null) {
				this.alignmentModel.removeModelListener(alignmentModelListener);
			}
			AlignmentModel<?> formerModel = this.alignmentModel;
			
			this.alignmentModel = alignmentModel;
			
			if (alignmentModel != null) {
				alignmentModel.addModelListener(alignmentModelListener);
			}
			propertyChangeListeners.firePropertyChange(ALIGNMENT_MODEL_PROPERTY_NAME, formerModel, alignmentModel);
			
			getLabelArea().setLocalMaxWidthRecalculateToAll();  // Needs to be called before assignSizeToAll().
			//TODO Remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).)
			getSequenceOrder().setSourceSequenceOrder();  // Update sequence names
			updateSubelements();
			getPaintSettings().getTokenPainterList().afterAlignmentModelChanged();
			assignSizeToAll();  //TODO reinsertSubements()?
		}
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
	public DataAreaLists getDataAreas() {
		return dataAreas;
	}


	public SizeManager getSizeManager() {
		return sizeManager;
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
	 * Returns an editable list of color overlays to be used when painting the contents of this area.
	 * 
	 * @return a (possibly empty) editable list
	 */
	public List<ColorOverlay> getOverlays() {
		return overlays;
	}

	
	/**
	 * Determines whether this instance currently uses a data area factory.
	 * <p>
	 * If none is used, {@link #getDataAreaFactory()} will return {@code null}.
	 * 
	 * @return {@code true} if a factory is used, {@code false} otherwise
	 */
	public boolean hasDataAreaFactory() {
		return getDataAreaFactory() != null;
	}
	

	/**
	 * Returns the data area factory used by this instance.
	 * <p>
	 * See {@link #setDataAreaFactory(DataAreaFactory)} for details on how such a factory is used by this instance.
	 * 
	 * @return the data area factory instance or {@code null} if no data area factory is used
	 */
	public DataAreaFactory getDataAreaFactory() {
		return dataAreaFactory;
	}


	/**
	 * Sets a new data area factory that will be used by this instance from now on.
	 * <p>
	 * A data area factory specified here will be used to automatically create and add data area instances every time a new data model is associated
	 * with the alignment model that is displayed by this instance. It will also be used to create data areas for all data models associated with a
	 * new alignment model (after calling {@link #setAlignmentModel(AlignmentModel)}).
	 * 
	 * @param dataAreaFactory the new factory (May be {@code null}.)
	 */
	public void setDataAreaFactory(DataAreaFactory dataAreaFactory) {
		this.dataAreaFactory = dataAreaFactory;
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
	 * Returns the alignment content area used internally by this instance. Content areas display the sequence and 
	 * data areas. This instance acts as the scroll container for the content area instance returned here.
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
	 *        whole width or {@code false} if scrolling should be avoided if possible.
	 */
	public void setAllowVerticalScrolling(boolean allowVerticalScrolling) {
		if (allowVerticalScrolling != this.allowVerticalScrolling) {
			boolean formerValue = this.allowVerticalScrolling;
			this.allowVerticalScrolling = allowVerticalScrolling;
			propertyChangeListeners.firePropertyChange(VERTICAL_SCROLLING_PROPERTY_NAME, formerValue, allowVerticalScrolling);
			//TODO redistribute size
		}
	}


	public void scrollCursorToVisible() {
		Rectangle visibleRectangle = getVisibleRectangle();
		Rectangle currentRectangle = getContentArea().getCursorRectangle().getBounds();  // Rounding takes place here.
		Rectangle scrollRectangle = new Rectangle(currentRectangle);
		int dy = currentRectangle.height - visibleRectangle.height;
		if ((dy > 0) && (lastCursorRectangle != null)) {
			scrollRectangle.height -= dy;
			if (lastCursorRectangle.y == currentRectangle.y) {  // Not moved upwards (= downwards).
				scrollRectangle.y += dy;
			}
		}
		scrollRectangleToVisible(scrollRectangle);
		lastCursorRectangle = currentRectangle;
	}
	
	
	/**
	 * Returns the name of a toolkit specific <i>Swing</i> component displaying the contents of this instance.
	 * 
	 * @param parameters This implementation does not recognize any additional parameters.
	 * @return the name of a toolkit specific <i>Swing</i> component
	 */
	@Override
	protected String getSwingComponentClassName(Object... parameters) {
		return "info.bioinfweb.libralign.alignmentarea.ScrollContainerSwingAlignmentArea";
	}


	/**
	 * Returns the name of a toolkit specific <i>SWT</i> component displaying the contents of this instance.
	 * 
	 * @param parameters This implementation expects no or a single {@link Boolean} parameter which determines 
	 *        whether the name of an <i>SWT</i> component using subcomponents for each sequence and data area 
	 *        or a shared component for direct painting shall be returned. If no parameter is passed, 
	 *        {@code false} is assumed.
	 * @return the name of a toolkit specific <i>SWT</i> component
	 */
	@Override
	protected String getSWTComponentClassName(Object... parameters) {
		if (GUITools.determineUseSubcomponents(parameters)) {
			return "info.bioinfweb.libralign.alignmentarea.ScrollContainerSWTAlignmentArea";
		}
		else {
			return "info.bioinfweb.libralign.alignmentarea.DirectPaintingSWTAlignmentArea";
		}
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
		if (hasToolkitComponent()) {
			if (getLabelArea().hasToolkitComponent()) {
				Iterator<AlignmentLabelSubArea> iterator = getLabelArea().subAreaIterator();
				while (iterator.hasNext()) {
					iterator.next().assignSize();;
				}
				getLabelArea().assignSize();
			}
			
			if (getContentArea().hasToolkitComponent()) {
				if (getContentArea().getToolkitComponent().hasSubcomponents()) {
					Iterator<AlignmentSubArea> contentIterator = getContentArea().subAreaIterator();
					while (contentIterator.hasNext()) {
						contentIterator.next().assignSize();;
					}
				}
				getContentArea().assignSize();
			}
			
			assignSize();
		}
	}
	
	
	/**
	 * Recalculates the size of this and all subcomponents. If this area is contained within a 
	 * {@link MultipleAlignmentsContainer} this container and all its subcomponents will do the
	 * same.
	 * <p>
	 * This method can be used e.g. by data areas to make sure that their owning alignment area
	 * adjusts to their content changes that possibly result in size changes.
	 */
	public void revalidate() {
		getLabelArea().setLocalMaxWidthRecalculateToAll();
		if (hasContainer()) {
			getContainer().assignSizeToAll();
		}
		else {
			assignSizeToAll();
		}
		//TODO Are additional repaint operations necessary?
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

	
	public boolean addDataAreasListener(DataAreasListener listener) {
		return dataAreaListenerList.add(listener);
	}
	
	
	public boolean removeDataAreasListener(DataAreasListener listener) {
		return dataAreaListenerList.remove(listener);
	}
	
	
	/**
	 * Registers a property change listener for all properties.
	 * <p>
	 * This class fires events for the following properties:
	 * <ul>
	 *   <li>{@value #ALIGNMENT_MODEL_PROPERTY_NAME} if {@link #setAlignmentModel(AlignmentModel)} is called with a new model</li>
	 *   <li>{@value #VERTICAL_SCROLLING_PROPERTY_NAME} if {@link #setAllowVerticalScrolling(boolean)} is called with a new value</li>
	 * </ul>
	 * <p>
	 * Note that no property change events are fired for inherited properties, e.g., {@link #setToolkitComponent(info.bioinfweb.tic.toolkit.ToolkitComponent)}.
	 * 
	 * @param listener the new listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.addPropertyChangeListener(listener);
	}


	/**
	 * Registers a property change listener for the specified property.
	 * <p>
	 * This class fires events for the following properties:
	 * <ul>
	 *   <li>{@value #ALIGNMENT_MODEL_PROPERTY_NAME} if {@link #setAlignmentModel(AlignmentModel)} is called with a new model</li>
	 *   <li>{@value #VERTICAL_SCROLLING_PROPERTY_NAME} if {@link #setAllowVerticalScrolling(boolean)} is called with a new value</li>
	 * </ul>
	 * <p>
	 * Note that no property change events are fired for inherited properties, e.g., {@link #setToolkitComponent(info.bioinfweb.tic.toolkit.ToolkitComponent)}.
	 * 
	 * @param propertyName the name of the property to be informed on (See list above.)
	 * @param listener the new listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeListeners.addPropertyChangeListener(propertyName, listener);
	}


	/**
	 * Remove a {@link PropertyChangeListener} from the listener list.
	 * <p>
	 * This removes a {@link PropertyChangeListener} that was registered for all properties. If {@code listener} was added more than once to the same event source, 
	 * it will be notified one less time after being removed. If {@code listener} is {@code null}, or was never added, no exception is thrown and no action is taken. 
	 * 
	 * @param listener the listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(listener);
	}


	/**
	 * Remove a {@link PropertyChangeListener} for a specific property.
	 * <p>
	 * This removes a {@link PropertyChangeListener} that was registered for all properties. If {@code listener} was added more than once to the same event source, 
	 * it will be notified one less time after being removed. If {@code listener} is {@code null}, or was never added, no exception is thrown and no action is taken. 
	 * 
	 * @param propertyName the name of the property that was listened on
	 * @param listener the listener to be removed
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(propertyName, listener);
	}
}
