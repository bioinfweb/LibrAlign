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



/**
 * Bean class that models the result of an index translation between an aligned and an unaligned sequence.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class IndexRelation {
  /**
   * This value returned as an index indicates that there is a gap in the associated sequence.
   */
  public static final int GAP = -1;
  
  /**
   * This value returned as an index indicates that the position specified to obtain lies outside
   * the parts of the two sequences that are aligned with each other.
   */
  public static final int OUT_OF_RANGE = -2;
  
  
  private int before = OUT_OF_RANGE;	
  private int corresponding = OUT_OF_RANGE;	
  private int after = OUT_OF_RANGE;
  
  
	public IndexRelation(int before, int corresponding, int after) {
		super();
		this.before = before;
		this.corresponding = corresponding;
		this.after = after;
	}


	/**
	 * This value is only needed if {@link #getCorresponding()} returns {@link #GAP} or {@link #OUT_OF_RANGE}. In that case
	 * it returns the first index in the corresponding sequence that is aligned with a position in the original sequence, that
	 * is located before the specified position of the original sequence that produced this result.
	 * 
	 * @return a valid index or {@link #OUT_OF_RANGE} but never {@link #GAP}
	 */
	public int getBefore() {
		return before;
	}
	
	
	/**
	 * Returns the position corresponding to the one specified to obtain this result object. 
	 * 
	 * @return a valid index or {@link #GAP} if the specified index producing this result object is aligned with a 
	 *         gap in the other sequence or {@link #OUT_OF_RANGE} if the specified index lies outside the aligned area 
	 */
	public int getCorresponding() {
		return corresponding;
	}
	
	
	/**
	 * This value is only needed if {@link #getCorresponding()} returns {@link #GAP} or {@link #OUT_OF_RANGE}. In that case
	 * it returns the first index in the corresponding sequence that is aligned with a position in the original sequence, that
	 * is located after the specified position of the original sequence that produced this result.
	 * 
	 * @return a valid index or {@link #OUT_OF_RANGE} but never {@link #GAP}
	 */
	public int getAfter() {
		return after;
	}
	
	
	/**
	 * Returns any index that is not {@link #GAP} or {@link #OUT_OF_RANGE} and first tries {@link #getCorresponding()}
	 * than {@link #getBefore()} and finally {@link #getAfter()}.
	 * 
	 * @return the first valid index that is found in the algorithm above or the same as {@link #getAfter()} if all 
	 *         indices should be invalid
	 */
	public int getBeforeValidIndex() {
		int result = getCorresponding();
		if (result < 0) {
			result = getBefore();
			if (result < 0) {
				result = getAfter();
			}
		}
		return result;
	}
	
	
	/**
	 * Returns any index that is not {@link #GAP} or {@link #OUT_OF_RANGE} and first tries {@link #getCorresponding()}
	 * than {@link #getAfter()} and finally {@link #getBefore()}.
	 * 
	 * @return the first valid index that is found in the algorithm above or the same as {@link #getBefore()} if all 
	 *         indices should be invalid
	 */
	public int getAfterValidIndex() {
		int result = getCorresponding();
		if (result < 0) {
			result = getAfter();
			if (result < 0) {
				result = getBefore();
			}
		}
		return result;
	}
	
	
	public String indexToString(int index) {
		if (index == GAP) {
			return "GAP";
		}
		else if (index == OUT_OF_RANGE) {
			return "OUT_OF_RANGE";
		}
		else {
			return Integer.toString(index);
		}
	}
	
	
	@Override
	public String toString() {
		return "(" + indexToString(getBefore()) + ", " + indexToString(getCorresponding()) + ", " + indexToString(getAfter()) + ")";
	}	
}
