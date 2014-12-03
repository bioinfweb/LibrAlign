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


import info.bioinfweb.commons.collections.observable.ObservableList;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * A list implementation {@link MultipleAlignmentsContainer} uses to manage its contained alignment areas.
 * <p>
 * Note that this list is not thread safe and should only be modified from the GUI thread (e.g. the Swing thread)
 * since made modifications might trigger GUI updates.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentAreaList extends ObservableList<AlignmentArea> {
	private MultipleAlignmentsContainer owner;

	
	public AlignmentAreaList(MultipleAlignmentsContainer owner) {
		super(new ArrayList<AlignmentArea>());
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
	
	
	private void checkContainers(Collection<? extends AlignmentArea> c) {
		for (AlignmentArea alignmentArea : c) {
			checkContainer(alignmentArea);
		}
	}


	@Override
	protected void beforeAdd(int index,	Collection<? extends AlignmentArea> addedElements) {
		checkContainers(addedElements);  // Prevents the insertion by throwing an exception if one or more elements do not specify the correct container.
	}
	
	
	private void adoptToListChanges() {  //TODO Should this method better be in the owner?
		if (getOwner().hasToolkitComponent()) {
			getOwner().getToolkitComponent().adoptChildAreas();
			getOwner().redistributeHeight();  //TODO Will this be to early for SWT or does layout() have to be called before?
		}
	}

	
	@Override
	protected void afterAdd(int index, Collection<? extends AlignmentArea> addedElements) {
		adoptToListChanges();
	}


	@Override
	protected void afterRemove(Collection<? extends AlignmentArea> removedElements) {
		adoptToListChanges();
	}


	@Override
	protected void afterReplace(int index, AlignmentArea previousElement,	AlignmentArea currentElement) {
		adoptToListChanges();
	}


	public static void main(String[] args) {
		MultipleAlignmentsContainer container = new MultipleAlignmentsContainer();
		List<AlignmentArea> collection = new ArrayList<AlignmentArea>();
		collection.add(new AlignmentArea(container));
		collection.add(new AlignmentArea(container));
		container.getAlignmentAreas().addAll(collection);
	}
}
