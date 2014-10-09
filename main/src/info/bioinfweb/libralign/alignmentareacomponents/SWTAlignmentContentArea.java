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
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.AlignmentSubArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreaModel;

import java.awt.geom.Dimension2D;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;



/**
 * The SWT component rendering the top central or bottom part of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTAlignmentContentArea extends Composite implements ToolkitSpecificAlignmentContentArea {
	private AlignmentContentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;

	
	public SWTAlignmentContentArea(Composite parentComposite, int style, AlignmentContentArea independentComponent) {
		super(parentComposite, style);
		this.independentComponent = independentComponent;
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginBottom = 0;
		rowLayout.spacing = 0;
		setLayout(rowLayout);

		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
		reinsertSubelements();
  }

	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public void repaint() {
		redraw();
	}


	public void addDataAreaList(DataAreaList list) {
		int width = 0;
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			width = Math.max(width, dataArea.getLength());
			if (dataArea.isVisible()) {
				Composite composite = dataArea.createSWTWidget(this, SWT.NONE);
				dataArea.assignSize();
				Dimension2D size = dataArea.getSize();
				composite.setLayoutData(new RowData((int)Math.round(size.getWidth()), (int)Math.round(size.getHeight())));
			}
		}
	}
	
	
	public void removeAll() {
		for (Control control : getChildren()) {  // Temporary implementation for removeAll() in Swing
      control.dispose();                     //TODO Implement an alternative that keeps the instances that can be reused.
    }
	}
	
	
	public void assignSize() {
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
	public AlignmentSubArea getAreaByY(int y) {
		Control[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			Rectangle r = children[i].getBounds();
			if (Math2.isBetween(y, r.y, r.y + r.height - 1) && (children[i] instanceof ToolkitComponent)) {
				TICComponent ticComponent = ((ToolkitComponent)children[i]).getIndependentComponent();
				if (ticComponent instanceof AlignmentSubArea) {
					return (AlignmentSubArea)ticComponent;
				}
			}
		}
		return null;
	}	


	@Override
	public void reinsertSubelements() {
		DataAreaModel dataAreaModel = getIndependentComponent().getDataAreas();
    removeAll();

		addDataAreaList(dataAreaModel.getTopAreas());

		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			sequenceAreaMap.get(id).createSWTWidget(this, SWT.NONE);
			sequenceAreaMap.get(id).assignSize();
			addDataAreaList(dataAreaModel.getSequenceAreas(id));
		}

		addDataAreaList(dataAreaModel.getBottomAreas());

    assignSize();
		//layout(true, true);  //TODO Necessary?
	}

	
	@Override
	public SequenceArea getSequenceAreaByID(int sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	@Override
	public int getHeight() {
		return getSize().y;
	}
}
