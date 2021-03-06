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


import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.DataAreaLists;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SWTAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.tic.SWTComponentFactory;



/**
 * The SWT component displaying a {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class ScrollContainerSWTAlignmentContentArea extends SWTAlignmentRowsArea<AlignmentSubArea> 
		implements ToolkitSpecificAlignmentContentArea {
	
	public ScrollContainerSWTAlignmentContentArea(AlignmentContentArea independentComponent, Composite parentComposite, int style) {  //TODO Is this constructor found in the factory, if a supertype of TICComponent is used?
		super(independentComponent, parentComposite, style);
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


	public void addDataAreaList(DataList<AlignmentArea, DataArea> list) {
		Iterator<DataArea> iterator = list.iterator();
		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				factory.getSWTComponent(dataArea, this, SWT.NO_BACKGROUND);
				dataArea.assignSize();
			}
		}
	}
	
	
	public void assignSize() {  //TODO The according method from AlignmentContentArea could also be used instead of this method. 
		int width = 0;
		int height = 0;
		for (Control control : getChildren()) {
			Point size = control.getSize();
			control.setLayoutData(new RowData(size));
			width = Math.max(width, size.x);
			height += size.y;
		}
		setSize(width, height);
	}


	@Override
	public void reinsertSubelements() {
		DataAreaLists dataAreas = getIndependentComponent().getOwner().getDataAreas();
		removeAll();

		addDataAreaList(dataAreas.getTopList());

		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		Iterator<String> idIterator = getIndependentComponent().getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			factory.getSWTComponent(getIndependentComponent().getSequenceAreaMap().get(id), this, SWT.NO_BACKGROUND);
			getIndependentComponent().getSequenceAreaMap().get(id).assignSize();
			addDataAreaList(dataAreas.getSequenceList(id));
		}

		addDataAreaList(dataAreas.getBottomList());

		assignSize();
		//layout(true, true);  //TODO Necessary?
	}

	
	@Override
	public void repaintSequences() {
		getIndependentComponent().getSequenceAreaMap().repaintSequenceAreas();
	}
}
