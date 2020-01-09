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
package info.bioinfweb.libralign.model.undo.alignment;


import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.events.DataModelChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



public class AlignmentModelUndoListener<T> implements AlignmentModelListener<T> {
	//TODO This listener may only record edits when no undo or redo operation is ongoing.
	
	@Override
	public void afterSequenceChange(SequenceChangeEvent<T> event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void afterSequenceRenamed(SequenceRenamedEvent<T> event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void afterTokenChange(TokenChangeEvent<T> event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void afterDataModelChange(DataModelChangeEvent<T> event) {
		// TODO Auto-generated method stub
		
	}
}
