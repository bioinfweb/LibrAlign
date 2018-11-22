package info.bioinfweb.libralign.demo.advancedpherogram.actions;

import info.bioinfweb.libralign.demo.advancedpherogram.Application;
import info.bioinfweb.libralign.pherogram.PherogramFormats;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class ShowHideBasecalllinesAction extends  AbstractAdvancedPherogramAction implements Action{

	 
		public ShowHideBasecalllinesAction (Application app) {
			super(app);
			putValue(Action.NAME, "Show/hide basecall lines"); 
		}
		

		@Override
		public void actionPerformed(ActionEvent e) {
			PherogramFormats f = getApplication().getPherogramDataArea().getFormats(); 
			f.toggleShowBaseCallLines();
		}
	}

