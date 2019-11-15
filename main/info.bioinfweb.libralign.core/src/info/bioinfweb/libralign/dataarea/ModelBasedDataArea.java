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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * All data area implementations that display data modeled by an implementation of {@link DataModel} should be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 *
 * @param <M> the class of the associated data model
 */
public abstract class ModelBasedDataArea<M extends DataModel<?>> extends DataArea {
	private M model;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner
	 * @param labeledArea
	 * @param model
	 * @throws IllegalArgumentException if {@code owner} or {@code model} is {@code null}
	 */
	public ModelBasedDataArea(AlignmentArea owner, M model) {
		super(owner);		
		setModel(model);
	}


	/**
	 * Returns a reference to the data model that provides the data displayed by this data area.
	 * 
	 * @return the model instance (never {@code null})
	 */
	public M getModel() {
		return model;
	}


	/**
	 * Sets a new model. Inherited class could overwrite this method and make it public if necessary. They need to take care about
	 * firing possibly necessary events or moving possible listeners.
	 * 
	 * @param model the new model to be used from now on
	 */
	public M setModel(M model) {
		if (model == null) {
			throw new IllegalArgumentException("The model must not be null.");
		}
		else {
			M result = this.model;
			this.model = model;
			return result;
		}
	}
}
