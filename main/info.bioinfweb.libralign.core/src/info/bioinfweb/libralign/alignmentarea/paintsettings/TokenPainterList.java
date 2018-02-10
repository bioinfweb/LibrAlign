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
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.collections.CollectionUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Manages a list of {@link TokenPainter}s used by {@link PaintSettings} and {@link AlignmentArea}. For usual 
 * alignment models this list will contain only one token painter, but if an instance of 
 * {@link ConcatenatedAlignmentModel} is used, it will contain one token painter for each part model.
 * <p>
 * Note that {@link ConcatenatedAlignmentModel} is just a placeholder for future functionality in current 
 * versions of <i>LibrAlign</i>. See its documnentation for details.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class TokenPainterList implements Iterable<TokenPainter> {
	private PaintSettings owner;
	private List<TokenPainter> painters = new ArrayList<TokenPainter>();
	private TokenPainterMap defaultPainters = new TokenPainterMap();
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the paint settings object containing this instance
	 */
	public TokenPainterList(PaintSettings owner) {
		super();
		this.owner = owner;
	}
	
	
	/**
	 * Returns the paint settings object containing this instance.
	 * 
	 * @return the owning paint settings instance
	 */
	public PaintSettings getOwner() {
		return owner;
	}


	/**
	 * Returns the painter at the specified index. If no painter is defined for this index, a default
	 * painter from {@link #getDefaultTokenMap()} is returned that best matches the alignment model
	 * at the specified index. If the index is out of range, a default model will anyway be returned.
	 * 
	 * @param index the index of the painter to be returned (identical with the index of the alignment part 
	 *        model in {@link ConcatenatedAlignmentModel}s) 
	 * @return the best matching painter for the alignment model at the specified position (never {@code null})
	 */
	public TokenPainter get(int index) {
		TokenPainter result = null;
		if (Math2.isBetween(index, 0, painters.size() - 1)) {
			result = painters.get(index);
		}
		
		if (result == null) {
			CharacterStateSetType type = null;
			if (getOwner().getOwner().hasAlignmentModel()) {
				AlignmentModel<?> model = getOwner().getOwner().getAlignmentModel();
				if (model instanceof ConcatenatedAlignmentModel) {
					ConcatenatedAlignmentModel concatenatedModel = (ConcatenatedAlignmentModel)model;
					if (Math2.isBetween(index, 0, concatenatedModel.getPartModelCount() - 1)) {
						type = concatenatedModel.getPartModel(index).getTokenSet().getType();
					}
				}
				if (type == null) {  // Sets the type if the model is not concatenated or if the index was out of range for a concatenated model.  //TODO Check if using the parent token type makes sense, when a concatenated model implementation is available.
					type = model.getTokenSet().getType();
				}
			}
			return getDefaultTokenMap().getPainter(type);  // Specifying null here is valid.
		}
		return result;
	}
	
	
	/**
	 * Returns the according painter to the specified column index. If no painter is found, the default painter will
	 * be returned, even if the specified column does not exist in the associated model.
	 * 
	 * @param columnIndex the index of the column that contains the token to be painted
	 * @return the associated painter or the default painter if no according painter is available
	 */
	public TokenPainter painterByColumn(int columnIndex) {
		if (getOwner().getOwner().hasAlignmentModel()) {
			if (getOwner().getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				ConcatenatedAlignmentModel model = (ConcatenatedAlignmentModel)getOwner().getOwner().getAlignmentModel();
				if (Math2.isBetween(columnIndex, 0, model.getPartModelCount() - 1)) {
					return get(model.partModelIndex(model.partModelByColumn(columnIndex)));  // A default painter would be returned, if none is in the list.
				}
			}
		}
		return get(0);  // Return the first painter of no concatenated model is used or if the index was out of bounds.
	}


	/**
	 * Replaces the painter at the specified position in this list with the specified new painter.
	 * <p>
	 * Painters can also be set to {@code null}. In such cases the alignment area will use its default
	 * painter for the according column ranges.
	 * 
	 * @param index the index of the painter to be replaced
	 * @param painter the new painter to be set
	 * @return the painter previously located at the specified position
	 * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
	 */
	public TokenPainter set(int index, TokenPainter painter) {
		TokenPainter oldValue = painters.get(index);
		if (((painter == null) && (oldValue != null)) || !painter.equals(oldValue)) {  // Second condition also covers oldValue == null
			painters.set(index, painter);
			getOwner().fireTokenPainterReplaced(oldValue, painter, index);
		}
		return oldValue;
	}


	/**
	 * Returns the number of list elements in this instance. (An entry in the list can either be a painter
	 * instance of {@code null}.)
	 * 
	 * @return the length of this list
	 */
	public int size() {
		return painters.size();
	}
	
	
	/**
	 * Returns the first index of the specified token painter in this list.
	 * 
	 * @param painter the painter object to determine the index for
	 * @return the index of the first occurrence or -1 of the painter is not contained in this list
	 */
	public int indexOf(TokenPainter painter) {
		return painters.indexOf(painter);
	}


	/**
	 * Returns an unmodifiable iterator over all painter of this list. ({@code null} elements can occur.)
	 * 
	 * @return an iterator not supporting {@link Iterator#remove()}
	 */
	@Override
	public Iterator<TokenPainter> iterator() {
		return CollectionUtils.unmodifiableIterator(painters.iterator());
	}


	/**
	 * Checks whether this list contains any painters. A list with only {@code null} elements is not empty.
	 * A list with the length of 0 only specifying a default painter is considered empty. 
	 * 
	 * @return {@code true} if the list is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return painters.isEmpty();
	}


	/**
	 * Returns the default token painter map that is used to paint tokens from the alignment model if no 
	 * respective painter is defined in the list. See the documentation of {@link TokenPainterMap} for
	 * further details.
	 * 
	 * @return the token painter map instance used by this i
	 */
	public TokenPainterMap getDefaultTokenMap() {
		return defaultPainters;
	}


	/**
	 * This method is called by the owning alignment area to notify this object about a change of the associated
	 * alignment model. This could either be that a different model was set or that changes inside a concatenated
	 * model happened.
	 * <p>
	 * This method is meant for internal use in <i>LibrAlign</i> and it should not be necessary to call it directly
	 * from external code.
	 */
	public void afterAlignmentModelChanged() {
		if (getOwner().getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("Not yet implemented.");
			//TODO Implement handling concatenated models
			//TODO Implement listening to model events and modify the list accordingly.
		}
		else {
			if (painters.isEmpty()) {  // No model was previously present.
				painters.add(null);
			}
			else {
				if (size() > 1) {  // The previous model was a concatenated model.
					painters.subList(1, size()).clear();  // Remove all token painters except the first one.
				}
			}
		}
		getOwner().fireTokenPainterListChange();
	}
}
