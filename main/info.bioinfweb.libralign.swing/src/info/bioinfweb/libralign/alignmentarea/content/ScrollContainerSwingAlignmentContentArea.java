/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SwingAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.tic.SwingComponentFactory;

import java.util.Iterator;

import javax.swing.Scrollable;



/**
 * The <i>Swing</i> component displaying the contents of an {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class ScrollContainerSwingAlignmentContentArea extends SwingAlignmentRowsArea<AlignmentSubArea> implements Scrollable, ToolkitSpecificAlignmentContentArea {
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
	public boolean hasSubcomponents() {
		return true;
	}


	@Override
	public void reinsertSubelements() {
		removeAll();

		addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getTopList());
		
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		Iterator<String> idIterator = getIndependentComponent().getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			add(factory.getSwingComponent(getIndependentComponent().getSequenceAreaMap().get(id)));
			addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getSequenceList(id));
		}
		
		addDataAreaList(getIndependentComponent().getOwner().getDataAreas().getBottomList());
	}
	
	
	public void addDataAreaList(DataList<AlignmentArea, DataArea> list) {
		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				add(factory.getSwingComponent(dataArea));
			}
		}
	}
	

	@Override
	public void repaintSequences() {
		getIndependentComponent().getSequenceAreaMap().repaintSequenceAreas();
	}
}
