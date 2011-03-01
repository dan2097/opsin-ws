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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;

public class OPSINResultToCMLWithCoords {

	/**
	 * Converts an OPSIN result to a CML element with coordinates provided by Indigo. An exception is thrown if Indigo fails to initialise
	 * An assumption is made that Indigo will create a molecule with atoms in the same order as in the CML.
	 * @param result
	 * @return
	 * @throws IOException
	 */
	public static Element convertResultToCMLWithCoords(OpsinResult result) throws IOException {
		Element cml = result.getCml();
		if (cml != null){
			Indigo indigo = new Indigo();
			IndigoObject mol = indigo.loadMolecule(cml.toXML());
			mol.layout();
			Elements molecules = cml.getChildElements("molecule", "http://www.xml-cml.org/schema");
			if (molecules.size()!=1){
				throw new RuntimeException("1 molecule element expected");
			}
			Elements atomArrays = molecules.get(0).getChildElements("atomArray", "http://www.xml-cml.org/schema");
			if (atomArrays.size()!=1){
				throw new RuntimeException("1 atomArray element expected");
			}
			Elements atoms = atomArrays.get(0).getChildElements("atom", "http://www.xml-cml.org/schema");
			if (atoms.size() < 1){
				throw new RuntimeException("At least 1 atom element expected");
			}
			int atomCount = mol.countAtoms();
			for (int i = 0; i < atomCount; i++) {
				IndigoObject indigoAtom =mol.getAtom(i);
				Element opsinAtom = atoms.get(i);
				opsinAtom.addAttribute(new Attribute("x2", String.valueOf(indigoAtom.xyz()[0])));
				opsinAtom.addAttribute(new Attribute("y2",  String.valueOf(indigoAtom.xyz()[1])));
			}
		}
		return cml;
	}
}
