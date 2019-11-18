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
package info.bioinfweb.libralign.dataarea;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataelement.DataLists;



/**
 * Class used internally by {@link AlignmentArea}.
 * <p>
 * It is used to forward lists events from a {@link DataLists} instance containing {@link DataArea}s
 * to a set of {@link DataAreasListener}s. Additionally, a property change listener is registered at every added data area to forwards events on 
 * visibility changes. Property change listeners are automatically removed from data areas that are removed from the monitored list object. 
 * 
 * @author Ben Stöver
 * @since 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class DataAreaListenerList extends HashSet<DataAreasListener> implements ListChangeListener<DataArea>, PropertyChangeListener {
	public static final String VISIBILITY_PROPERTY_NAME = "visible";
	
	
	@Override
	public void beforeElementsAdded(ListAddEvent<DataArea> event) {
		forEach(listener -> listener.beforeElementsAdded(event));
	}

	
	@Override
	public void afterElementsAdded(ListAddEvent<DataArea> event) {
		event.getAffectedElement().addPropertyChangeListener(VISIBILITY_PROPERTY_NAME, this);
		forEach(listener -> listener.afterElementsAdded(event));
	}

	
	@Override
	public void beforeElementReplaced(ListReplaceEvent<DataArea> event) {
		forEach(listener -> listener.beforeElementReplaced(event));
	}

	
	@Override
	public void afterElementReplaced(ListReplaceEvent<DataArea> event) {
		forEach(listener -> listener.afterElementReplaced(event));
	}

	
	@Override
	public void beforeElementsRemoved(ListRemoveEvent<DataArea, Object> event) {
		forEach(listener -> listener.beforeElementsRemoved(event));
	}

	
	@Override
	public void afterElementsRemoved(ListRemoveEvent<DataArea, DataArea> event) {
		event.getAffectedElement().removePropertyChangeListener(VISIBILITY_PROPERTY_NAME, this);
		forEach(listener -> listener.afterElementsRemoved(event));
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (VISIBILITY_PROPERTY_NAME.equals(event.getPropertyName())) {
			forEach(listener -> listener.afterVisibilityChanged(new DataAreaVisibilityChangeEvent((DataArea)event.getSource(), (Boolean)event.getNewValue())));
		}
	}
}
