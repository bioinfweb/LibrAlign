/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.libralign.alignmentarea.selection.SelectionChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.multiplealignments.ToolkitSpecificMultipleAlignmentsContainer;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;



/**
 * Manages the {@link SequenceArea}s that are contained in an implementation of 
 * {@link ToolkitSpecificMultipleAlignmentsContainer}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceAreaMap extends TreeMap<Integer, SequenceArea> implements SelectionListener {
	private AlignmentContentArea owner;
	private CursorSelectionInputListener selectionInputListener;

	
	public SequenceAreaMap(AlignmentContentArea owner) {
		super();
		this.owner = owner;
		selectionInputListener = new CursorSelectionInputListener(owner.getOwner());
		owner.getOwner().getSelection().addSelectionListener(this);
		updateElements();
	}


	public AlignmentContentArea getOwner() {
		return owner;
	}
	
	
	/**
	 * Removes sequence areas for sequences that are not present anymore in the linked {@link AlignmentModel}
	 * and adds new sequence areas for new sequences in the provider which were not present at the last call of this
	 * method.
	 * 
	 * @since 0.3.0
	 */
	public void updateElements() {
		if (getOwner().getOwner().hasSequenceProvider()) {
			// Backup this map and clear it: (Necessary to remove sequences that are not present in the source anymore.)
			Map<Integer, SequenceArea> saveMap = new TreeMap<Integer, SequenceArea>(this);
			clear();
			
			// (Re)insert sequence areas: 
			Iterator<Integer> iterator = getOwner().getOwner().getAlignmentModel().sequenceIDIterator();
			while (iterator.hasNext()) {
				Integer id = iterator.next();
				SequenceArea sequenceArea = saveMap.get(id);
				if (sequenceArea == null) {
					sequenceArea = new SequenceArea(getOwner(), id);
					sequenceArea.addMouseListener(selectionInputListener);
					sequenceArea.addKeyListener(selectionInputListener);
				}
				else {
					saveMap.remove(id);
				}
				put(id, sequenceArea);
			}
			
			// Unregister listeners from removed sequence areas: (Would probably also work without this.)
			for (Integer id: saveMap.keySet()) {
				SequenceArea sequenceArea = saveMap.get(id);
				sequenceArea.removeMouseListener(selectionInputListener);
				sequenceArea.removeKeyListener(selectionInputListener);
			}
		}
		else {
			clear();
		}
	}
	

	public void repaintSequenceAreas() {
		for (SequenceArea sequenceArea: values()) {
			sequenceArea.repaint();
		}
	}
	
	
	@Override
	public void selectionChanged(SelectionChangeEvent e) {
		repaintSequenceAreas();  // Just repainting the areas in the selection is not enough, because other might have just become deselected.
	}
}
