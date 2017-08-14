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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.tic.toolkit.ToolkitComponent;

import java.util.Iterator;
import java.util.NoSuchElementException;



/**
 * Adapter class that converts an iterator of {@link ToolkitComponent} to an iterator of {@link AlignmentSubArea}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentSubAreaIterator implements Iterator<AlignmentSubArea> {
	private AlignmentArea area;
	private Iterator<String> idIterator;
	private Iterator<DataArea> dataAreaIterator;
	private boolean bottomAreaIteratorCreated = false;


	public AlignmentSubAreaIterator(AlignmentArea area) {
		super();
		this.area = area;
		idIterator = area.getSequenceOrder().idIterator();
		dataAreaIterator = area.getDataAreas().getTopAreas().iterator();
	}
	
	
	@Override
	public boolean hasNext() {
		return dataAreaIterator.hasNext() || idIterator.hasNext() || (!bottomAreaIteratorCreated && !area.getDataAreas().getBottomAreas().isEmpty());
	}

	
	@Override
	public AlignmentSubArea next() {
		if (dataAreaIterator.hasNext()) {
			return dataAreaIterator.next();
		}
		else if (idIterator.hasNext()) {
			String sequenceID = idIterator.next();
			dataAreaIterator = area.getDataAreas().getSequenceAreas(sequenceID).iterator();
			return area.getContentArea().getSequenceAreaByID(sequenceID);
		}
		else if (!bottomAreaIteratorCreated) {
			dataAreaIterator = area.getDataAreas().getBottomAreas().iterator();
			bottomAreaIteratorCreated = true;
			return dataAreaIterator.next();  // May throw a NoSuchElementException as well.
		}
		else {
			throw new NoSuchElementException("This iterator does not contain more elements.");
		}
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
