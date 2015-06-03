/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.multiplealignments.AlignmentAreaList;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;



/**
 * Class for internal use with {@link MultipleAlignmentsContainer} to synchronize single paint setting properties
 * between different alignment areas in such a container. Application developers who want to define single paint 
 * settings as global must modify {@link MultipleAlignmentsContainer#getPaintSettingsToSynchronize()} instead of 
 * using this class directly.
 * <p>
 * <b>Warning:</b> This class is for internal use in LibrAlign only and should not be referenced in application code
 * directly. API stability is not guaranteed for this class for any release of LibrAlign (no matter if the major version
 * number is increased or not). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @see PaintSettings
 * @see MultipleAlignmentsContainer#getPaintSettingsToSynchronize()
 */
public class PaintSettingsSynchronizer implements PaintSettingsListener {
	private AlignmentAreaList owner;
	private Set<String> propertiesToSynchronizes = new TreeSet<String>();
	
	
	public PaintSettingsSynchronizer(AlignmentAreaList owner) {
		super();
		this.owner = owner;
	}


	public AlignmentAreaList getOwner() {
		return owner;
	}


	public Set<String> getPropertiesToSynchronizes() {
		return propertiesToSynchronizes;
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (getPropertiesToSynchronizes().contains(event.getPropertyName())) {
			for (AlignmentArea area : getOwner()) {
				if (!event.getSource().equals(area.getPaintSettings())) {
					try {
						BeanUtils.setProperty(area.getPaintSettings(), event.getPropertyName(), event.getNewValue());
					} 
					catch (IllegalAccessException e) {
						throw new InternalError(e.getMessage());
					} 
					catch (InvocationTargetException e) {
						throw new InternalError(e.getMessage());
					}
				}
			}
		}
	}
	
	@Override
	public void tokenPainterReplaced(TokenPainterReplacedEvent event) {}
	
	@Override
	public void tokenPainterListChange(TokenPainterListEvent event) {}
}
