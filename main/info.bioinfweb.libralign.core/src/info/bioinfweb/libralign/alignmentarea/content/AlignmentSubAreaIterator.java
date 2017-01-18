/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.ToolkitComponent;

import java.util.Iterator;



/**
 * Adapter class that converts an iterator of {@link ToolkitComponent} to an iterator of {@link AlignmentSubArea}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentSubAreaIterator<C extends TICComponent> implements Iterator<C> {
	private Iterator iterator;


	public AlignmentSubAreaIterator(Iterator iterator) {
		super();
		this.iterator = iterator;
	}
	
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	
	@Override
	public C next() {
		Object component = iterator.next();
		if (component instanceof ToolkitComponent) {
			return (C)((ToolkitComponent)component).getIndependentComponent();  // Checking C would only be possible with a Class parameter.
		}
		throw new ClassCastException("A child component of an invalid type was found.");
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
