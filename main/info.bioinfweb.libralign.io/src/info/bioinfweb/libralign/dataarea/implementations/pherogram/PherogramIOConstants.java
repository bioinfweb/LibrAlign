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
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import javax.xml.namespace.QName;



public interface PherogramIOConstants {
	public static final String NAMESPACE_URI_PREFIX = "http://bioinfweb.info/xmlns/LibrAlign/Pherogram/";
	public static final String PHEROGRAM_ALIGNMENT_NAMESPACE_URI = NAMESPACE_URI_PREFIX + "PherogramAlignment/"; //$NON-NLS-1$
	public static final String PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX = "pha";
	public static final String DATA_TYPE_NAMESPACE_URI = NAMESPACE_URI_PREFIX + "DataTypes/";
	public static final String DATA_TYPE_NAMESPACE_PREFIX = "ldt";
	public static final String PREDICATE_NAMESPACE_URI = NAMESPACE_URI_PREFIX + "Predicates/";
	public static final String PREDICATE_NAMESPACE_PREFIX = "lp";
	
	public static final QName PREDICATE_IS_REVERSE_COMPLEMENTED = new QName(PREDICATE_NAMESPACE_URI, "isRCed", PREDICATE_NAMESPACE_PREFIX); 
    public static final QName PREDICATE_HAS_PHEROGRAM = new QName(PREDICATE_NAMESPACE_URI, "hasPherogram", PREDICATE_NAMESPACE_PREFIX); 
    public static final QName PREDICATE_HAS_PHEROGRAM_ALIGNMENT = new QName(PREDICATE_NAMESPACE_URI, "hasPherogramAlignment", PREDICATE_NAMESPACE_PREFIX); 
    public static final QName PREDICATE_HAS_LEFT_CUT_POSITION = new QName(PREDICATE_NAMESPACE_URI, "hasLeftCutPosition", PREDICATE_NAMESPACE_PREFIX); 
    public static final QName PREDICATE_HAS_RIGHT_CUT_POSITION = new QName(PREDICATE_NAMESPACE_URI, "hasRightCutPosition", PREDICATE_NAMESPACE_PREFIX);
    public static final QName PREDICATE_HAS_FIRST_SEQ_POSITION = new QName(PREDICATE_NAMESPACE_URI, "hasFirstSeqPosition", PREDICATE_NAMESPACE_PREFIX);
	public static final QName DATA_TYPE_SHIFT_LIST = new QName(DATA_TYPE_NAMESPACE_URI, "shifts", DATA_TYPE_NAMESPACE_PREFIX);

	public static final QName TAG_SHIFTS = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shifts", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
	public static final QName TAG_SHIFT = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shift", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
	public static final QName ATTR_POSITION = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "pos", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
	public static final QName ATTR_SHIFT = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shift", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
}
