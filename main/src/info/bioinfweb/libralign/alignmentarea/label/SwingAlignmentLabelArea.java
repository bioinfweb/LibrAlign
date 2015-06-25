/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.label;


import java.util.Iterator;

import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;



/**
 * The Swing component displaying the label components for an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class SwingAlignmentLabelArea extends SwingAlignmentRowsArea<AlignmentLabelSubArea> 
		implements ToolkitSpecificAlignmentLabelArea {
	
	private AlignmentLabelArea independentComponent;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the toolkit independent component associated with the new instance
	 */
	public SwingAlignmentLabelArea(AlignmentLabelArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		reinsertSubelements();
	}


	@Override
	public AlignmentLabelArea getIndependentComponent() {
		return independentComponent;
	}
	

	@Override
	public void reinsertSubelements() {
		if (getIndependentComponent().getOwner().getContentArea().hasToolkitComponent()) {
			removeAll();
			Iterator<AlignmentSubArea> iterator = 
					getIndependentComponent().getOwner().getContentArea().getToolkitComponent().subAreaIterator();
			while (iterator.hasNext()) {
				add(iterator.next().getLabelSubArea().createSwingComponent());
			}
		}
		else {
			throw new IllegalStateException(
					"The Swing component of the associated alignment content area must already exist before this method can be called.");
		}
	}
}
