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


import java.beans.PropertyChangeEvent;

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
public abstract class ModelBasedDataArea<M extends DataModel<L>, L> extends DataArea {
	private M model;
	private L modelListener;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner
	 * @param labeledArea
	 * @param model
	 * @throws IllegalArgumentException if {@code owner} is {@code null}
	 */
	public ModelBasedDataArea(AlignmentArea owner, M model) {
		super(owner);		
		setModel(model);
		modelListener = createListener();
	}
	
	
	protected abstract L createListener();

	
	public boolean hasModel() {
		return getModel() != null;
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
	 * Sets a new model and moves the model listener of this instance to the new model.
	 * <p>
	 * This methods fires a {@link PropertyChangeEvent} with the property name {@code model}.
	 * 
	 * @param model the new model to be used from now on
	 */
	public M setModel(M model) {
		M result = this.model;
		
		if (this.model != null) {
			this.model.removeModelListener(modelListener);
		}
		this.model = model;
		if (model != null) {
			model.addModelListener(modelListener);
		}
		
		propertyChangeListeners.firePropertyChange("model", result, model);
		return result;
	}
}
