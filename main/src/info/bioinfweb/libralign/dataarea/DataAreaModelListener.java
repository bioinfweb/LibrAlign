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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.libralign.AlignmentArea;

import java.util.EventListener;



/**
 * This interface should be implemented by classes that want to track changes of the data areas
 * contained in a {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public interface DataAreaModelListener extends EventListener {
	public void dataAreaModelInsertedRemoved(DataAreaChangeEvent e);
	
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e);
}
