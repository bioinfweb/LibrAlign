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


import info.bioinfweb.libralign.alignmentarea.rowsarea.SWTAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreasModel;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.TICComponent;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;



/**
 * The SWT component displaying a {@link AlignmentContentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class ScrollContainerSWTAlignmentContentArea extends SWTAlignmentRowsArea<TICComponent> 
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


	public void addDataAreaList(DataAreaList list) {
		Iterator<DataArea> iterator = list.iterator();
		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				factory.getSWTComponent(dataArea.getComponent(), this, SWT.NO_BACKGROUND);
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
		DataAreasModel dataAreaModel = getIndependentComponent().getOwner().getDataAreas();
		removeAll();

		addDataAreaList(dataAreaModel.getTopAreas());

		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		Iterator<String> idIterator = getIndependentComponent().getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			factory.getSWTComponent(getIndependentComponent().getSequenceAreaMap().get(id).getComponent(), this, SWT.NO_BACKGROUND);
			getIndependentComponent().getSequenceAreaMap().get(id).assignSize();
			addDataAreaList(dataAreaModel.getSequenceAreas(id));
		}

		addDataAreaList(dataAreaModel.getBottomAreas());

		assignSize();
		//layout(true, true);  //TODO Necessary?
	}

	
	@Override
	public void repaintSequences() {
		getIndependentComponent().getSequenceAreaMap().repaintSequenceAreas();
	}
}
