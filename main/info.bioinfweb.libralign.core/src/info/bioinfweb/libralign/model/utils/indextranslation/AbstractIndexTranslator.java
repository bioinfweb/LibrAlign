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
package info.bioinfweb.libralign.model.utils.indextranslation;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelAdapter;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



public abstract class AbstractIndexTranslator<T, D> implements IndexTranslator<T> {
	private final AlignmentModelListener<T> MODEL_LISTENER = new AlignmentModelAdapter<T>() {
		@Override
		public void afterTokenChange(TokenChangeEvent<T> e) {
			sequenceDataMap.remove(e.getSequenceID());
		}
		
		@Override
		public void afterSequenceChange(SequenceChangeEvent<T> e) {
			if (e.getType().equals(ListChangeType.DELETION) || e.getType().equals(ListChangeType.REPLACEMENT)) {
				sequenceDataMap.remove(e.getSequenceID());
			}
		}
	};
	
	
	private AlignmentModel<T> model;
	private Set<T> gapTokens;
	private Map<String, D> sequenceDataMap = new HashMap<String, D>();
	
	
	public AbstractIndexTranslator(AlignmentModel<T> model, Set<T> gapTokens) {
		super();
		this.model = model;
		this.gapTokens = gapTokens;
		
		model.addModelListener(MODEL_LISTENER);
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator#getModel()
	 */
	@Override
	public AlignmentModel<T> getModel() {
		return model;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator#getGapTokens()
	 */
	@Override
	public Set<T> getGapTokens() {
		return gapTokens;
	}

	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator#getUnalignedIndex(java.lang.String, int)
	 */
	@Override
	public abstract IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex);

	
	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator#getAlignedIndex(java.lang.String, int)
	 */
	@Override
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
		model.removeModelListener(MODEL_LISTENER);  //TODO Allow doing this manually with another method too?
		super.finalize();
	}
}
