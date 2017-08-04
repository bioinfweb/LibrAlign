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
package info.bioinfweb.libralign.alignmentarea.label;


import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;
import info.bioinfweb.tic.SwingComponentFactory;

import java.util.Iterator;



/**
 * The Swing component displaying the label components for an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class ScrollContainerSwingAlignmentLabelArea extends SwingAlignmentRowsArea<AlignmentLabelSubArea> 
		implements ToolkitSpecificAlignmentLabelArea {
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param independentComponent - the toolkit independent component associated with the new instance
	 */
	public ScrollContainerSwingAlignmentLabelArea(AlignmentLabelArea independentComponent) {
		super(independentComponent);
		reinsertSubelements();
	}


	@Override
	public AlignmentLabelArea getIndependentComponent() {
		return (AlignmentLabelArea)super.getIndependentComponent();
	}
	

	@Override
	public void reinsertSubelements() {
		if (getIndependentComponent().getOwner().getContentArea().hasToolkitComponent()) {
			removeAll();
			Iterator<AlignmentLabelSubArea> iterator = subAreaIterator();
			SwingComponentFactory factory = SwingComponentFactory.getInstance();
			while (iterator.hasNext()) {
				add(factory.getSwingComponent(iterator.next()));
			}
		}
		else {
			throw new IllegalStateException(
					"The Swing component of the associated alignment content area must already exist before this method can be called.");
		}
	}
}
