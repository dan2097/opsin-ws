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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.ggasoftware.indigo.IndigoException;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureConfig;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;

/**
 * 
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OpsinResource extends ServerResource {

	public final static MediaType TYPE_CML = MediaType.register("chemical/x-cml", "Chemical Markup Language");
	public final static MediaType TYPE_INCHI = MediaType.register("chemical/x-inchi", "InChI");
	public final static MediaType TYPE_SMILES = MediaType.register("chemical/x-daylight-smiles", "SMILES");
	
	//These aren't commonly accepted MIME types
	public final static MediaType TYPE_STDINCHIKEY = MediaType.register("chemical/x-stdinchikey", "StdInChIKey");
	public final static MediaType TYPE_NO2DCML = MediaType.register("chemical/x-no2d-cml", "Chemical Markup Language");

	private String name;
	private static NameToStructure n2s;
	private static NameToStructureConfig n2sConfig = new NameToStructureConfig();
	
	static{
		try {
			n2s = NameToStructure.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("OPSIN failed to initialise!");
		}
		n2sConfig.setDetailedFailureAnalysis(true);
		n2sConfig.setAllowRadicals(true);
	}
	
	@Override
	public void doInit() {
		getVariants().clear();
		List<Variant> list = new ArrayList<Variant>();
		list.add(new Variant(TYPE_CML));
		list.add(new Variant(TYPE_INCHI));
		list.add(new Variant(TYPE_SMILES));
		list.add(new Variant(TYPE_STDINCHIKEY));
		list.add(new Variant(TYPE_NO2DCML));
		list.add(new Variant(MediaType.IMAGE_PNG));
		getVariants().addAll(list);
		getAllowedMethods().add(Method.GET);
		getAllowedMethods().add(Method.HEAD);
		
		name = (String) getRequest().getResourceRef().getLastSegment(true);
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			if (TYPE_CML.equals(variant.getMediaType())) {
				return getCmlRepresentation();
			}
			else if (TYPE_INCHI.equals(variant.getMediaType())) {
				return getInchiRepresentation();
			}
			else if (TYPE_SMILES.equals(variant.getMediaType())) {
				return getSmilesRepresentation();
			}
			else if (MediaType.IMAGE_PNG.equals(variant.getMediaType())) {
				return getPngRepresentation();
			}
			else if (TYPE_STDINCHIKEY.equals(variant.getMediaType())) {
				return getInchiKeyRepresentation();
			}
			else if (TYPE_NO2DCML.equals(variant.getMediaType())) {
				return getNo2dCmlRepresentation();
			}
			else{
				throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			}
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	private Representation getCmlRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			try{
				String cml = OpsinResultToCMLWithCoords.convertResultToCMLWithCoords(opsinResult);
				return new StringRepresentation(cml, TYPE_CML);
			}
			catch (Exception e) {
				String cml = opsinResult.getPrettyPrintedCml();
				if (cml != null) {
					return new StringRepresentation(cml, TYPE_CML);
				} else {
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "CML generation failed!");
				}
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getNo2dCmlRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String cml = opsinResult.getPrettyPrintedCml();
			if (cml != null) {
				return new StringRepresentation(cml, TYPE_CML);
			} else {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "CML generation failed!");
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getInchiRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String inchi = NameToInchi.convertResultToInChI(opsinResult);
			if (inchi == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "InChI generation failed!");
			} else {
				return new StringRepresentation(inchi, TYPE_INCHI);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getInchiKeyRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String stdInchiKey = NameToInchi.convertResultToStdInChIKey(opsinResult);
			if (stdInchiKey == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "StdInChIKey generation failed!");
			} else {
				return new StringRepresentation(stdInchiKey, TYPE_STDINCHIKEY);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}

	private Representation getSmilesRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String smiles = opsinResult.getSmiles();
			if (smiles == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "SMILES generation failed!");
			} else {
				return new StringRepresentation(smiles, TYPE_SMILES);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}

	private Representation getPngRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			final byte[] pngBytes;
			try{
				pngBytes = OpsinResultToDepiction.convertResultToDepiction(opsinResult);
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Indigo failed to initialise! Hence an image could not be generated", e);
			}
			catch (IndigoException e) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Image generation failed!");
			}
			if (pngBytes == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Image generation failed!");
			} else {
				return new OutputRepresentation(MediaType.IMAGE_PNG) {
					@Override
					public void write(OutputStream outputStream) throws IOException {
						outputStream.write(pngBytes);
					}
				};
			}

		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
}
