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


import java.util.function.Consumer;

import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.dataelement.DataLists;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Instances of this class are used to organize the data models that are associated with an {@link AlignmentModel} instance.
 * 
 * @author Ben St&ouml;ver
 * @see 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 *
 * @param <T> the token type used in the associated alignment model
 */
public class DataModelLists extends DataLists<AlignmentModel<?>, DataModel<?>> {
  private final DataList<AlignmentModel<?>, DataModel<?>> alignmentList;
  
  
	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner the {@link AlignmentModel} that uses the new instance
	 * @param listChangeListener This listener will be informed on changes in all lists contained in the new instance. Owning classes should provide an 
	 *        implementation that processes the events, e.g., by forwarding them to their own respective listeners. 
	 */
	public DataModelLists(AlignmentModel<?> owner, ListChangeListener<DataModel<?>> listChangeListener) {
		super(owner, listChangeListener);
		
		alignmentList = new DataList<AlignmentModel<?>, DataModel<?>>(this, DataListType.ALIGNMENT);
		alignmentList.addListChangeListener(listChangeListener);
	}


	/**
	 * Returns the list of data models that are directly associated with the alignment and not a specific sequence.
	 * 
	 * @return a list instance (never {@code null})
	 */
	public DataList<AlignmentModel<?>, DataModel<?>> getAlignmentList() {
		return alignmentList;
	}
	

	@Override
	public void forEach(Consumer<? super DataModel<?>> action) {
		getAlignmentList().forEach(action);
		super.forEach(action);
	}
}
