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


import info.bioinfweb.libralign.model.AlignmentModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;



/**
 * Implements basic functionality for writing alignments to streams and files. All methods are delegated to 
 * {@link #doWrite(BufferedOutputStream, AlignmentModel)} and streams are buffered.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public abstract class AbstractAlignmentWriter implements AlignmentWriter {
	protected abstract void doWrite(BufferedOutputStream stream, AlignmentModel<?> provider) throws Exception;
	

	@Override
	public void write(OutputStream stream, AlignmentModel<?> provider) throws Exception {
		if (!(stream instanceof BufferedOutputStream)) {
			stream = new BufferedOutputStream(stream);
		}
		doWrite((BufferedOutputStream)stream, provider);
	}

	
	@Override
	public void write(File file, AlignmentModel<?> provider) throws Exception {
		write(new FileOutputStream(file), provider);
	}
}
