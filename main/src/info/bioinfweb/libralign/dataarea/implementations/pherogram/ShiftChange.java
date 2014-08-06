package info.bioinfweb.libralign.dataarea.implementations.pherogram;



/**
 * Used by {@link PherogramAlignmentModel} to model which position in the base call sequence corresponds to which
 * position in the editable alignment sequence.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class ShiftChange {
	private int baseCallIndex = 0;
	private int shiftChange = 0;


	public ShiftChange(int baseCallIndex, int shiftChange) {
		super();
		this.baseCallIndex = baseCallIndex;
		this.shiftChange = shiftChange;
	}


	public int getBaseCallIndex() {
		return baseCallIndex;
	}


	public void setBaseCallIndex(int baseCallIndex) {
		this.baseCallIndex = baseCallIndex;
	}


	public int getShiftChange() {
		return shiftChange;
	}


	public void setShiftChange(int shiftChange) {
		this.shiftChange = shiftChange;
	}		
}