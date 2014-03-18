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


import java.util.Iterator;



/**
 * Iterator that only returns visible data areas and skips all the others.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class DataAreaVisibleIterator implements Iterator<DataArea> {
	private Iterator<DataArea> completeIterator;
	private DataArea nextVisible = null;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param completeIterator - an iterator that returns all elements (not only visible data areas)
	 */
	public DataAreaVisibleIterator(Iterator<DataArea> completeIterator) {
		super();
		this.completeIterator = completeIterator;
		setNextVisible();
	}


	private void setNextVisible() {
		nextVisible = null;
		while (completeIterator.hasNext() && (nextVisible == null)) {
			DataArea next = completeIterator.next();
			if (next.isVisible()) {
				nextVisible = next;
			}
		}
	}
	
	
	@Override
	public boolean hasNext() {
		return nextVisible != null;
	}

	
	@Override
	public DataArea next() {
		DataArea result = nextVisible;
		setNextVisible();
		return result;
	}

	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();		
	}	
}
