/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.label.DefaultLabelSubArea;



/**
 * All GUI components that are part of an {@link AlignmentContentArea} should inherit from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AlignmentSubArea extends TICComponent {
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
}
