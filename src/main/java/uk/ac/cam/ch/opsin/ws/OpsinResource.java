package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONStringer;
import org.json.JSONWriter;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.epam.indigo.IndigoException;

import io.github.dan2097.jnainchi.JnaInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureConfig;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import uk.ac.cam.ch.wwmm.opsin.OpsinWarning;

/**
 * 
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OpsinResource extends ServerResource {

	public final static MediaType TYPE_CML = MediaType.register("chemical/x-cml", "Chemical Markup Language");
	public final static MediaType TYPE_INCHI = MediaType.register("chemical/x-inchi", "InChI");
	public final static MediaType TYPE_JSON = MediaType.register("application/json", "JSON");
	public final static MediaType TYPE_SMILES = MediaType.register("chemical/x-daylight-smiles", "SMILES");
	
	//These aren't commonly accepted MIME types
	public final static MediaType TYPE_STDINCHI = MediaType.register("chemical/x-stdinchi", "StdInChI");
	public final static MediaType TYPE_STDINCHIKEY = MediaType.register("chemical/x-stdinchikey", "StdInChIKey");
	public final static MediaType TYPE_NO2DCML = MediaType.register("chemical/x-no2d-cml", "Chemical Markup Language");

	private String name;
	private static NameToStructure n2s;
	private static NameToStructureConfig n2sConfig = new NameToStructureConfig();
	
	static{
		try {
			n2s = NameToStructure.getInstance();
		} catch (Exception e) {
			throw new RuntimeException("OPSIN failed to initialise!", e);
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
		list.add(new Variant(TYPE_JSON));
		list.add(new Variant(TYPE_SMILES));
		list.add(new Variant(TYPE_STDINCHI));
		list.add(new Variant(TYPE_STDINCHIKEY));
		list.add(new Variant(TYPE_NO2DCML));
		list.add(new Variant(MediaType.IMAGE_PNG));
		list.add(new Variant(MediaType.IMAGE_SVG));
		getVariants().addAll(list);
		getAllowedMethods().add(Method.GET);
		getAllowedMethods().add(Method.HEAD);
		
		name = (String) getRequest().getResourceRef().getLastSegment(true);
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			MediaType format = variant.getMediaType();
			if (TYPE_CML.equals(format)) {
				return getCmlRepresentation();
			}
			else if (TYPE_INCHI.equals(format)) {
				return getInchiRepresentation();
			}
			else if (TYPE_JSON.equals(format)) {
				return getJsonRepresentation();
			}
			else if (TYPE_SMILES.equals(format)) {
				return getSmilesRepresentation();
			}
			else if (MediaType.IMAGE_PNG.equals(format)) {
				return getPngRepresentation();
			}
			else if (MediaType.IMAGE_SVG.equals(format)) {
				return getSvgRepresentation();
			}
			else if (TYPE_STDINCHI.equals(format)) {
				return getStdInchiRepresentation();
			}
			else if (TYPE_STDINCHIKEY.equals(format)) {
				return getStdInchiKeyRepresentation();
			}
			else if (TYPE_NO2DCML.equals(format)) {
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
				String cml = OpsinResultToCmlWithCoords.convertResultToCmlWithCoords(opsinResult);
				return new StringRepresentation(cml, MediaType.APPLICATION_XML);
			}
			catch (Exception e) {
				String cml = opsinResult.getPrettyPrintedCml();
				if (cml != null) {
					return new StringRepresentation(cml, MediaType.APPLICATION_XML);
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
				return new StringRepresentation(cml, MediaType.APPLICATION_XML);
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
				return new StringRepresentation(inchi);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getStdInchiRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String stdInchi = NameToInchi.convertResultToStdInChI(opsinResult);
			if (stdInchi == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "StdInChI generation failed!");
			} else {
				return new StringRepresentation(stdInchi);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getStdInchiKeyRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String stdInchiKey = NameToInchi.convertResultToStdInChIKey(opsinResult);
			if (stdInchiKey == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "StdInChIKey generation failed!");
			} else {
				return new StringRepresentation(stdInchiKey);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}

	private Representation getJsonRepresentation() {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		OPSIN_RESULT_STATUS status = opsinResult.getStatus();
		JSONWriter writer = new JSONStringer().object();
		writer.key("status").value(opsinResult.getStatus().toString());
		writer.key("message").value(opsinResult.getMessage());
		if (!status.equals(OPSIN_RESULT_STATUS.FAILURE)){
			String inchi = NameToInchi.convertResultToInChI(opsinResult);
			String stdInchi = NameToInchi.convertResultToStdInChI(opsinResult);
			String stdInchiKey = null;
			if (stdInchi != null){
				try {
					stdInchiKey = JnaInchi.inchiToInchiKey(stdInchi).getInchiKey();
				} catch (Exception e) {}
			}
			String smiles = opsinResult.getSmiles();
			String cml = opsinResult.getPrettyPrintedCml().trim();//Trim is a workaround for a bug in OPSIN-2.8.0

			writer.key("cml").value(cml)
				.key("inchi").value(inchi)
				.key("stdinchi").value(stdInchi)
				.key("stdinchikey").value(stdInchiKey)
				.key("smiles").value(smiles);
				
			List<OpsinWarning> warnings = opsinResult.getWarnings();
			if (!warnings.isEmpty()) {
				writer.key("warnings").array();
				for (OpsinWarning warning : warnings) {
					writer.value(warning.getType().toString());
				}
				writer.endArray();
			}
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		String json = writer.endObject().toString();
		return new StringRepresentation(json, TYPE_JSON);
	}

	private Representation getSmilesRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String smiles = opsinResult.getSmiles();
			if (smiles == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "SMILES generation failed!");
			} else {
				return new StringRepresentation(smiles);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	private Representation getPngRepresentation() {
		return getImageRepresentation(MediaType.IMAGE_PNG, "png");
	}
	
	private Representation getSvgRepresentation() {
		return getImageRepresentation(MediaType.IMAGE_SVG, "svg");
	}

	private Representation getImageRepresentation(MediaType mediaType, String imageFormat) {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			final byte[] imageBytes;
			try{
				imageBytes = OpsinResultToDepiction.convertResultToDepiction(opsinResult, imageFormat);
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Indigo failed to initialise! Hence an image could not be generated", e);
			}
			catch (IndigoException e) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Image generation failed!");
			}
			if (imageBytes == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Image generation failed!");
			} else {
				return new ByteArrayRepresentation(imageBytes, mediaType);
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
}
