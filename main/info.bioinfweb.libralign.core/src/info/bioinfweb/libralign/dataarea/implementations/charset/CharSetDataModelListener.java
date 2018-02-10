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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColorChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColumnChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;



/**
 * This interface should be implemented by classes which want to receive information on changes
 * in an instance of {@link CharSetDataModel} and the {@link CharSet} instances within.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public interface CharSetDataModelListener {
	public void afterCharSetChange(CharSetChangeEvent e);
	
	public void afterCharSetRenamed(CharSetRenamedEvent e);
	
	public void afterCharSetColumnChange(CharSetColumnChangeEvent e);
	
	public void afterCharSetColorChange(CharSetColorChangeEvent e);

	/**
	 * Called if this listener was moved to another instance of {@link CharSetArea}.
	 * <p>
	 * This happens if the alignment model of an {@link CharSetArea} was changed.
	 * 
	 * @param previous the model this listener was attached to before the event happened
	 * @param current the new model this listener is attached to now
	 */
	public void afterModelChanged(CharSetDataModel previous, CharSetDataModel current);
}
