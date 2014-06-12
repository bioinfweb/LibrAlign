/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.PherogramArea;



/**
 * Classes implementing this interface allow storing data that defines the alignment between a
 * pherogram and the sequence in an alignment area it is associated with.
 * 
 * @author Ben St&ouml;ver
 */
public interface PherogramAlignmentModel { 
  /**
   * This value returned as an index indicates that there is a gap in the associated sequence.
   */
  public static final int GAP = -1;
  
  /**
   * This value returned as an index indicates that the position specified to obtain lies outside
   * the parts of the two sequences that are aligned with each other.
   */
  public static final int OUT_OF_RANGE = -2;
  
	
	/**
	 * Returns the index of the first nucleotide in the base call sequence of the pherogram that
	 * shall be displayed in the associated {@link PherogramArea}.
	 * 
	 * @return a value >= 0
	 */
	public int getFirstVisibleBaseCallIndex();
	
	/**
	 * Sets the index of the first nucleotide in the base call sequence of the pherogram that
	 * shall be displayed in the associated {@link PherogramArea}.
	 * 
	 * @param baseCallIndex - a values inside the bounds of the base call sequence of the pherogram
	 *        (The index of the first nucleotide is 0.)
	 */
	public void setFirstVisibleBaseCallIndex(int baseCallIndex);
	
	/**
	 * Returns the number of nucleotides from the base call sequence of the pherogram that shall be
	 * displayed in the associated {@link PherogramArea} (starting with {@link #getFirstVisibleBaseCallIndex()}).
	 * <p>
	 * Note that gaps in the base call sequence due to the alignment with the associated sequence in the 
	 * {@link AlignmentArea} the pherogram is attached to do not contribute to this length.
	 * 
	 * @return a value >= 0
	 */
	public int getVisibleBaseCallLength();
	
	/**
	 * Sets the number of nucleotides from the base call sequence of the pherogram that shall be
	 * displayed in the associated {@link PherogramArea} (starting with {@link #getFirstVisibleBaseCallIndex()}).
	 * <p>
	 * Note that gaps in the base call sequence due to the alignment with the associated sequence in the 
	 * {@link AlignmentArea} the pherogram is attached to do not contribute to this length.
	 * 
	 * @param length - the new length ({@link #getFirstVisibleBaseCallIndex()}{@code + length} should not
	 *        be greater than the length of the alignment in the associated {@link AlignmentArea}.)
	 */
	public void setVisibleBaseCallLength(int length);
	
  /**
   * Returns the first position in the associated sequence of the alignment the displaying of
   * the visible part if the pherogram starts. 
   * 
   * @return a value >= 0
   */
  public int getSequenceStartIndex();
  
  /**
   * Sets the first position in the associated sequence of the alignment the displaying of
   * the visible part if the pherogram starts.
   * 
   * @param sequenceIndex - the new start index (Must be inside the bounds of the alignment this pherogram
   *        is displayed in. The index of the first position is 0.)
   */
  public void setSequenceStartIndex(int sequenceIndex);
  
//  /**
//   * Returns the position in the associated sequence of the alignment area that is aligned with
//   * the specified position in the base call sequence of the pherogram.
//   * <p>
//   * If the specified base call position is aligned with a gap in the alignment sequence this pherogram
//   * is attached to, {@link #GAP} is returned. If the specified base call position is not contained in the 
//   * visible part of the pherogram (that is displayed in the alignment by the associated 
//   * {@link PherogramArea}), {@link #OUT_OF_RANGE} is returned. 
//   * 
//   * @param baseCallIndex - the absolute index in the base call sequence of the pherogram (Note that the 
//   *        first position of the bas calls associated with the aligned sequence might be greater zero.
//   *        Specifying invisible positions here would return {@link #OUT_OF_RANGE}.) 
//   * @return an integer value {@code >= 0} or {@link #GAP} or {@link #OUT_OF_RANGE}
//   */
//  public int sequenceByBaseCallIndex(int baseCallIndex);
  
  /**
   * Returns the position in the base call sequence of the pherogram.
   * <p>
   * If the specified alignment sequence position is aligned with a gap in the base call sequence, 
   * {@link #GAP} is returned. If the specified position is located outside the interval that is aligned
   * with this pherogram, {@link #OUT_OF_RANGE} is returned.
   * 
   * @param sequenceIndex - the absolute position in the sequence of the {@link AlignmentArea} this pherogram
   *        is attached to (Note that the first position associated with the pherogram might be greater zero.
   *        Specifying invisible positions here would return {@link #OUT_OF_RANGE}.)
   * @return an integer value {@code >= 0} or {@link #GAP} or {@link #OUT_OF_RANGE}
   */
  public int baseCallBySequenceIndex(int sequenceIndex);
  
  /**
   * Stores a new insertion in the alignment sequence (the sequence in the alignment area this pherogram 
   * is attached to) in this model. (This would be equivalent to a new deletion in the base call sequence.)
   * <p>
   * This is necessary if the sequence in the alignment is edited so that it is not identical with the
   * base call sequence anymore. (Such an edit could e.g. be the insertion of a gap character into the
   * alignment sequence.)
   * 
   * @param sequenceStart - the absolute position in the alignment sequence where the insertion did
   *        take place
   * @param length - the length of the insertion
   */
  public void setSequenceInsertion(int sequenceStart, int length);
  
  /**
   * Adds a deletion or removes a stored insertion in the alignment sequence (the sequence in the alignment 
   * area this pherogram is attached to) from this model. (This would be equivalent to adding an insertion
   * the remaining a deletion in the base call sequence.)
   * <p>
   * This is necessary if the sequence in the alignment is edited so that it is not identical with the
   * base call sequence anymore. (Such an edit could e.g. be the deletion of a previously inserted gap 
   * character into the alignment sequence or the removal of an nucleotide initially copied from the base
   * call sequence.)
   * 
   * @param sequenceStart - the absolute position in the alignment sequence where the insertion did
   *        take place
   * @param length - the length of the insertion
   */
  public void setSequenceDeletion(int sequenceStart, int length);
}
