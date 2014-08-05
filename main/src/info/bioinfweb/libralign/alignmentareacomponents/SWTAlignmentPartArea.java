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


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;

import java.awt.geom.Dimension2D;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
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
public class SWTAlignmentPartArea extends Composite implements ToolkitSpecificAlignmentPartArea {
	public SWTAlignmentPartArea(Composite parent, int style) {
		super(parent, style);
		
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
	}

	
	@Override
	public int getHeight() {
		return getSize().y;
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
}
