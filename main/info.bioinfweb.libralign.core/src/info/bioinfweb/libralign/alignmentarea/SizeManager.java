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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaVisibleIterator;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.dataelement.DataLists;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * Instances of this class are used by {@link AlignmentArea} to manage and perform size and position operations related to {@link AlignmentSubArea}s and to
 * manage the visibility of {@link DataArea}s. (Positioning depends of course on the visibility of data areas.)
 * <p>
 * This class also provides methods that take information from instances of other {@link AlignmentArea}s contained in the same 
 * {@link MultipleAlignmentsContainer} into account, if such a container is present. 
 * <p>
 * For performance reasons the space possibly required by data areas left and right of the aligned sequences is stored and not recalculated in every access.
 * Data areas and related classes must make sure to inform instances of this class when recalculation becomes necessary by calling 
 * {@link #setLocalMaxLengthBeforeAfterRecalculate()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class SizeManager {  //DataAreaLayoutManager?
	private static interface DataAreaCalculator {
		public double calculate(double previousResult, DataArea dataArea);
	}
	
	
	private AlignmentArea owner;
  private double localMaxLengthBeforeStart = AlignmentLabelArea.RECALCULATE_VALUE;
  private double localMaxLengthAfterEnd = AlignmentLabelArea.RECALCULATE_VALUE;

	
	public SizeManager(AlignmentArea owner) {
		super();
		
		if (owner == null) {
			throw new IllegalArgumentException("owner must not be null.");
		}
		else {
			this.owner = owner;
		}
	}
	
	
	/**
	 * The owning alignment area that uses the functionality of this instance.
	 * 
	 * @return an instance of {@link AlignmentArea} (never {@code null})
	 */
	public AlignmentArea getOwner() {
		return owner;
	}

	
	private double calculateOverVisibleDataArea(DataList list, DataAreaCalculator calculator) {
    double result = 0;
    Iterator<DataArea> iterator = new DataAreaVisibleIterator(list.iterator());
    while (iterator.hasNext()) {
      result = calculator.calculate(result, iterator.next());
    }
    return result;
	}
	

  /**
   * Returns maximum space left of the alignment start that is needed by any currently visible data area
   * in this list.
   *
   * @return an integer >= 0
   */
  private double getMaxLengthBeforeStartForList(DataList list) {
  	return calculateOverVisibleDataArea(list, (previousResult, dataArea) -> Math.max(previousResult, dataArea.getLengthBeforeStart()));
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
			DataLists dataAreas = getOwner().getDataAreas();
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
  private double getMaxLengthAfterEndForList(DataList list) {
  	return calculateOverVisibleDataArea(list, (previousResult, dataArea) -> Math.max(previousResult, dataArea.getLengthAfterEnd()));
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
				DataLists dataAreas = getOwner().getDataAreas();
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


  /**
	 * Calculates the needed with to label the associated alignment. Note that the actual width of this
	 * component is calculated using {@link #getGlobalMaxNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public double getLocalMaximumNeededAlignmentWidth() {
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
	 * If the owning {@link AlignmentArea} is contained within a {@link MultipleAlignmentsContainer},
	 * this method calculates the maximum length of all sequences in all alignment areas contained in
	 * that container. Otherwise the return value is identical with {@link AlignmentModel#getMaxSequenceLength()}.
	 * <p>
	 * <i>Implementation note:</i> Although the sequence length is technically a model information, it is provided here, since the 
	 * relation between multiple {@link AlignmentModel} instances is only defined on the view level by a 
	 * {@link MultipleAlignmentsContainer}. <i>LibrAlign</i> does not model possible relations between multiple alignment models. 
	 * Application classes may do so. Displaying multiple alignment models together in a {@link MultipleAlignmentsContainer} does 
	 * not necessarily mean, though, that there needs to be a strong relation between them.
	 * 
	 * @return a value >= 0
	 * @see AlignmentModel#getMaxSequenceLength()
	 */
	public int getGlobalMaxSequenceLength() {
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
	 * Returns the maximum needed width to display all alignment columns with the current token painters and the
	 * current zoom factor including the space before and after the alignment possibly occupied by data areas. If the 
	 * owning {@link AlignmentArea} is contained within a {@link MultipleAlignmentsContainer} the returned maximum is
	 * calculated over all contained {@link AlignmentArea} instances.
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
	 * Calculates the sum of the heights of all visible data areas contained in this list.
	 *
	 * @return an integer value greater of equal to zero
	 */
	private double getVisibleHeightForList(DataList list) {
  	return calculateOverVisibleDataArea(list, (previousResult, dataArea) -> previousResult + dataArea.getHeight());
	}
	
	
	/**
	 * Calculates the height of all sequences and all visible data areas together. 
	 * 
	 * @return the height of the alignment displayed by this component in pixels
	 */
	public double getPaintHeight() {
		DataLists dataAreas = getOwner().getDataAreas();
		
		// Height of top and bottom data areas:
  	double result = getVisibleHeightForList(dataAreas.getTopAreas()) + getVisibleHeightForList(dataAreas.getBottomAreas());
  	
		if (getOwner().hasAlignmentModel()) {
			AlignmentModel<?> model = getOwner().getAlignmentModel();
			
			// Height of sequence data areas:
			Iterator<String> iterator = model.sequenceIDIterator();
			while (iterator.hasNext()) {
				result += getVisibleHeightForList(dataAreas.getSequenceAreas(iterator.next()));
			}

			// Height of sequences:
			result += model.getSequenceCount() * getOwner().getPaintSettings().getTokenHeight();
		}
		return result;
	}	
}
