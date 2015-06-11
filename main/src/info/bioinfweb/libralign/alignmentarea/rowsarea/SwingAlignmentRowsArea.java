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
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import java.awt.Dimension;
import java.awt.Rectangle;

import info.bioinfweb.libralign.alignmentarea.content.SwingAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.SwingAlignmentLabelArea;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.Scrollable;



/**
 * Abstract base class for {@link SwingAlignmentContentArea} and {@link SwingAlignmentLabelArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public abstract class SwingAlignmentRowsArea extends JComponent implements Scrollable, ToolkitSpecificAlignmentRowsArea {
	public SwingAlignmentRowsArea() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// reinsertSubelements() cannot be called here, because necessary fields need to be initialized by the implementing class first.
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
