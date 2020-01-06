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
package info.bioinfweb.libralign.dataarea;


import java.util.List;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Factory interface used by {@link AlignmentArea} to create instances of data areas to represent the contents of data models that are associated 
 * with the alignment model that is displayed by an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 */
public interface DataAreaFactory {
	/**
	 * This class models an element of the {@code result} parameter in {@link DataAreaFactory#createDataAreas(DataModel, String, List)}.
	 * <p>
	 * See there for more information.
	 * 
	 * @author Ben St&ouml;ver
	 * @since 0.10.0
	 */
	public static class DataAreaResult {
		private DataArea dataArea;
		private DataListType position;
		
	
		/**
		 * Creates a new instance of this class with a specific position.
		 * <p>
		 * Note that only {@link DataListType#TOP} and {@link DataListType#BOTTOM} are valid locations for data areas displaying contents of
		 * data models associated with an alignment as a whole. {@link DataListType#SEQUENCE} is the only valid location data reas displaying 
		 * the contents of sequence-associated data models.
		 * 
		 * @param dataArea the new data area instance
		 * @param position the future position of {@code dataArea}
		 * @throws IllegalArgumentException if {@code dataArea} or {@code position} is {@code null} or if {@code position} is {@link DataListType#ALIGNMENT}
		 */
		public DataAreaResult(DataArea dataArea, DataListType position) {
			super();
			
			if (dataArea == null) {
				throw new IllegalArgumentException("dataArea must not be null.");
			}
			else if (position == null) {
				throw new IllegalArgumentException("position must not be null.");
			}
			else if (DataListType.ALIGNMENT.equals(position)) {
				throw new IllegalArgumentException("ALIGNMENT is not a valid position for a data area.");
			}
			else {
				this.dataArea = dataArea;
				this.position = position;
			}
		}


		/**
		 * Convenience constructor for creating result entries for sequence-associated data models.
		 * <p>
		 * The position will be set to {@link DataListType#SEQUENCE}. 
		 * 
		 * @param dataArea the new data area instance
		 * @throws IllegalArgumentException if {@code dataArea} is {@code null}
		 */
		public DataAreaResult(DataArea dataArea) {
			this(dataArea, DataListType.SEQUENCE);
		}


		public DataArea getDataArea() {
			return dataArea;
		}


		public DataListType getPosition() {
			return position;
		}	
	}
	
	
	/**
	 * This method is called by {@link AlignmentArea} to create zero, one or more new data area instance for a single data model instance.
	 * <p>
	 * Implementations of this method will typically add one or more elements to {@code result}. The elements are instances of {@link DataAreaResult}
	 * that allow to specify a data area instance together with its position. Another common use case may also be to associate the specified data 
	 * model with a data area that is already present in the alignment area and add no data area to the result list.
	 * <p>
	 * The position is only required for data models that are associated with an alignment as a whole and not a specific sequence. In that case the 
	 * position specifies whether the respective data area should be displayed above or below the alignment. (Note that the position property cannot 
	 * be used to position data areas below a specific sequence if the respective data model is associated with a whole alignment or vice versa. For 
	 * alignment-associated data models only the positions {@link DataListType#TOP} and {@link DataListType#BOTTOM} are valid, while for 
	 * sequence-associated data models only {@link DataListType#SEQUENCE} is valid. {@link DataListType#ALIGNMENT} may never be used.) 
	 * 
	 * @param model the data model that provides the data for the new data area
	 * @param sequenceID the ID of the sequence the data model is associated with or {@code null} if {@code model} is associated with the alignment 
	 *        as a whole
	 * @param result an empty list that should be filled with zero, one or more data areas that will represent the contents of {@code model}
	 */
	public void createDataAreas(DataModel<?> model, String sequenceID, List<DataAreaResult> result);
	
	
	/**
	 * This method is called by {@link AlignmentArea} when a data model is removed from the associated alignment model for which a respective data area
	 * exists. Implementing classes can decide here, whether the respective data area should be removed as well or if it should remain and its model 
	 * should be set to {@code null}.
	 * <p>
	 * Note that this method may also be called if a sequence is removed from an alignment model including its associated data 
	 * models). In that case, respective data areas will be removed in any case.
	 * 
	 * @param dataArea the data area to be removed or edited
	 * @return {@code true} if the data area should be removed, {@code false} if it should remain and its model should be set to {@code null}
	 */
	public boolean removeDataArea(ModelBasedDataArea<?, ?> dataArea);
}
