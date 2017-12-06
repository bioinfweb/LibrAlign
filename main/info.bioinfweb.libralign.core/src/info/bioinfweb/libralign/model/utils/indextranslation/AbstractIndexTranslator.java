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
package info.bioinfweb.libralign.model.utils.indextranslation;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public abstract class AbstractIndexTranslator<T, D> {
	private final AlignmentModelChangeListener MODEL_LISTENER = new AlignmentModelChangeListener() {
		@Override
		public <T> void afterTokenChange(TokenChangeEvent<T> e) {
			sequenceDataMap.remove(e.getSequenceID());
		}
		
		@Override
		public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}
		
		@Override
		public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
			if (e.getType().equals(ListChangeType.DELETION) || e.getType().equals(ListChangeType.REPLACEMENT)) {
				sequenceDataMap.remove(e.getSequenceID());
			}
		}
		
		@Override
		public <T, U> void afterModelChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}
	};
	
	
	private AlignmentModel<T> model;
	private Set<T> gapTokens;
	private Map<String, D> sequenceDataMap = new HashMap<String, D>();
	
	
	public AbstractIndexTranslator(AlignmentModel<T> model, Set<T> gapTokens) {
		super();
		this.model = model;
		this.gapTokens = gapTokens;
		
		model.getChangeListeners().add(MODEL_LISTENER);
	}
	
	
	/**
	 * Returns the alignment model this instance works on.
	 * 
	 * @return the associated alignment model
	 */
	public AlignmentModel<T> getModel() {
		return model;
	}


	/**
	 * Returns the set of gap tokens used by this instance.  
	 * 
	 * @return a set of tokens to be considered as gaps
	 */
	public Set<T> getGapTokens() {
		return gapTokens;
	}

	
	public abstract IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex);

	
	public abstract int getAlignedIndex(String sequenceID, int unalignedIndex);
	
	
	protected abstract D createSequenceData(String sequenceID);
	
	
	protected D getSequenceData(String sequenceID) {
		D result = sequenceDataMap.get(sequenceID);
		if (result == null) {
			result = createSequenceData(sequenceID);
			sequenceDataMap.put(sequenceID, result);
		}
		return result;
	}


	@Override
	protected void finalize() throws Throwable {
		model.getChangeListeners().remove(MODEL_LISTENER);  //TODO Allow doing this manually with another method too?
		super.finalize();
	}
}
