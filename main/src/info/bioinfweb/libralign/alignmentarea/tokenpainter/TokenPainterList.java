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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;



/**
 * Manages a list of {@link TokenPainter}s used by {@link AlignmentArea}. For usual alignment models
 * this list will contain only one token painter, but if an instance of {@link ConcatenatedAlignmentModel}
 * is used, it will contain one token painter for each part model. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class TokenPainterList {
	private AlignmentArea owner;
	private List<TokenPainter> painters = new ArrayList<TokenPainter>();
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area using this instance
	 */
	public TokenPainterList(AlignmentArea owner) {
		super();
		this.owner = owner;
	}
	
	
	/**
	 * Returns the alignment area using this instance.
	 * 
	 * @return the owning alignment area instance
	 */
	public AlignmentArea getOwner() {
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
		return painters.set(index, painter);
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
	 * This method is called by the owning alignment area to notify this object about a change of the associated
	 * alignment model. This could either be that a different model was set or that changes inside a concatenated
	 * model happened.
	 * <p>
	 * This method is meant for internal use in LibrAlign and it should not be necessary to call it directly from
	 * external code.
	 */
	public void afterAlignmentModelChanged() {
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
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
	}
}
