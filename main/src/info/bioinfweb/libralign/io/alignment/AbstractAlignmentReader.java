/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.io.alignment;


import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;



/**
 * Implements basic functionality for reading alignments from streams or files. All methods are delegated 
 * to {@link #doRead(BufferedInputStream, AlignmentModel)} and streams are buffered.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public abstract class AbstractAlignmentReader implements AlignmentReader { 
	protected abstract void doRead(BufferedInputStream stream, AlignmentModel<?> provider) throws Exception;

	
	@Override
	public void read(InputStream stream, AlignmentModel<?> provider) throws Exception {
		if (!(stream instanceof BufferedInputStream)) {
			stream = new BufferedInputStream(stream);
		}
		doRead((BufferedInputStream)stream, provider);
	}


	@Override
	public void read(File file, AlignmentModel<?> provider)	throws Exception {
		read(new FileInputStream(file), provider);
	}
}
