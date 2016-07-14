/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Iterator;

import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubAreaIterator;
import info.bioinfweb.libralign.alignmentarea.content.SwingAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.SwingAlignmentLabelArea;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.AbstractSwingComponent;

import javax.swing.BoxLayout;
import javax.swing.Scrollable;



/**
 * Abstract base class for {@link SwingAlignmentContentArea} and {@link SwingAlignmentLabelArea}.
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
	public Iterator<C> subAreaIterator() {
		return new AlignmentSubAreaIterator<C>(Arrays.asList(getComponents()).iterator()); 
	}


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return getPreferredSize();
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 10;
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
		// TODO Auto-generated method stub
		return 10;
	}
}
