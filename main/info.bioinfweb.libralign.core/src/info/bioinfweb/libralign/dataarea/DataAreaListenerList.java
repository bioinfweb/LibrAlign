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


import java.util.EventObject;
import java.util.HashSet;

import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;



public class DataAreaListenerList extends HashSet<DataAreasListener> implements ListChangeListener<DataArea> {
	@Override
	public void beforeElementsAdded(ListAddEvent<DataArea> event) {
		forEach(listener -> listener.beforeElementsAdded(event));
	}

	
	@Override
	public void afterElementsAdded(ListAddEvent<DataArea> event) {
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
		forEach(listener -> listener.afterElementsRemoved(event));
	}
	
	
	public void fireAfterVisibilityChanged(DataArea source, boolean visible) {
		forEach(listener -> listener.afterVisibilityChanged(new DataAreaVisibilityChangeEvent(source, visible)));
	}
}
