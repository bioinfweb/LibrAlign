/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.multiplealignments;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeAdapter;
import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaVisibilityChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreasListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.events.DataModelChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * Helper class to forward events from elements within a {@link MultipleAlignmentsContainer} used by {@link MultipleAlignmentsContainer} internally.
 * <p>
 * There should be no need to use this class directly in application code.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
class SingleMultipleAlignmentsEventForwarder {
	private MultipleAlignmentsContainer container;
	private Set<AlignmentModelListener<Object>> alignmentModellisteners = new HashSet<>();
	private Set<DataAreasListener> dataAreasListeners = new HashSet<>();
	
	
	private final ListChangeListener<AlignmentArea> containerListener = new ListChangeAdapter<AlignmentArea>() {
		@Override
		public void afterElementsAdded(ListAddEvent<AlignmentArea> event) {
			event.getAffectedElements().forEach(area -> addAlignmentAreaListeners(area));
		}

		
		@Override
		public void afterElementReplaced(ListReplaceEvent<AlignmentArea> event) {
			if (event.getOldElement() != null) {
				removeAlignmentAreaListeners(event.getOldElement());
			}
			
			if (event.getNewElement() != null) {
				addAlignmentAreaListeners(event.getNewElement());
			}
		}

		
		@Override
		public void afterElementsRemoved(ListRemoveEvent<AlignmentArea, AlignmentArea> event) {
			event.getAffectedElements().forEach(area -> removeAlignmentAreaListeners(area));
		}
	};
	
	
	private final PropertyChangeListener alignmentModelChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (AlignmentArea.ALIGNMENT_MODEL_PROPERTY_NAME.equals(event.getPropertyName())) {
				if (event.getOldValue() != null) {
					((AlignmentModel<?>)event.getOldValue()).removeModelListener(alignmentModelListener);
				}
				
				if (event.getNewValue() != null) {
					((AlignmentModel<?>)event.getNewValue()).addModelListener(alignmentModelListener);
				}
			}
		}
	};
	
	
	private final AlignmentModelListener<Object> alignmentModelListener = new AlignmentModelListener<Object>() {
		@Override
		public void afterSequenceChange(SequenceChangeEvent<Object> event) {
			alignmentModellisteners.forEach(listener -> listener.afterSequenceChange(event));
		}

		@Override
		public void afterSequenceRenamed(SequenceRenamedEvent<Object> event) {
			alignmentModellisteners.forEach(listener -> listener.afterSequenceRenamed(event));
		}

		@Override
		public void afterTokenChange(TokenChangeEvent<Object> event) {
			alignmentModellisteners.forEach(listener -> listener.afterTokenChange(event));
		}

		@Override
		public void afterDataModelChange(DataModelChangeEvent<Object> event) {
			alignmentModellisteners.forEach(listener -> listener.afterDataModelChange(event));
		}
	};
	
	
	private final DataAreasListener dataAreasListener = new DataAreasListener() {
		@Override
		public void beforeElementsRemoved(ListRemoveEvent<DataArea, Object> event) {
			dataAreasListeners.forEach(listener -> listener.beforeElementsRemoved(event));
		}
		
		@Override
		public void beforeElementsAdded(ListAddEvent<DataArea> event) {
			dataAreasListeners.forEach(listener -> listener.beforeElementsAdded(event));
		}
		
		@Override
		public void beforeElementReplaced(ListReplaceEvent<DataArea> event) {
			dataAreasListeners.forEach(listener -> listener.beforeElementReplaced(event));
		}
		
		@Override
		public void afterElementsRemoved(ListRemoveEvent<DataArea, DataArea> event) {
			dataAreasListeners.forEach(listener -> listener.afterElementsRemoved(event));
		}
		
		@Override
		public void afterElementsAdded(ListAddEvent<DataArea> event) {
			dataAreasListeners.forEach(listener -> listener.afterElementsAdded(event));
		}
		
		@Override
		public void afterElementReplaced(ListReplaceEvent<DataArea> event) {
			dataAreasListeners.forEach(listener -> listener.afterElementReplaced(event));
		}
		
		@Override
		public void afterVisibilityChanged(DataAreaVisibilityChangeEvent event) {
			dataAreasListeners.forEach(listener -> listener.afterVisibilityChanged(event));
		}
	};
	
	
	public SingleMultipleAlignmentsEventForwarder(MultipleAlignmentsContainer container) {
		super();
		this.container = container;
		registerInitialListeners();
	}


	private void registerInitialListeners() {
		container.getAlignmentAreas().addListChangeListener(containerListener);
		container.getAlignmentAreas().forEach(area -> addAlignmentAreaListeners(area));
	}
	
	
	private void addAlignmentAreaListeners(AlignmentArea area) {
		area.addPropertyChangeListener(AlignmentArea.ALIGNMENT_MODEL_PROPERTY_NAME, alignmentModelChangeListener);
		if (area.hasAlignmentModel()) {
			area.getAlignmentModel().addModelListener(alignmentModelListener);
		}
		
		area.addDataAreasListener(dataAreasListener);
	}
	
	
	private void removeAlignmentAreaListeners(AlignmentArea area) {
		area.removePropertyChangeListener(AlignmentArea.ALIGNMENT_MODEL_PROPERTY_NAME, alignmentModelChangeListener);
		if (area.hasAlignmentModel()) {
			area.getAlignmentModel().removeModelListener(alignmentModelListener);
		}

		area.removeDataAreasListener(dataAreasListener);
	}

	
	public boolean addAlignmentModelListener(AlignmentModelListener<Object> listener) {
		return alignmentModellisteners.add(listener);
	}

	
	public boolean removeAlignmentModelListener(AlignmentModelListener<Object> listener) {
		return alignmentModellisteners.remove(listener);
	}
	
	
	public boolean addDataAreasListener(DataAreasListener listener) {
		return dataAreasListeners.add(listener);
	}

	
	public boolean removeDataAreasListener(DataAreasListener listener) {
		return dataAreasListeners.remove(listener);
	}
	
	
	public boolean isEmpty() {
		return alignmentModellisteners.isEmpty() && dataAreasListeners.isEmpty();
	}


	/**
	 * Unregisters all listeners from the associated {@link MultipleAlignmentsContainer} and its {@link AlignmentArea}s.
	 * <p>
	 * Note that this object is not functional anymore after calling this method. It should only be called before discarding the object.
	 */
	public void unregisterListeners() {
		container.getAlignmentAreas().removeListChangeListener(containerListener);
		container.getAlignmentAreas().forEach(area -> removeAlignmentAreaListeners(area));
	}
}
