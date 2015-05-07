/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentmodel.AlignmentModelChangeListener;
import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.events.SequenceChangeEvent;
import info.bioinfweb.libralign.alignmentmodel.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.alignmentmodel.events.TokenChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;



/**
 * Delegates events from a single alignment area contained in a {@link MultipleAlignmentsContainer} to
 * all other alignment areas in that container.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentListener implements AlignmentModelChangeListener, DataAreaModelListener {
	private MultipleAlignmentsContainer owner;

	
	public MultipleAlignmentListener(MultipleAlignmentsContainer owner) {
		super();
		this.owner = owner;
	}


	public MultipleAlignmentsContainer getOwner() {
		return owner;
	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getSequenceProvider())) {
				alignmentArea.afterSequenceChange(e);
			}
		}
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getSequenceProvider())) {
				alignmentArea.afterSequenceRenamed(e);
			}
		}
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getSequenceProvider())) {
				alignmentArea.afterTokenChange(e);
			}
		}
	}


	@Override
	public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {
		previous.getChangeListeners().remove(this);
		if (!current.getChangeListeners().contains(this)) {
			current.getChangeListeners().add(this);
		}

		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if ((current != null) && !current.equals(alignmentArea.getSequenceProvider())) {
				alignmentArea.afterModelChanged(previous, current);
			}
		}
	}
	
	
	@Override
	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getDataAreas())) {
				alignmentArea.dataAreaInsertedRemoved(e);
			}
		}
	}


	@Override
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getDataAreas())) {
				alignmentArea.dataAreaVisibilityChanged(e);
			}
		}
	}
}
