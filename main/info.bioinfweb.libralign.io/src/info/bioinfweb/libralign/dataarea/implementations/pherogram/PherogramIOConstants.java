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
  
  public static final QName TAG_SHIFTS = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shifts", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
  public static final QName TAG_SHIFT = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shift", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
  public static final QName ATTR_POSITION = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "pos", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
  public static final QName ATTR_SHIFT = new QName(PHEROGRAM_ALIGNMENT_NAMESPACE_URI, "shift", PHEROGRAM_ALIGNMENT_NAMESPACE_PREFIX); //$NON-NLS-1$
}
