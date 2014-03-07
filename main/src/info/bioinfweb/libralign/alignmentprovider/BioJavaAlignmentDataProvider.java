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
package info.bioinfweb.libralign.alignmentprovider;


import java.util.Iterator;
import java.util.Map;

import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.template.AbstractSequence;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.exception.AlignmentSourceNotWritableException;



/**
 * Provides alignment source data from BioJava {@link Sequence} objects.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 *
 * @param <C> - the compound type used by the underlying sequence objects.
 */
public class BioJavaAlignmentDataProvider<C extends Compound>
    extends AbstractAlignmentDataProvider implements AlignmentDataProvider {
	
	public static final String DEFAULT_SEQUENCE_NAME_PREFIX = "Sequence";
	
	
	private Alignment<Sequence<C>, C> alignment;
	
	
	public BioJavaAlignmentDataProvider(Alignment<Sequence<C>, C> alignment, AlignmentSourceDataType dataType) {
		super(dataType);
		this.alignment = alignment;
	}

	
	private String createNewSequenceName() {
		int index = 1;
		String name;
		do {
			name = DEFAULT_SEQUENCE_NAME_PREFIX + index;
			index++;
		} while (alignment.containsName(name));
		return name;
	}
	

	public BioJavaAlignmentDataProvider(Profile<Sequence<C>, C> profile, AlignmentSourceDataType dataType) {
		super(dataType);
		
		alignment = new SimpleAlignment<Sequence<C>, C>();
		for (int i = 0; i < profile.getSize(); i++) {
			String name;
			if (profile.getAlignedSequence(i).getOriginalSequence() instanceof AbstractSequence) {
				name = ((AbstractSequence)profile.getAlignedSequence(i).getOriginalSequence()).getOriginalHeader();
				//TODO Is there a more genral way to obtain sequence names from BioJava?
			}
			else {
				name = createNewSequenceName();
			}
			alignment.add(name, profile.getAlignedSequence(i)); 
		}
	}


	public BioJavaAlignmentDataProvider(Map<String, Sequence<C>> map, AlignmentSourceDataType dataType) {
		super(dataType);
		
		alignment = new SimpleAlignment<Sequence<C>, C>();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next(); 
			alignment.add(name, map.get(name));
		}
	}


	@Override
	public Object getTokenAt(String sequenceName, int elementIndex) {
		return alignment.getSequence(sequenceName).getCompoundAt(elementIndex + 1);  // BioJava indices start with 1.
	}


	@Override
	public void setTokenAt(String sequenceName, int elementIndex, Object token)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(String sequenceName, int elementIndex, Object token)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(String sequenceName, int elementIndex)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public int getSequenceCount() {
		return alignment.size();
	}


	@Override
	public Iterator<String> sequenceNameIterator() {
		return alignment.nameIterator();
	}


	@Override
	public int getSequenceLength(String sequenceName) {
		return alignment.getSequence(sequenceName).getLength();
	}


	@Override
	public boolean isReadOnly() {
		return true;
	}
}
