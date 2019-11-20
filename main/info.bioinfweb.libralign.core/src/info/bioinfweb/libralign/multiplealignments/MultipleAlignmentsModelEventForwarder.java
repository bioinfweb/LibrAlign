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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * Forwards events from alignment and data models of a single alignment area contained in a 
 * {@link MultipleAlignmentsContainer} to all other alignment areas in that container.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentsModelEventForwarder<T> implements AlignmentModelListener<T> /*, DataAreasListener*/ {
	//TODO This needs to be split into two classes, one for views, one for models. It should also be checked if it makes sense at all to forward both in the same place. (#356)
	private MultipleAlignmentsContainer owner;

	
	public MultipleAlignmentsModelEventForwarder(MultipleAlignmentsContainer owner) {
		super();
		this.owner = owner;
	}


	public MultipleAlignmentsContainer getOwner() {
		return owner;
	}


	@Override
	public void afterSequenceChange(SequenceChangeEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getAlignmentModel())) {
				//alignmentArea.afterSequenceChange(e);
			}
		}
	}


	@Override
	public void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getAlignmentModel())) {
				//alignmentArea.afterSequenceRenamed(e);
			}
		}
	}


	@Override
	public void afterTokenChange(TokenChangeEvent<T> e) {
		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
			if (!e.getSource().equals(alignmentArea.getAlignmentModel())) {
				//alignmentArea.afterTokenChange(e);
			}
		}
	}


	@Override
	public void beforeElementsAdded(ListAddEvent<DataModel<?>> event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterElementsAdded(ListAddEvent<DataModel<?>> event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void beforeElementReplaced(ListReplaceEvent<DataModel<?>> event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterElementReplaced(ListReplaceEvent<DataModel<?>> event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void beforeElementsRemoved(ListRemoveEvent<DataModel<?>, Object> event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterElementsRemoved(ListRemoveEvent<DataModel<?>, DataModel<?>> event) {
		// TODO Auto-generated method stub
		
	}
	
	
//	@Override
//	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
//		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
//			if (!e.getSource().equals(alignmentArea.getDataAreas())) {
//				alignmentArea.dataAreaInsertedRemoved(e);
//			}
//		}
//	}
//
//
//	@Override
//	public void visibilityChanged(DataAreaChangeEvent e) {
//		for (AlignmentArea alignmentArea : getOwner().getAlignmentAreas()) {
//			if (!e.getSource().equals(alignmentArea.getDataAreas())) {
//				alignmentArea.visibilityChanged(e);
//			}
//		}
//	}
}
