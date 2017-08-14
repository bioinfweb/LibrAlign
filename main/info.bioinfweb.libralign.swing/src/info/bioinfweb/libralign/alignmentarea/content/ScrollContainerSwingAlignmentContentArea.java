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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.tic.SwingComponentFactory;
import info.bioinfweb.tic.TICComponent;

import java.util.Iterator;

import javax.swing.Scrollable;



/**
 * The Swing component displaying a {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class ScrollContainerSwingAlignmentContentArea extends SwingAlignmentRowsArea<TICComponent> implements Scrollable, ToolkitSpecificAlignmentContentArea {
	public ScrollContainerSwingAlignmentContentArea(AlignmentContentArea independentComponent) {
		super(independentComponent);
		getIndependentComponent().getSequenceAreaMap().updateElements();  // This needs to be called before reinsertSubelements(). (getIndependentComponent().updateSubelements() cannot be used before this constructor returned a value.)
		reinsertSubelements();
	}

	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return (AlignmentContentArea)super.getIndependentComponent();
	}
	

	@Override
	public void reinsertSubelements() {
		removeAll();

		addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getTopAreas());
		
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		Iterator<String> idIterator = getIndependentComponent().getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			add(factory.getSwingComponent(getIndependentComponent().getSequenceAreaMap().get(id).getComponent()));
			addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getSequenceAreas(id));
		}
		
		addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getBottomAreas());
	}
	
	
	@Override
	public void addDataAreaList(DataAreaList list) {
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				add(factory.getSwingComponent(dataArea.getComponent()));
			}
		}
	}
	

	@Override
	public void assignSequenceAreaSize(String sequenceID) throws IllegalArgumentException {
		AlignmentContentAreaTools.assignSequenceAreaSize(getIndependentComponent().getSequenceAreaMap(), sequenceID);
	}


	@Override
	public void repaintSequenceAreas() {
		getIndependentComponent().getSequenceAreaMap().repaintSequenceAreas();
	}
}
