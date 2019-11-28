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
package info.bioinfweb.libralign.multiplealignments;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreasListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelListener;



/**
 * Allows to conveniently listen to events from all {@link AlignmentArea} instances or their associated {@link AlignmentModel} implementations 
 * contained in one instance of {@link MultipleAlignmentsContainer}.
 * <p> 
 * More precisely, alignment model events from alignment models associated with all {@link AlignmentArea} instances contained within the same 
 * {@link MultipleAlignmentsContainer} and data area list events from all such {@link AlignmentArea} instances will be forwarded to respective
 * listeners registered here using {@link #addAlignmentModelListener(MultipleAlignmentsContainer, AlignmentModelListener)} or 
 * {@link #addDataAreasListener(MultipleAlignmentsContainer, DataAreasListener)}. 
 * <p>
 * Registered listener will receive events from all alignment areas in the container and their associated models  at any time. The forwarding 
 * structure will be adjusted when alignment areas in the container are added, removed or replaced and when alignment models associated with 
 * the alignment areas change.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class MultipleAlignmentsEventForwarder {
	private static MultipleAlignmentsEventForwarder firstInstance = null;
	
	private Map<MultipleAlignmentsContainer, SingleMultipleAlignmentsEventForwarder> forwarders = new HashMap<>();
	
	
	private MultipleAlignmentsEventForwarder() {
		super();
	}
	
	
	public static MultipleAlignmentsEventForwarder getInstance() {
		if (firstInstance == null) {
			firstInstance = new MultipleAlignmentsEventForwarder();
		}
		return firstInstance;
	}
	
	
	private SingleMultipleAlignmentsEventForwarder getForwarder(MultipleAlignmentsContainer container) {
		SingleMultipleAlignmentsEventForwarder result = forwarders.get(container);
		if (result == null) {
			result = new SingleMultipleAlignmentsEventForwarder(container);
			forwarders.put(container, result);
		}
		return result;
	}

	
	private boolean removeListener(MultipleAlignmentsContainer container, Consumer<SingleMultipleAlignmentsEventForwarder> consumer) {
		boolean result = false;
		if (forwarders.containsKey(container)) {
			SingleMultipleAlignmentsEventForwarder forwarder = getForwarder(container);
			consumer.accept(forwarder);
			
			if (forwarder.isEmpty()) {  // Make sure not leave unused objects that also keep the associated (possibly disused) GUI components in memory.
				forwarders.remove(container);
				forwarder.unregisterListeners();  // The object does not receive any events from now on.
			}
		}
		return result;
	}

	
	/**
	 * Ensures that the specified listener will receive alignment model events from alignment models associated with all alignment areas within the
	 * specified container.
	 * <p>
	 * Future changes regarding the alignment models associated with the container will be recognized.
	 * 
	 * @param container the container defining which alignment models to listener to
	 * @param listener the listener to receive events from all alignment models
	 * @return {@code true} if the specified listener was not already registered
	 */
	public boolean addAlignmentModelListener(MultipleAlignmentsContainer container, AlignmentModelListener<Object> listener) {
		return getForwarder(container).addAlignmentModelListener(listener);
	}

	
	/**
	 * Removes the specified listener from the list of listeners to receive events from all alignment models associated with the specified container.
	 * <p>
	 * Note that the internal structure of objects used to forward events will be removed for a container as soon as the last listener for it is removed
	 * using this method or {@link #removeDataAreasListener(MultipleAlignmentsContainer, DataAreasListener)}. If listeners should be replaced, it makes 
	 * sense to add the new one before removing the old one to avoid unnecessary creations of new internal objects. 
	 * 
	 * @param container the container defining which alignment models to listener to
	 * @param listener the listener to receive events from all alignment models
	 * @return {@code true} if the listener was previously registered and now removed, {@code false} if it was not registered for {@code container}
	 */
	public boolean removeAlignmentModelListener(MultipleAlignmentsContainer container, AlignmentModelListener<Object> listener) {
		return removeListener(container, forwarder -> forwarder.removeAlignmentModelListener(listener));
	}
	
	
	/**
	 * Ensures that the specified listener will receive data area events from all alignment models contained within the specified container.
	 * <p>
	 * Future changes regarding the alignment areas contained within the container will be recognized.
	 * 
	 * @param container the container defining which alignment models to listener to
	 * @param listener the listener to receive events from all alignment areas
	 * @return {@code true} if the specified listener was not already registered
	 */
	public boolean addDataAreasListener(MultipleAlignmentsContainer container, DataAreasListener listener) {
		return getForwarder(container).addDataAreasListener(listener);
	}

	
	/**
	 * Removes the specified listener from the list of listeners to receive events from all alignment areas associated with the specified container.
	 * <p>
	 * Note that the internal structure of objects used to forward events will be removed for a container as soon as the last listener for it is removed
	 * using this method or {@link #removeAlignmentModelListener(MultipleAlignmentsContainer, AlignmentModelListener)}. If listeners should be replaced, 
	 * it makes sense to add the new one before removing the old one to avoid unnecessary creations of new internal objects. 
	 * 
	 * @param container the container defining which alignment areas to listener to
	 * @param listener the listener to receive events from all alignment areas
	 * @return {@code true} if the listener was previously registered and now removed, {@code false} if it was not registered for {@code container}
	 */
	public boolean removeDataAreasListener(MultipleAlignmentsContainer container, DataAreasListener listener) {
		return removeListener(container, forwarder -> forwarder.removeDataAreasListener(listener));
	}
}
