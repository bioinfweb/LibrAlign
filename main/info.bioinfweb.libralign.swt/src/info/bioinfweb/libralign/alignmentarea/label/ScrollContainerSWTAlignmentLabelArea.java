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


import info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SWTAlignmentRowsArea;
import info.bioinfweb.tic.SWTComponentFactory;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;



/**
 * The <i>SWT</i> component displaying the label components for an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class ScrollContainerSWTAlignmentLabelArea extends SWTAlignmentRowsArea<AlignmentLabelSubArea> 
		implements ToolkitSpecificAlignmentLabelArea {
	
	/**
	 * Creates a new instance of this class.
   * <p>
   * Note that this method will only add subelements if the subelements of the associated 
   * {@link ScrollContainerSWTAlignmentContentArea} are already present. Otherwise the component will 
   * be empty after calling this method and you need to call {@link #reinsertSubelements()} again after 
   * the associated alignment content area has been created. 
	 * 
	 * @param parent the parent component
	 * @param style the <i>SWT</i> style value
	 * @param independentComponent the toolkit independent component that uses this instance
	 */
	public ScrollContainerSWTAlignmentLabelArea(AlignmentLabelArea independentComponent, Composite parent, int style) {
		super(independentComponent, parent, style);
		reinsertSubelements();
	}


	@Override
	public AlignmentLabelArea getIndependentComponent() {
		return (AlignmentLabelArea)super.getIndependentComponent();
	}


	/**
   * Recreates the components displaying labels for sequences and data areas in the alignment according to
   * the current model information.
   * <p>
   * Note that this method will only add elements if the subelements of the associated {@link ScrollContainerSWTAlignmentContentArea}
   * are already present. Otherwise the component will be empty after calling this method. 
	 */
	@Override
	public void reinsertSubelements() {
		if (getIndependentComponent().getOwner().getContentArea().hasToolkitComponent()) {
			removeAll();
			SWTComponentFactory factory = SWTComponentFactory.getInstance();
			Iterator<AlignmentLabelSubArea> iterator = getIndependentComponent().subAreaIterator();
			while (iterator.hasNext()) {
				final AlignmentLabelSubArea subArea = iterator.next();
				factory.getSWTComponent(subArea, this, SWT.NONE);
				subArea.assignSize();
			}
			getIndependentComponent().assignSize(); 
			layout(); // Needed to reposition elements if this methods is called again after the construction of the instance (e.g. when a new sequence was added).
		}
	}
}
