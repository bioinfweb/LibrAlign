/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.io;


import info.bioinfweb.jphyloio.JPhyloIOEventListener;
import info.bioinfweb.libralign.sequenceprovider.DataProvider;



/**
 * Interface to be implemented by all readers that process JPhyloIO events to read data to be stored in
 * implementations of {@link DataProvider}.
 * <p>
 * All implementations should accept an instance of {@link DataProvider} as a constructor parameter that
 * will than be returned by {@link #getModel()}. Implementing classes should not allow to change the model
 * during runtime, since changing the model between two calls of 
 * {@link #processEvent(info.bioinfweb.jphyloio.JPhyloIOEventReader, info.bioinfweb.jphyloio.events.JPhyloIOEvent)}
 * should be avoided.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface DataModelEventReader extends JPhyloIOEventListener {
	/**
	 * Returns the model object associated with this reader.
	 * 
	 * @return the associated model
	 */
	public DataProvider getModel();
}
