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


import info.bioinfweb.commons.collections.observable.ListChangeAdapter;



/**
 * Event listener adapter for {@link DataAreasListener} with empty implementations for all event methods.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class DataAreasAdapter extends ListChangeAdapter<DataArea> implements DataAreasListener {
	@Override
	public void afterVisibilityChanged(DataAreaVisibilityChangeEvent e) {}
}
