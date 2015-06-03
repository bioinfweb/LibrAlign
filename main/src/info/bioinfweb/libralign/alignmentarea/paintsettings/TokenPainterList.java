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
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.commons.collections.CollectionUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;



/**
 * Manages a list of {@link TokenPainter}s used by {@link AlignmentArea}. For usual alignment models
 * this list will contain only one token painter, but if an instance of {@link ConcatenatedAlignmentModel}
 * is used, it will contain one token painter for each part model.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class TokenPainterList implements Iterable<TokenPainter> {
	private PaintSettings owner;
	private List<TokenPainter> painters = new ArrayList<TokenPainter>();
	private TokenPainter defaultTokenPainter = new SingleColorTokenPainter();  // Currently all elements would be painted in the default color.
	
	
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
	 * Returns the painter at the specified index.
	 * 
	 * @param index the index of the painter to be returned
	 * @return the painter or {@code null} if no painter was defined at that position
	 * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
	 */
	public TokenPainter get(int index) {
		return painters.get(index);
	}
	
	
	/**
	 * Returns the according painter to the specified column index. If no painter is found, the default painter will
	 * be returned, even if the specified column does not exist in the associated model.
	 * 
	 * @param columnIndex the index of the column that contains the token to be painted
	 * @return the associated painter or the default painter if no according painter is available
	 */
	public TokenPainter painterByColumn(int columnIndex) {
		TokenPainter result = null;
		if (getOwner().getOwner().hasAlignmentModel()) {
			if (getOwner().getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				throw new InternalError("not implemented");
				//TODO Implement returning painters for concatenated model
			}
			else {
				result = get(0);  // In this case this list must have the length 1.
			}
		}
		if (result == null) {
			return getDefaultTokenPainter();
		}
		return result;
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
	 * Returns the default token painter that is used to paint tokens from the alignment model if no according
	 * painter is defined in the list.
	 * 
	 * @return the default token painter instance used by this alignment area
	 */
	public TokenPainter getDefaultTokenPainter() {
		return defaultTokenPainter;
	}


	/**
	 * This method is called by the owning alignment area to notify this object about a change of the associated
	 * alignment model. This could either be that a different model was set or that changes inside a concatenated
	 * model happened.
	 * <p>
	 * This method is meant for internal use in LibrAlign and it should not be necessary to call it directly from
	 * external code.
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
				while (size() > 1) {  // The previous model was a concatenated model.
					painters.remove(size() - 1);
				}
			}
		}
		getOwner().fireTokenPainterListChange();
	}
}
