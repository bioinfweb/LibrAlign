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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;

import javax.swing.Scrollable;



/**
 * The Swing component displaying a {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class SwingAlignmentContentArea extends SwingAlignmentRowsArea implements Scrollable, ToolkitSpecificAlignmentContentArea {
  private AlignmentContentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	
	
	public SwingAlignmentContentArea(AlignmentContentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		sequenceAreaMap = new SequenceAreaMap(independentComponent);
		reinsertSubelements();
	}

	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return independentComponent;
	}
	

	@Override
	public SequenceArea getSequenceAreaByID(int sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	@Override
	public void reinsertSubelements() {
		removeAll();
		sequenceAreaMap.updateElements();
		
		addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIDList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			add(sequenceAreaMap.get(id).createSwingComponent());
			addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}
		
		addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
	}
	
	
	@Override
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
	public Iterator<AlignmentSubArea> subAreaIterator() {
		return new AlignmentSubAreaIterator(Arrays.asList(getComponents()).iterator()); 
	}


	@Override
	public AlignmentSubArea getAreaByY(int y) {
		Component child = getComponentAt(0, y);
		if ((child != null) && (child instanceof ToolkitComponent)) {
			TICComponent ticComponent = ((ToolkitComponent)child).getIndependentComponent();
			if (ticComponent instanceof AlignmentSubArea) {
				return (AlignmentSubArea)ticComponent;
			}
		}
		return null;
	}

	
	@Override
	public void assignSequenceAreaSize(int sequenceID) throws IllegalArgumentException {
		AlignmentContentAreaTools.assignSequenceAreaSize(sequenceAreaMap, sequenceID);
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
