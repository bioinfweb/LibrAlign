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
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSWTAlignmentContentArea;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.AbstractSWTComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;



/**
 * Abstract base class for {@link ScrollContainerSWTAlignmentContentArea} and {@link ScrollContainerSWTAlignmentLabelArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public abstract class SWTAlignmentRowsArea<C extends TICComponent> extends AbstractSWTComposite 
		implements ToolkitSpecificAlignmentRowsArea<C> {
	
	/**
	 * Creates a new instance of this class with {@link RowLayout}.
	 * 
	 * @param parent - the parent composite
	 * @param style - the SWT component style
	 */
	public SWTAlignmentRowsArea(TICComponent owner, Composite parent, int style) {
		super(owner, parent, style);
		
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
	public void repaint() {
		redraw();
	}
	
	
	public void removeAll() {
		for (Control control : getChildren()) {  // Temporary implementation for removeAll() in Swing
			control.dispose();                     //TODO Implement an alternative that keeps the instances that can be reused.
		}
	}
}
