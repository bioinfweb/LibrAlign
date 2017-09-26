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
package info.bioinfweb.libralign.alignmentarea.label;


import java.awt.Dimension;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.tic.TICComponent;



/**
 * All GUI components displaying labels to aligned sequences or data areas must be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public abstract class AlignmentLabelSubArea extends TICComponent {
	private AlignmentLabelArea owner;
	private AlignmentSubArea labeledSubArea;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the component that will contains the returned instance
	 * @param labeledSubArea - the alignment sub area that will be labeled by this instance
	 */
	public AlignmentLabelSubArea(AlignmentLabelArea owner, AlignmentSubArea labeledSubArea) {
		super();
		this.owner = owner;
		this.labeledSubArea = labeledSubArea;
	}


	/**
	 * Returns the owning GUI component displaying all labels.
	 * 
	 * @return always an instance of {@link AlignmentLabelArea}
	 */
	public AlignmentLabelArea getOwner() {
		return owner;
	}


	/**
	 * Returns the alignment sub area that is labeled by this instance.
	 * 
	 * @return an alignment sub area that is part of the same {@link AlignmentArea}
	 */
	public AlignmentSubArea getLabeledArea() {
		return labeledSubArea;
	}
	
	
	/**
	 * Returns the width this component needs to display its label information.
	 * 
	 * @return the width in pixels
	 */
	public abstract int getNeededWidth();
	
	
	@Override
	public Dimension getSize() {
		return new Dimension((int)Math.round(getOwner().getGlobalMaxNeededWidth()), (int)Math.round(getLabeledArea().getHeight()));
		//TODO If a directly drawn AlignmentContentArea is used, a vertical shift between the content and its labels may occur, due to the rounding done here.
		//     A possible solution may be to keep track of the rounding differences and round the following label area heights in a way that there will never be a difference lager than one pixel.
		//     (A directly drawn AlignmentLabelArea would also solve that problem.)
	}
}
