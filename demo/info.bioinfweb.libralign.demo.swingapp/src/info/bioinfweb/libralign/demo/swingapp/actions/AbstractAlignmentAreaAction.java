package info.bioinfweb.libralign.demo.swingapp.actions;


import javax.swing.AbstractAction;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



public abstract class AbstractAlignmentAreaAction extends AbstractAction {
	protected AlignmentArea area;

	
	public AbstractAlignmentAreaAction(AlignmentArea area) {
		super();
		this.area = area;
	}	
}