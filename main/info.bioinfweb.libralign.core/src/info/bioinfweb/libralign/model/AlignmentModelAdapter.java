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
package info.bioinfweb.libralign.model;


import info.bioinfweb.commons.collections.observable.ListChangeAdapter;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * Provides empty implementations for all listener methods of {@link AlignmentModelListener}.
 * <p>
 * Anonymous listener implementations that only need to overwrite one or a few method can be inherited
 * from this class to reduce the necessary amount of code.
 * 
 * @author Ben St&ouml;ver
 * @since 0.7.0
 */
public class AlignmentModelAdapter extends ListChangeAdapter<DataModel> implements AlignmentModelListener {
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {}

	
	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}

	
	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {}

	
	@Override
	public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {}
}
