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
package info.bioinfweb.libralign.alignmentarea;


import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;



/**
 * Base class for all TIC components which are used in a row layout (e.g. components of the alignment label and
 * content areas). Implements {@link #assignSizeToSWTLayoutData(Point, Composite)} to store a new instance of #
 * {@link RowData} with the according width and height information. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public abstract class RowLayoutTICComponent extends TICComponent {
	public RowLayoutTICComponent() {
		super();
	}

	
	@Override
	public void assignSizeToSWTLayoutData(Point size, Composite composite) {
		composite.setLayoutData(new RowData(size));
	}
}
