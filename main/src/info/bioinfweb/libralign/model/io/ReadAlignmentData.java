/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
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
package info.bioinfweb.libralign.model.io;


import java.util.List;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Contains the alignment model and all data model associated with a single alignment read from a 
 * stream of <i>JPhyloIO</i> events.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the type of sequence elements (tokens) the contained alignment model object works with
 */
public class ReadAlignmentData<T> {
	private AlignmentModel<T> alignmentModel;
	private List<DataModel> dataModels;
	
	
	public ReadAlignmentData(AlignmentModel<T> alignmentModel, List<DataModel> dataModels) {
		super();
		this.alignmentModel = alignmentModel;
		this.dataModels = dataModels;
	}


	public AlignmentModel<T> getAlignmentModel() {
		return alignmentModel;
	}
	
	
	public List<DataModel> getDataModels() {
		return dataModels;
	}
}
