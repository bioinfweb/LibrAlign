/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.naming.OperationNotSupportedException;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.label.DefaultLabelSubArea;
import info.bioinfweb.tic.TICComponent;



/**
 * All GUI components that are part of an {@link AlignmentContentArea} should inherit from this class.
 * The method {@link #paintPart(int, Graphics2D, Rectangle)} is responsible for drawing this component.
 * {@link AlignmentArea}s configured to create subcomponents for each sequence and data area will
 * additionally make use of {@link #createComponent()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AlignmentSubArea extends TICComponent {  //TODO Remove inheritance
	private AlignmentLabelSubArea labelSubArea = null;
	private AlignmentContentArea owner = null;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment content area that will contain this instance
	 */
	public AlignmentSubArea(AlignmentContentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment content area that displays this data area.
	 * 
	 * @return the owning alignment content area
	 */
	public AlignmentContentArea getOwner() {
		return owner;
	}
	
	
	/**
	 * This method can be overwritten to provide a specific implementation for labeling the implementing data area.
	 * <p>
	 * This default implementation always returns an instance of {@link DefaultLabelSubArea}.
	 * 
	 * @param owner - the alignment label area that can be set as the owner of the returned component.
	 * @return a new instance of {@link DefaultLabelSubArea} linked to this instance
	 */
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new DefaultLabelSubArea(owner, this);
	}
	
	
	/**
	 * Returns the GUI component that displays the row label for this part of the alignment.
	 * 
	 * @return a GUI component displaying the according label information
	 */
	public AlignmentLabelSubArea getLabelSubArea() {
		if (labelSubArea == null) {
			labelSubArea = createLabelSubArea(getOwner().getOwner().getLabelArea());
		}
		return labelSubArea;
	}
	
	
	/**
	 * Implementations of this method perform the painting of a part of the component. It is used when an
	 * {@link AlignmentArea} is set to contain no subcomponents but to paint its contents directly. This 
	 * default implementation always throws an {@link OperationNotSupportedException} and should be 
	 * overwritten by inherited classes.
	 * <p>
	 * Note that the aim of this method is to allow painting very wide components, which may have a width
	 * larger than {@link Integer#MAX_VALUE}. Therefore painting coordinates (as used in 
	 * {@link AlignmentPaintEvent#getRectangle()} are always relative to the left most pixel of the alignment 
	 * column with the index <i>firstColumn</i>. Implementations should not calculate absolute pixel 
	 * coordinates at any time to avoid integer overflows, since column indices may range from 0 to 
	 * {@link Integer#MAX_VALUE}.   
	 * 
	 * @param event the paint event providing information on the area to be painted, the graphics context
	 *        and tool methods.
	 */
	public void paintPart(AlignmentPaintEvent event) {
		throw new UnsupportedOperationException("This class provides no implementation for direct component painting.");
	}
	
	
	/**
	 * Returns a <i>TIC</i> component displaying the contents of this area. It will only be called if 
	 * {@link AlignmentArea} is set to use separate subcomponents for every sequence and data area.
	 * This default implementation returns a component that delegates painting to 
	 * {@link #paintPart(int, Graphics2D, Rectangle)}, but inherited classes may overwrite it if they
	 * need to return a specialized component (e.g. containing child GUI components).
	 * <p>
	 * Note that {@link AlignmentArea}s set to use subcomponents are not able to handle very long 
	 * sequences. See the documentation of {@link AlignmentArea} for details.
	 * 
	 * @return the <i>TIC</i> component displaying the contents of this area 
	 */
	public TICComponent createComponent() {
		return null;  //TODO Implement by returning a component that delegates to paintPart().
	}
}
