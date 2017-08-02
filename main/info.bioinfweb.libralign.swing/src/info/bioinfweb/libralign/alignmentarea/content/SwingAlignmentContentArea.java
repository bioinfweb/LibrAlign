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


import java.awt.Component;
import java.util.Iterator;

import info.bioinfweb.tic.SwingComponentFactory;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;

import javax.swing.Scrollable;



/**
 * The Swing component displaying a {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class SwingAlignmentContentArea extends SwingAlignmentRowsArea<TICComponent> implements Scrollable, ToolkitSpecificAlignmentContentArea {
	private SequenceAreaMap sequenceAreaMap;
	
	
	public SwingAlignmentContentArea(AlignmentContentArea independentComponent) {
		super(independentComponent);
		sequenceAreaMap = new SequenceAreaMap(independentComponent);
		reinsertSubelements();
	}

	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return (AlignmentContentArea)super.getIndependentComponent();
	}
	

	@Override
	public SequenceArea getSequenceAreaByID(String sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	@Override
	public void reinsertSubelements() {
		removeAll();
		sequenceAreaMap.updateElements();
		
		addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getTopAreas());
		
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		Iterator<String> idIterator = getIndependentComponent().getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			add(factory.getSwingComponent(sequenceAreaMap.get(id).getComponent()));
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
	public AlignmentSubArea getAreaByY(double y) {
		Component child = getComponentAt(0, (int)Math.round(y));
		if ((child != null) && (child instanceof ToolkitComponent)) {
			TICComponent ticComponent = ((ToolkitComponent)child).getIndependentComponent();
			if (ticComponent instanceof AbstractAlignmentSubAreaComponent) {
				return ((AbstractAlignmentSubAreaComponent)ticComponent).getOwner();
			}
		}
		return null;
	}

	
	@Override
	public void assignSequenceAreaSize(String sequenceID) throws IllegalArgumentException {
		AlignmentContentAreaTools.assignSequenceAreaSize(sequenceAreaMap, sequenceID);
	}


	@Override
	public void repaintSequenceAreas() {
		sequenceAreaMap.repaintSequenceAreas();
	}
}
