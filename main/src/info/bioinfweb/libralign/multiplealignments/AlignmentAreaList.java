/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.ArrayList;



/**
 * A list implementation {@link MultipleAlignmentsContainer} uses to manage its contained alignment areas.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentAreaList extends ArrayList<AlignmentArea>{
	private MultipleAlignmentsContainer owner;

	
	public AlignmentAreaList(MultipleAlignmentsContainer owner) {
		super();
		this.owner = owner;
	}


	public MultipleAlignmentsContainer getOwner() {
		return owner;
	}

	
	private void checkContainer(AlignmentArea alignmentArea) {
		if (alignmentArea.getContainer() != getOwner()) {
			throw new IllegalArgumentException("The alignment area to be inserted does not reference this instance as its container.");
		}
	}
	

	@Override
	public boolean add(AlignmentArea e) {
		checkContainer(e);
		return super.add(e);
	}
	

	@Override
	public void add(int index, AlignmentArea element) {
		checkContainer(element);
		super.add(index, element);
	}

	

}
