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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import info.bioinfweb.commons.collections.NonOverlappingIntervalList;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;



public class CharSet extends NonOverlappingIntervalList {
	private String name;
	private Color color;
	private Set<CharSetDataModel> models = new HashSet<CharSetDataModel>();
	
	
	public CharSet(String name, Color color) {
		super();
		this.name = name;
		this.color = color;
	}

	
	//TODO Which inherited methods need to be overwritten? (Which of the remove() methods.)

	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		if (this.name != name) {
			String previousName = this.name;
			this.name = name;
			
			for (CharSetDataModel model : models) {
				//model.fireAfterCharSetRenamed(new CharSetRenamedEvent(model, true, , this, previousName));
			}
		}
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		//TODO Inform models
		this.color = color;
	}


	/**
	 * Returns a set of model classes that contain this character set.
	 * <p>
	 * This property is only meant for internal use in <i>LibrAlign</i> and should not be used 
	 * in application code directly and is not visible there. It is used to inform model classes
	 * of property changes of an contained character set.
	 * 
	 * @return the set of models containing this instance
	 */
	protected Set<CharSetDataModel> getModels() {
		return models;
	}
}
