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
package info.bioinfweb.libralign.model.factory;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.AbstractUndecoratedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Implements shared functionality for alignment model factories.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type of the alignment models to be created by this instance
 */
public abstract class AbstractAlignmentModelFactory<T> implements AlignmentModelFactory<T> {
	private SequenceIDManager sharedIDManager = null;
	private boolean reuseSequenceIDs = false;
	
	
	/**
	 * Creates a new instance of this class without an shared sequence ID manager. Each model instance,
	 * that will be created by this factory, will have its own sequence ID manager.
	 */
	public AbstractAlignmentModelFactory() {
		super();
	}


	/**
	 * Creates a new instance of this class using a shared sequence ID manager.
	 * 
	 * @param sharedIDManager the sequence ID manager that will be shared by all model instances 
	 *        created by this factory 
	 */
	public AbstractAlignmentModelFactory(SequenceIDManager sharedIDManager, boolean reuseSequenceIDs) {
		super();
		this.sharedIDManager = sharedIDManager;
		this.reuseSequenceIDs = reuseSequenceIDs;
	}


	/**
	 * Returns the sequence ID manager that is shared among all model instances created by this factory.
	 * 
	 * @return the shared ID manager of {@code null} if no shared ID manager is used by this factory
	 * @see #hasSharedIDManager()
	 */
	public SequenceIDManager getSharedIDManager() {
		return sharedIDManager;
	}
	
	
	/**
	 * Determines whether models created by this factory should reuse IDs already present in their associated 
	 * ID manager or should always create new IDs. (See the documentation of 
	 * {@link AbstractUndecoratedAlignmentModel#isReuseSequenceIDs()} for details.)
	 * 
	 * @return {@code true} if IDs should be reused, {@code false} otherwise
	 */
	public boolean isReuseSequenceIDs() {
		return reuseSequenceIDs;
	}


	/**
	 * Determines whether this instance uses a shared sequence ID manager.
	 * 
	 * @return {@code true} if a shared ID manager is present, {@code false} otherwise
	 * @see #getSharedIDManager()
	 */
	public boolean hasSharedIDManager() {
		return getSharedIDManager() != null;
	}


	/**
	 * Creates a token using {@link TokenSet#tokenByRepresentation(String)}.
	 * 
	 * @return the token or {@code null} if no according representation was found in the set
	 * @see info.bioinfweb.libralign.model.factory.AlignmentModelFactory#createToken(info.bioinfweb.libralign.model.AlignmentModel, java.lang.String)
	 */
	@Override
	public T createToken(AlignmentModel<T> alignmentModel, String tokenRepresentation) {
		return alignmentModel.getTokenSet().tokenByRepresentation(tokenRepresentation);
	}


	protected abstract AlignmentModel<T> doCreateNewModel(NewAlignmentModelParameterMap parameterMap);
	
	
	@Override
	public AlignmentModel<T> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		AlignmentModel<T> result = doCreateNewModel(parameterMap);
		if (result != null) {
			result.setLabel(parameterMap.getString(NewAlignmentModelParameterMap.KEY_ALIGNMENT_LABEL));
		}
		return result;
	}
}
