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
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSwingAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.ScrollContainerSwingAlignmentLabelArea;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.AbstractSwingComponent;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.Scrollable;



/**
 * Abstract base class for {@link ScrollContainerSwingAlignmentContentArea} and {@link ScrollContainerSwingAlignmentLabelArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public abstract class SwingAlignmentRowsArea<C extends TICComponent> extends AbstractSwingComponent 
		implements Scrollable, ToolkitSpecificAlignmentRowsArea<C> {
	
	public SwingAlignmentRowsArea(TICComponent owner) {
		super(owner);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// reinsertSubelements() cannot be called here, because necessary fields need to be initialized by the implementing class first.
	}


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;  //TODO Implement method to exactly expose five new rows or columns 
	}


	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 10;  //TODO Implement method to exactly expose one new row or column
	}
}
