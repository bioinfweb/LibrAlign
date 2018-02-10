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
package info.bioinfweb.libralign.model.implementations.decorate;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.adapters.AbstractBasicAlignmentModelView;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;



/**
 * Implementation of {@link AlignmentModelView} that delegates all methods to the underlying model
 * and uses the same token type as the underlying model. Inherited classes can overwrite single 
 * delegated methods to change the behavior.
 * <p>
 * Note that {@link #getChangeListeners()} also delegates to the underlying model and therefore
 * modifying this list, actually modifies the listener list of the underlying model.
 * <p>
 * This class is marked as abstract, since it does not behave different then the underlying model,
 * if no method is overwritten in inherited classes.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the token type of the underlying and the viewed model
 */
public abstract class DelegatedAlignmentModelView<T> extends AbstractBasicAlignmentModelView<T> 
		implements AlignmentModelView<T, T> {
	
	public DelegatedAlignmentModelView(AlignmentModel<T> underlyingModel) {
		super(underlyingModel);
	}
	
	
	public String getID() {
		return getUnderlyingModel().getID();
	}
	
	
	public void setID(String id) {
		getUnderlyingModel().setID(id);
	}
	
	
	public String getLabel() {
		return getUnderlyingModel().getLabel();
	}
	
	
	public void setLabel(String label) throws UnsupportedOperationException {
		getUnderlyingModel().setLabel(label);
	}
	
	
	public TokenSet<T> getTokenSet() {
		return getUnderlyingModel().getTokenSet();
	}
	
	
	public void setTokenSet(TokenSet<T> set)
			throws UnsupportedOperationException, IllegalArgumentException {
		getUnderlyingModel().setTokenSet(set);
	}
	
	
	public int getSequenceLength(String sequenceID) {
		return getUnderlyingModel().getSequenceLength(sequenceID);
	}
	
	
	public int getMaxSequenceLength() {
		return getUnderlyingModel().getMaxSequenceLength();
	}
	
	
	public AlignmentModelWriteType getWriteType() {
		return getUnderlyingModel().getWriteType();
	}
	
	
	public boolean isTokensReadOnly() {
		return getUnderlyingModel().isTokensReadOnly();
	}
	
	
	public boolean isSequencesReadOnly() {
		return getUnderlyingModel().isSequencesReadOnly();
	}
	
	
	public boolean containsSequence(String sequenceID) {
		return getUnderlyingModel().containsSequence(sequenceID);
	}
	
	
	public Set<String> sequenceIDsByName(String sequenceName) {
		return getUnderlyingModel().sequenceIDsByName(sequenceName);
	}
	
	
	public String sequenceNameByID(String sequenceID) {
		return getUnderlyingModel().sequenceNameByID(sequenceID);
	}
	
	
	public String addSequence(String sequenceName)
			throws AlignmentSourceNotWritableException {
		return getUnderlyingModel().addSequence(sequenceName);
	}
	
	
	public String addSequence(String sequenceName, String sequenceID)
			throws AlignmentSourceNotWritableException, IllegalArgumentException {
		return getUnderlyingModel().addSequence(sequenceName, sequenceID);
	}
	
	
	public boolean removeSequence(String sequenceID)
			throws AlignmentSourceNotWritableException {
		return getUnderlyingModel().removeSequence(sequenceID);
	}
	
	
	public String renameSequence(String sequenceID, String newSequenceName)
			throws AlignmentSourceNotWritableException, SequenceNotFoundException {
		return getUnderlyingModel().renameSequence(sequenceID, newSequenceName);
	}
	
	
	public Iterator<String> sequenceIDIterator() {
		return getUnderlyingModel().sequenceIDIterator();
	}
	
	
	public int getSequenceCount() {
		return getUnderlyingModel().getSequenceCount();
	}
	
	
	public Set<AlignmentModelChangeListener> getChangeListeners() {
		return getUnderlyingModel().getChangeListeners();
	}
	
	
	public T getTokenAt(String sequenceID, int index) {
		return getUnderlyingModel().getTokenAt(sequenceID, index);
	}
	
	
	public void setTokenAt(String sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		getUnderlyingModel().setTokenAt(sequenceID, index, token);
	}
	
	
	public void setTokensAt(String sequenceID, int beginIndex,
			Collection<? extends T> tokens)
					throws AlignmentSourceNotWritableException {
		getUnderlyingModel().setTokensAt(sequenceID, beginIndex, tokens);
	}
	
	
	public void appendToken(String sequenceID, T token)
			throws AlignmentSourceNotWritableException {
		getUnderlyingModel().appendToken(sequenceID, token);
	}
	
	
	public void appendTokens(String sequenceID,
			Collection<? extends T> tokens)
					throws AlignmentSourceNotWritableException {
		getUnderlyingModel().appendTokens(sequenceID, tokens);
	}
	
	
	public void insertTokenAt(String sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		getUnderlyingModel().insertTokenAt(sequenceID, index, token);
	}
	
	
	public void insertTokensAt(String sequenceID, int beginIndex,
			Collection<? extends T> tokens)
					throws AlignmentSourceNotWritableException {
		getUnderlyingModel().insertTokensAt(sequenceID, beginIndex, tokens);
	}
	
	
	public void removeTokenAt(String sequenceID, int index)
			throws AlignmentSourceNotWritableException {
		getUnderlyingModel().removeTokenAt(sequenceID, index);
	}
	
	
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		getUnderlyingModel().removeTokensAt(sequenceID, beginIndex, endIndex);
	}
	
}
