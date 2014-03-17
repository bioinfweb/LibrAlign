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
package info.bioinfweb.libralign.dataarea;



public class DataAreaLocation {
  private DataAreaListType listType;
  private String sequenceName;


	/**
	 * Creates a new instance of this class specifying locations above or underneath the alignment.
	 * 
	 * @param listType - specifies if list represents data areas displayed above
	 *        or underneath the alignment
	 *        
	 * @throws IllegalArgumentException - if {@link DataAreaListType#SEQUENCE} is specified as {@code listType}
	 */
	public DataAreaLocation(DataAreaListType listType) {
		super();
		if (listType.equals(DataAreaListType.SEQUENCE)) {
			throw new IllegalArgumentException("The type " + DataAreaListType.SEQUENCE + 
					" cannot be used if no sequence name is specified.");
		}
		else {
			this.listType = listType;
			sequenceName = null;
		}
	}

	
	/**
	 * Creates a new instance of this class specifying a location attached to one
	 * sequence of the alignment.
	 * <p>
	 * The list type is automatically set to {@link DataAreaListType#SEQUENCE}.
	 * </p>
	 * 
	 * @param sequenceName - the name of the sequence the contained data areas will be attached to
	 */
	public DataAreaLocation(String sequenceName) {
		super();
		this.listType = DataAreaListType.SEQUENCE;
		this.sequenceName = sequenceName;
	}
	
	
	public String getSequenceName() {
		return sequenceName;
	}


	/**
	 * Can be used to update the sequence name, if a sequence was renamed or data area is moved. 
	 * The list type is automatically set to {@link DataAreaListType#SEQUENCE} if a name which 
	 * is not {@code null} is specified.
	 * 
	 * @param sequenceName - the new name of the associated sequence
	 */
	public void setSequenceName(String sequenceName) {
		if (sequenceName != null) {
			listType = DataAreaListType.SEQUENCE;
		}
		this.sequenceName = sequenceName;
	}


	public DataAreaListType getListType() {
		return listType;
	}


	/**
	 * Changes the list type. If a value different from {@link DataAreaListType#SEQUENCE} is
	 * specified, the sequence name is automatically set to {@code null}.
	 * 
	 * @param listType -  the new list type
	 */
	public void setListType(DataAreaListType listType) {
		this.listType = listType;
		if (listType != DataAreaListType.SEQUENCE) {
			sequenceName = null;
		}
	}
}
