/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.alignmentareacomponents;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.Transient;
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;



/**
 * The Swing component rendering the top central or bottom part of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SwingAlignmentPartArea extends JPanel implements Scrollable, ToolkitSpecificAlignmentPartArea {
	public SwingAlignmentPartArea() {
		super();
		init();
	}
	
	
	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}


	public void addDataAreaList(DataAreaList list) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				add(dataArea.createSwingComponent());
			}
		}
	}
	
	
	@Override
	@Transient
	public Dimension getPreferredSize() {
		//TODO AWT tree lock?;
		Dimension result = new Dimension(0, 0);
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Dimension componentSize = components[i].getPreferredSize();
			result.width = Math.max(result.width, componentSize.width);
			result.height += componentSize.height;
		}
		return result;
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
