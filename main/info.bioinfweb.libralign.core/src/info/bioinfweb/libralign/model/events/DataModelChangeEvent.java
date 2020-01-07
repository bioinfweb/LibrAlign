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
package info.bioinfweb.libralign.model.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * This event indicates that a data models have been added to or removed from an alignment model.
 * <p>
 * Note that this event does not allow the type {@link ListChangeType#REPLACEMENT}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 *
 * @param <T> the token type of the alignment model that is the source of this event.
 */
public class DataModelChangeEvent<T> extends SequenceChangeEvent<T> {
	private DataModel<?> dataModel;
	private DataList<AlignmentModel<?>, DataModel<?>> dataList;

	
	public DataModelChangeEvent(AlignmentModel<T> source, String sequenceID, ListChangeType type, 
			DataModel<?> dataModel, DataList<AlignmentModel<?>, DataModel<?>> dataList) {
		
		super(source, sequenceID, type);
		if (ListChangeType.INSERTION.equals(getType()) || ListChangeType.DELETION.equals(getType())) {
			this.dataModel = dataModel;
			this.dataList = dataList;
		}
		else {
			throw new IllegalArgumentException("Only the types " + ListChangeType.INSERTION + " and " + ListChangeType.DELETION + " are allowed for this event.");
		}
	}


	/**
	 * Returns the data model that was added, removed.
	 * 
	 * @return the data model
	 */
	public DataModel<?> getDataModel() {
		return dataModel;
	}


	/**
	 * Returns the data list that contains/contained the affected data model.
	 * <p>
	 * {@link DataList#getLocation()} can be used to determine the location of the data model.
	 * 
	 * @return the data list (either for the whole alignment or for a sequence)
	 */
	public DataList<AlignmentModel<?>, DataModel<?>> getDataList() {
		return dataList;
	}


	@Override
	public DataModelChangeEvent<T> cloneWithNewSource(AlignmentModel<T> source) {
		return (DataModelChangeEvent<T>)super.cloneWithNewSource(source);
	}
}
