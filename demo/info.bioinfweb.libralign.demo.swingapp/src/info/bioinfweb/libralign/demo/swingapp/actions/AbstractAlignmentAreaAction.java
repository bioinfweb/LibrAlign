package info.bioinfweb.libralign.demo.swingapp.actions;


import javax.swing.AbstractAction;
import javax.swing.JFrame;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



public abstract class AbstractAlignmentAreaAction extends AbstractAction {
	protected AlignmentArea area;
	protected JFrame frame;

	
	public AbstractAlignmentAreaAction(AlignmentArea area) {
		super();
		this.area = area;
	}	
}