/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.ch.opsin.ws;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import nu.xom.Element;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import sea36.chem.base.io.CMLBuilder;
import sea36.chem.core.CMLMolecule;
import sea36.chem.graphics.layout.Layout2D;
import sea36.util.restlet.ByteArrayRepresentation;
import sea36.xml.Document;
import sea36.xml.Serializer;
import uk.ac.cam.ch.wwmm.opsin.NameToDepiction;
import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToSmiles;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.XOMFormatter;

/**
 * 
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OPSINResource extends ServerResource {

	public final static MediaType TYPE_CML = MediaType.register("chemical/x-cml", "Chemical Markup Language");
	public final static MediaType TYPE_INCHI = MediaType.register("chemical/x-inchi", "InChI");
	public final static MediaType TYPE_SMILES = MediaType.register("chemical/x-daylight-smiles", "SMILES");

	private String name;
	private static NameToStructure n2s;
	private static NameToInchi n2i;
	private static NameToSmiles n2smi;
	private static NameToDepiction n2depict;
	
	static{
		try {
			n2s = NameToStructure.getInstance();
			n2i = new NameToInchi();
			n2smi = new NameToSmiles();
			n2depict = new NameToDepiction();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("OPSIN failed to intialise!");
		}
	}
	
	@Override
	public void doInit() {
		getVariants().clear();
		List<Variant> list = new ArrayList<Variant>();
		list.add(new Variant(TYPE_CML));
		list.add(new Variant(TYPE_INCHI));
		list.add(new Variant(TYPE_SMILES));
		list.add(new Variant(MediaType.IMAGE_PNG));
		getVariants().put(Method.GET, list);
		
		String name = (String) getRequest().getResourceRef().getRemainingPart();
		if (name.startsWith("/")) {
			name = name.substring(1);
		}
		try {
			name = URLDecoder.decode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Java VM is broken! Must support UTF-8", e);
		}
		this.name = name;
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
			else{
				throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			}
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private Representation getCmlRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, false);
		if (opsinResult.getCml() == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		} else {
			Element cml =opsinResult.getCml();
			try{
				CMLBuilder builder = new CMLBuilder();
				Document doc = builder.parse(cml.toXML());
				CMLMolecule mol = (CMLMolecule) doc.getRootElement().getChild(0);
				Layout2D layout = new Layout2D();
				layout.generateCoordinates(mol);
				ByteArrayOutputStream formattedCML = new ByteArrayOutputStream();
				Serializer serialiser = new Serializer(formattedCML);
				serialiser.writeDocument(doc);
				return new StringRepresentation(formattedCML.toString(), TYPE_CML);
			}
			catch (Exception e) {
				return new StringRepresentation(new XOMFormatter().elemToString(opsinResult.getCml()), TYPE_CML);
			}
		}
	}
	
	private Representation getInchiRepresentation() throws Exception {
		String inchi = n2i.parseToInchi(name, false);
		if (inchi == null) {
			throw new ResourceException(
					Status.CLIENT_ERROR_NOT_FOUND);
		} else {
			return new StringRepresentation(inchi, TYPE_INCHI);
		}
	}

	private Representation getSmilesRepresentation() throws Exception {
		String smiles = n2smi.parseToSmiles(name, false);
		if (smiles == null) {
			throw new ResourceException(
					Status.CLIENT_ERROR_NOT_FOUND);
		} else {
			return new StringRepresentation(smiles, TYPE_SMILES);
		}
	}

	private Representation getPngRepresentation() throws Exception {
		RenderedImage image = n2depict.parseToDepiction(name, false);
		if (image == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.close();
			ByteArrayRepresentation rep = new ByteArrayRepresentation(baos.toByteArray(), MediaType.IMAGE_PNG);
			return rep;
		}
	}
	
	
}
