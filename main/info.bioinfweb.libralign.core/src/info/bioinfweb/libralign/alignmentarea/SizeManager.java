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
package info.bioinfweb.libralign.alignmentarea;


import java.util.Iterator;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreasModel;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



public class SizeManager {
	private AlignmentArea owner;
  private double localMaxLengthBeforeStart = AlignmentLabelArea.RECALCULATE_VALUE;
  private double localMaxLengthAfterEnd = AlignmentLabelArea.RECALCULATE_VALUE;

	
	public SizeManager(AlignmentArea owner) {
		super();
		this.owner = owner;
	}
	
	
	public AlignmentArea getOwner() {
		return owner;
	}

	
  /**
   * Returns maximum space left of the alignment start that is needed by any currently visible data area
   * in this list.
   *
   * @return an integer >= 0
   */
  private double getMaxLengthBeforeStartForList(DataAreaList list) {
    double result = 0;
    Iterator<DataArea> iterator = list.visibleIterator();
    while (iterator.hasNext()) {
      result = Math.max(result, iterator.next().getLengthBeforeStart());
    }
    return result;
  }


	/**
	 * Returns maximum space left of the alignment start that is needed by any currently visible data area in
	 * any list contained in this model. Note the space that is actually present will be determined using
	 * {@link #getGlobalMaxLengthBeforeStart()}.
	 *
	 * @return an integer >= 0
	 */
	private double getLocalMaxLengthBeforeStart() {
		if (localMaxLengthBeforeStart == AlignmentLabelArea.RECALCULATE_VALUE) {
			DataAreasModel dataAreas = getOwner().getDataAreas();
			localMaxLengthBeforeStart = Math.max((int)Math2.roundUp(getOwner().getPaintSettings().getCursorLineWidth() / 2),
					Math.max(getMaxLengthBeforeStartForList(dataAreas.getTopAreas()), getMaxLengthBeforeStartForList(dataAreas.getBottomAreas())));
			
			Iterator<String> iterator = getOwner().getAlignmentModel().sequenceIDIterator();
			while (iterator.hasNext()) {
				localMaxLengthBeforeStart = Math.max(localMaxLengthBeforeStart, getMaxLengthBeforeStartForList(getOwner().getDataAreas().getSequenceAreas(iterator.next())));
			}
		}
		return localMaxLengthBeforeStart;
	}


	/**
	 * Returns maximum space left of the alignment calculated over all alignment areas in the parent
	 * {@link MultipleAlignmentsContainer} of the alignment area using this model. If that alignment area
	 * is not contained in such a container the return value is equivalent to {@link #getLocalMaxLengthBeforeStart()}.
	 *
	 * @return an integer >= 0
	 */
	public double getGlobalMaxLengthBeforeStart() {
		double result = 0;
		if (getOwner().hasContainer()) {
			for (AlignmentArea area : getOwner().getContainer().getAlignmentAreas()) {
				result = Math.max(result, area.getSizeManager().getLocalMaxLengthBeforeStart());
			}
		}
		else {
			result = getLocalMaxLengthBeforeStart();
		}
		return result;
	}


  /**
   * Returns maximum space right of the alignment end that is needed by any currently visible data area
   * in this list.
   *
   * @return an integer >= 0
   */
  private double getMaxLengthAfterEndForList(DataAreaList list) {
    double result = 0;
    Iterator<DataArea> iterator = list.visibleIterator();
    while (iterator.hasNext()) {
      result = Math.max(result, iterator.next().getLengthAfterEnd());
    }
    return result;
  }

  
  /**
   * Returns maximum space right of the alignment end that is needed by any currently visible data area in
   * any list contained in this model.
   *
   * @return an integer >= 0
   */
  private double getLocalMaxLengthAfterEnd() {
  	if (getOwner().hasAlignmentModel()) {
	    if (localMaxLengthAfterEnd == AlignmentLabelArea.RECALCULATE_VALUE) {
				DataAreasModel dataAreas = getOwner().getDataAreas();
	      localMaxLengthAfterEnd = Math.max((int)Math2.roundUp(getOwner().getPaintSettings().getCursorLineWidth() / 2),
	      		Math.max(getMaxLengthAfterEndForList(dataAreas.getTopAreas()), getMaxLengthAfterEndForList(dataAreas.getBottomAreas())));
	      
	      Iterator<String> iterator = getOwner().getAlignmentModel().sequenceIDIterator();
	      while (iterator.hasNext()) {
	        localMaxLengthAfterEnd = Math.max(localMaxLengthAfterEnd, getMaxLengthAfterEndForList(dataAreas.getSequenceAreas(iterator.next())));
	      }
	    }
	    return localMaxLengthAfterEnd;
  	}
  	else {
  		return 0;
  	}
  }


	/**
	 * Flags the properties {@link #getLocalMaxLengthBeforeStart()} and {@link #getLocalMaxLengthAfterEnd()} to
	 * be recalculated when they are accessed the next time.
	 * <p>
	 * <i>LibrAlign</i> does not recalculate these values on every call for performance reasons. Usually application code
	 * will not have to call this method directly.
	 */
	public void setLocalMaxLengthBeforeAfterRecalculate() {
		localMaxLengthBeforeStart = AlignmentLabelArea.RECALCULATE_VALUE;
    localMaxLengthAfterEnd = AlignmentLabelArea.RECALCULATE_VALUE;
	}


  // From AlignmentArea:
  
  /**
	 * Calculates the needed with to label the associated alignment. Note that the actual width of this
	 * component is calculated using {@link #getGlobalMaxNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public double getLocalMaximumNeededAlignmentWidth() {  //TODO Should this really be implemented here or should it remain in AlignmentArea?
		if (getOwner().hasAlignmentModel()) {
			if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
				throw new InternalError("not implemented");
			}
			else {
				int length = getOwner().getAlignmentModel().getMaxSequenceLength(); 
				if (length > 0) {
					return length * getOwner().getPaintSettings().getTokenWidth(0);
				}
			}
		}
		return 0;
	}
	
	
	/**
	 * If this area is part of an alignment area that is contained in a {@link MultipleAlignmentsContainer}
	 * than this methods calculates the maximum length of all sequences in all alignment areas contained in
	 * this container. Otherwise the return value is identical with {@code getSequenceProvider.getMaxSequenceLength()}.
	 * 
	 * @return a value >= 0
	 * @see AlignmentModel#getMaxSequenceLength()
	 */
	public int getGlobalMaxSequenceLength() {  //TODO Should this really be implemented here or should it remain in AlignmentArea?
		if (getOwner().hasContainer()) {
			int result = 0;
			for (AlignmentArea alignmentArea : getOwner().getContainer().getAlignmentAreas()) {
				AlignmentModel<?> model = alignmentArea.getAlignmentModel();
				if (model != null) {
					result = Math.max(result, model.getMaxSequenceLength());
				}
			}
			return result;
		}
		else {
			return getOwner().getAlignmentModel().getMaxSequenceLength();
		}
	}
	
	
	private double getLocalMaxNeededWidth() {
		return getGlobalMaxLengthBeforeStart() + getLocalMaximumNeededAlignmentWidth() + getLocalMaxLengthAfterEnd();
	}
	
	
	/**
	 * Returns the maximum needed width to display all alignment columns with the according token painters and the
	 * current zoom factor and the space before and after the alignment possibly occupied by data areas calculated 
	 * over all alignments contained in the parent {@link MultipleAlignmentsContainer}.
	 * 
	 * @return a value >= 0
	 */
	public double getGlobalMaxNeededWidth() {
		double result = 0;
		if (getOwner().hasContainer()) {
			for (AlignmentArea alignmentArea : getOwner().getContainer().getAlignmentAreas()) {
				result = Math.max(result, alignmentArea.getSizeManager().getLocalMaxNeededWidth());
			}
		}
		else {
			result = getLocalMaxNeededWidth();
		}
		return result;
	}
	
	
	/**
	 * Calculates the height of all sequences and all visible data areas together. 
	 * 
	 * @return the height of the alignment displayed by this component in pixels
	 */
	public double getPaintHeight() {  //TODO Should this method be here or still in AlignmentArea?
		double result = getOwner().getDataAreas().getVisibleAreaHeight();
		if (getOwner().hasAlignmentModel()) {
			result += getOwner().getAlignmentModel().getSequenceCount() * getOwner().getPaintSettings().getTokenHeight();
		}
		return result;
	}	
}
