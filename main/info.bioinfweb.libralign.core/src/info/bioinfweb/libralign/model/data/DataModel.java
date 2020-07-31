/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.data;


import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * The interface that all classes providing data for {@link DataArea} implementations should implement.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
/**
 * @author User
 *
 * @param <L>
 */
public interface DataModel<L> {  //TODO: Add generic parameters M and T to force same model type for getAlignmentModel() and createUndoListener()?
	public AlignmentModel<?> getAlignmentModel();
	
	public boolean addModelListener(L listener);
	
	public boolean removeModelListener(L listener);
	
	/**
	 * Returns a respective undo listener that can monitor instances of this model to be used with {@link EditRecorder}.
	 * 
	 * @param recorder the {@link EditRecorder} to be used with the returned listener
	 * @return a new undo listener instance or {@code null} if this model does not support creating undo listeners
	 */
	public L createUndoListener(EditRecorder<?, ?> recorder); 
	
	/**
	 * Ensures whether an UndoListener already exists. If none exist then it creates one with createUndoListener.
	 * Returns a respective undo listener that can monitor instances of this model to be used with {@link EditRecorder}.
	 * 
	 * @param recorder the {@link EditRecorder} to be used with the returned listener
	 * 
	 */
	default void ensureUndoListener(EditRecorder<?, ?> recorder) throws UnsupportedOperationException {
		L undoListener = createUndoListener(recorder); 
		if (undoListener != null) {
			addModelListener(undoListener);
		}
	}
	
	/**
	 * Removed the respective undo listener.
	 * 
	 */
	public void removeUndoListener(); // TODO: no parameter
	
}
