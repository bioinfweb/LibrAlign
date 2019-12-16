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
package info.bioinfweb.libralign.model.io;


import org.apache.commons.collections4.MultiValuedMap;

import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Interface to be implemented by all readers that process <i>JPhyloIO</i> events to read data or metadata stored together 
 * with alignments.
 * <p>
 * Implementations of this interface should be used to read implementations of {@link DataModel} but can also be used to 
 * read metadata into any other type of object that will be used by application code.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 *
 * @param <E> the type of data element to be read by this reader (This will often be an implementation of {@link DataModel} 
 *        but does not have to be.)
 */
public interface DataElementEventReader<E> extends JPhyloIOEventListener {
	/**
	 * Returns the instance of the main reader (for reading alignment and data models) which
	 * uses this data model reader.
	 * 
	 * @return the parent alignment and data reader
	 */
	public AlignmentDataReader getMainReader();
	
	/**
	 * Returns the data element objects that have been read from the underlying <i>JPhyloIO</i> event stream until now.
	 * <p>
	 * The list may be modified by application classes (e.g. to remove consumed objects), but this may change 
	 * the future behavior of some implementations.
	 * 
	 * @return a list with the currently completed data elements (maybe empty)
	 */
	public MultiValuedMap<DataElementKey, E> getCompletedElements();
	
	/**
	 * Returns the class of data elements that are created by this reader.
	 * <p>
	 * If a reader can create instances of multiple classes, this method should return a shared base class or interface.
	 * 
	 * @return the element class, never {@code null}
	 */
	public Class<E> getElementClass();
}
