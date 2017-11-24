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
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColorChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColumnChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;



public class CharSet extends NonOverlappingIntervalList {
	private String name;
	private Color color;
	private Set<CharSetDataModel> models = new HashSet<CharSetDataModel>();
	private boolean isRemoving = false;
	
	
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
				model.fireAfterCharSetRenamed(new CharSetRenamedEvent(model, true, null, this, previousName));  // The ID is unknown here and could only be found out by searching the whole model.
			}
		}
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		if (this.color != color) {
			Color previousColor = this.color;
			this.color = color;
			
			for (CharSetDataModel model : models) {
				model.fireAfterCharSetColorChanged(new CharSetColorChangeEvent(model, true, null, this, previousColor));  // The ID is unknown here and could only be found out by searching the whole model.
			}
		}
	}


	@Override
	public boolean add(int firstPos, int lastPos) {
		boolean result = super.add(firstPos, lastPos);
		
		if (result) {
			for (CharSetDataModel model : models) {
				model.fireAfterCharSetColumnChange(new CharSetColumnChangeEvent(model, true, null, this, true, firstPos, lastPos));  // The ID is unknown here and could only be found out by searching the whole model.
			}
		}
		return result;
	}


	@Override
	public boolean remove(Object o) {
		if (isRemoving) {
			return super.remove(o);
		}
		else {
			isRemoving = true;
			try {
				boolean result = super.remove(o);
				if (result) {
					SimpleSequenceInterval interval = (SimpleSequenceInterval)o;  // If o would have another type, result could not have been true.
					for (CharSetDataModel model : models) {
						model.fireAfterCharSetColumnChange(new CharSetColumnChangeEvent(model, true, null, this, false, interval.getFirstPos(), interval.getLastPos()));  // The ID is unknown here and could only be found out by searching the whole model.
					}
				}
				return result;
			}
			finally {
				isRemoving = false;
			}
		}
	}


	@Override
	public boolean remove(int firstPos, int lastPos) {
		if (isRemoving) {
			return super.remove(firstPos, lastPos);
		}
		else {
			isRemoving = true;
			try {
				boolean result = super.remove(firstPos, lastPos);
				if (result) {
					for (CharSetDataModel model : models) {
						model.fireAfterCharSetColumnChange(new CharSetColumnChangeEvent(model, true, null, this, false, firstPos, lastPos));  // The ID is unknown here and could only be found out by searching the whole model.
					}
				}
				return result;
			}
			finally {
				isRemoving = false;
			}
		}
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
