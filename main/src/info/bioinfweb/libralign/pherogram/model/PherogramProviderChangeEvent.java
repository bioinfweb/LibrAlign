/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.pherogram.model;


import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.libralign.pherogram.provider.ReverseComplementPherogramProvider;



/**
 * Change event that indicates the the pherogram provider of an instance of {@link PherogramComponentModel}
 * was replaced or its contents changed. If the contents of the previous provider changes, {@link #getOldProvider()}
 * and {@link #getNewProvider()} would return the same instance.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramProviderChangeEvent extends PherogramComponentModelChangeEvent {
	private PherogramProvider oldProvider;
	private PherogramProvider newProvider;
	private boolean reverseComplemented;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model where the change occurred
	 * @param oldProvider the pherogram provider that was replaced (or edited)
	 * @param newProvider the pherogram provider that replaced the previous one or the same as {@code oldProvider}
	 *        if the contents if provider were changed and the provider instance remains the same
	 * @param reverseComplemented indicates whether this provider change occurred because the model was reverse complemented
	 *        (In that case one of the providers will usually be an instance of {@link ReverseComplementPherogramProvider}.)
	 */
	public PherogramProviderChangeEvent(PherogramComponentModel source, PherogramProvider oldProvider, PherogramProvider newProvider,
			boolean reverseComplemented) {
		
		super(source);
		this.oldProvider = oldProvider;
		this.newProvider = newProvider;
		this.reverseComplemented = reverseComplemented;
	}


	/**
	 * Returns the pherogram provider used before this event occurred.
	 * 
	 * @return the previous pherogram provider of the component model
	 */
	public PherogramProvider getOldProvider() {
		return oldProvider;
	}


	/**
	 * Returns the pherogram provider after before this event occurred.
	 * 
	 * @return the current pherogram provider of the component model
	 */
	public PherogramProvider getNewProvider() {
		return newProvider;
	}
	
	
	/**
	 * Returns if this event occurred due to a reverse complement operation.
	 * 
	 * @return {@code true} if this event occurred due to a reverse complement operation, {@code false} otherwise
	 */
	public boolean isReverseComplemented() {
		return reverseComplemented;
	}


	/**
	 * Tests if this event indicates a substitution or an edit of the pherogram provider instance referenced by {@link #getSource()}.
	 * 
	 * @return {@code true} if the source model now references a different pherogram provider instance than before or
	 *         {@code false} if the referenced instance remains the same, but its contents changed
	 */
	public boolean isProviderInstanceChange() {
		return oldProvider != newProvider;
	}
}
