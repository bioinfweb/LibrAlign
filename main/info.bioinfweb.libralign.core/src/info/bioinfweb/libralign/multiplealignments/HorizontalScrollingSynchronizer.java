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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.scrolling.ScrollingTICComponent;
import info.bioinfweb.tic.scrolling.TICScrollEvent;
import info.bioinfweb.tic.scrolling.TICScrollListener;



/**
 * Synchronizes the horizontal scroll position of {@link AlignmentArea}s that are grouped together in a
 * {@link MultipleAlignmentsContainer}.
 * <p>
 * <b>Warning:</b> This class is used by {@link AlignmentAreaList} within {@link MultipleAlignmentsContainer}. It is 
 * designed for internal use in <i>LibrAlign</i> only and should not be referenced in application code directly. API 
 * stability is not guaranteed for this class for any release of LibrAlign (no matter if the major version number is 
 * increased or not). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class HorizontalScrollingSynchronizer implements TICScrollListener {
	private AlignmentAreaList owner;
	private boolean syncOngoing = false;
	
	
	public HorizontalScrollingSynchronizer(AlignmentAreaList owner) {
		super();
		this.owner = owner;
	}


	public AlignmentAreaList getOwner() {
		return owner;
	}


	@Override
	public void contentScrolled(TICScrollEvent event) {
		if (!syncOngoing) {  // Avoid to recursively react on scroll events triggered by synchronization. 
			syncOngoing = true;
			try {
				ScrollingTICComponent source = event.getSource().getIndependentComponent();
				for (AlignmentArea area : getOwner()) {
					if (source != area) {
						area.setScrollOffsetX(source.getScrollOffsetX());
					}
				}
			}
			finally {
				syncOngoing = false;
			}
		}
	}
}
