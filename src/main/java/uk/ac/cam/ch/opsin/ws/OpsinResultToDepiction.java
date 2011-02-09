/****************************************************************************
* Copyright (C) 2011 Daniel Lowe
*
* This file is part of the OPSIN Web Service
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* A copy of the GNU General Public License version 3 is included in LICENSE.GPL
***************************************************************************/
package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.IndigoRenderer;

import dan2097.org.bitbucket.indigoloader.IndigoLoader;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OpsinResultToDepiction {
	
	/**
	 * Converts an OPSIN result to a byte array representing a PNG. An exception is thrown if indigo fails to initialise
	 * @param result
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertResultToDepiction(OpsinResult result) throws IOException{
		if (result.getCml() != null){
			Indigo indigo = IndigoLoader.getIndigo();
			IndigoRenderer renderer = new IndigoRenderer(indigo);
			indigo.setOption("render-output-format", "png");
			indigo.setOption("render-coloring", true);
			indigo.setOption("render-stereo-style", "none");
			indigo.setOption("render-bond-length", "35");
			indigo.setOption("render-label-mode", "hideterminal");
			indigo.setOption("render-implicit-hydrogen-mode", "hetero");
			IndigoObject mol = indigo.loadMolecule(result.getSmiles());
			return renderer.renderToBuffer(mol);
		}
		return null;
	}
}
