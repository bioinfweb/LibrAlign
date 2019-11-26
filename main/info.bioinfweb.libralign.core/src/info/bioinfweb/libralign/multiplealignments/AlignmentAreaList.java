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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.commons.collections.observable.ObservableList;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.ArrayList;
import java.util.Collection;



/**
 * A list implementation {@link MultipleAlignmentsContainer} uses to manage its contained alignment areas.
 * <p>
 * Note that this list is not thread safe and should only be modified from the GUI thread (e.g., the <i>Swing</i> 
 * thread in <i>Swing</i> applications).
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentAreaList extends ObservableList<AlignmentArea> {
	private MultipleAlignmentsContainer owner;
	private MultipleAlignmentsModelEventForwarder<Object> modelEventForwarder;
	private PaintSettingsSynchronizer paintSettingsSynchronizer = new PaintSettingsSynchronizer(this);
	private HorizontalScrollingSynchronizer scrollingSynchronizer = new HorizontalScrollingSynchronizer(this);

	
	public AlignmentAreaList(MultipleAlignmentsContainer owner) {
		super(new ArrayList<AlignmentArea>());
		this.owner = owner;
		modelEventForwarder = new MultipleAlignmentsModelEventForwarder<Object>(owner);
	}


	public MultipleAlignmentsContainer getOwner() {
		return owner;
	}

	
	protected PaintSettingsSynchronizer getPaintSettingsSynchronizer() {
		return paintSettingsSynchronizer;
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
	
	
	private void adoptToListChanges() {
		if (getOwner().hasToolkitComponent()) {
			getOwner().getToolkitComponent().adoptChildAreas();
		}
	}
	
	
	private void addListenersToAlignmentArea(AlignmentArea alignmentArea) {
		if (alignmentArea.hasAlignmentModel()) {
			alignmentArea.getAlignmentModel().addModelListener(modelEventForwarder);
		}
		//alignmentArea.getDataAreas().addListener(modelEventForwarder);  //TODO Reconsider this line when #356 is solved.
		alignmentArea.getPaintSettings().addListener(paintSettingsSynchronizer);
		alignmentArea.getScrollListeners().add(scrollingSynchronizer);
	}

	
	private void removeListenersFromAlignmentArea(AlignmentArea alignmentArea) {
		if (alignmentArea.hasAlignmentModel()) {
			alignmentArea.getAlignmentModel().removeModelListener(modelEventForwarder);
		}
		//alignmentArea.getDataAreas().removeListener(modelEventForwarder);  //TODO Reconsider this line when #356 is solved.
		alignmentArea.getPaintSettings().removeListener(paintSettingsSynchronizer);
		alignmentArea.getScrollListeners().remove(scrollingSynchronizer);
	}

	
	@Override
	protected void afterAdd(int index, Collection<? extends AlignmentArea> addedElements) {
		adoptToListChanges();
		for (AlignmentArea alignmentArea : addedElements) {
			addListenersToAlignmentArea(alignmentArea);
		}
		
		super.afterAdd(index, addedElements);
	}


	@Override
	protected void afterRemove(int index, Collection<? extends AlignmentArea> removedElements) {
		adoptToListChanges();
		for (AlignmentArea alignmentArea : removedElements) {
			removeListenersFromAlignmentArea(alignmentArea);
		}
		
		super.afterRemove(index, removedElements);
	}


	@Override
	protected void afterReplace(int index, AlignmentArea previousElement, AlignmentArea currentElement) {
		removeListenersFromAlignmentArea(previousElement);
		adoptToListChanges();
		addListenersToAlignmentArea(currentElement);
		
		super.afterReplace(index, previousElement, currentElement);
	}
}
