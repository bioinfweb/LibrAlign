/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.selection;


import info.bioinfweb.commons.events.GenericEventObject;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;



/**
 * Instances of this class allow to synchronize different instances of {@link SelectionModel} if possible. The
 * synchronization can be switched on and off using {@link #setEnabled(boolean)}.
 * <p>
 * Note that synchronizing multiple selection models will usually require that the associated sequence data
 * providers of all selection models contain the same number of columns and rows.
 * <p>
 * <b>Warning:</b> This implementation is not thread save. All registered selection models should only be modified
 * by the same thread. Its recommended to e.g. use {@link EventQueue#invokeLater(Runnable)} in Swing applications or
 * similar methods. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class SelectionSynchronizer {
	private final SelectionListener<GenericEventObject<SelectionModel>> LISTENER = new SelectionListener<GenericEventObject<SelectionModel>>() {
				@Override
				public void selectionChanged(GenericEventObject<SelectionModel> event) {
					if (enabled && !isAdopting) {  // Avoid processing events that have been fired from this method. (Not thread save.)
						isAdopting = true;
						try {
							for (SelectionModel selectionModel : selectionModels) {
								if (!selectionModel.equals(event.getSource())) {
									selectionModel.adoptFromOther(event.getSource());
								}
							}
						}
						finally {
							isAdopting = false;
						}
					}
				}
			};
			
		
	private List<SelectionModel> selectionModels = new ArrayList<SelectionModel>();
	private boolean enabled = true;
	private boolean isAdopting = false;

	
	public boolean add(SelectionModel model) {
		model.addSelectionListener(LISTENER);
		return selectionModels.add(model);
	}

	
	public void clear() {
		selectionModels.clear();
	}


	public boolean contains(Object object) {
		return selectionModels.contains(object);
	}
	

	public boolean isEmpty() {
		return selectionModels.isEmpty();
	}
	

	public boolean remove(SelectionModel model) {
		boolean result = selectionModels.remove(model);
		if (result) {
			model.removeSelectionListener(LISTENER);
		}
		return result;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
