/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.ch.opsin.ws;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureConfig;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.XOMFormatter;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;

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
	
	public final static MediaType TYPE_NO2DCML = MediaType.register("chemical/x-no2d-cml", "Chemical Markup Language");

	private String name;
	private static NameToStructure n2s;
	private static NameToStructureConfig n2sConfig = new NameToStructureConfig();
	
	static{
		try {
			n2s = NameToStructure.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("OPSIN failed to intialise!");
		}
		n2sConfig.setDetailedFailureAnalysis(true);
	}
	
	@Override
	public void doInit() {
		getVariants().clear();
		List<Variant> list = new ArrayList<Variant>();
		list.add(new Variant(TYPE_CML));
		list.add(new Variant(TYPE_INCHI));
		list.add(new Variant(TYPE_SMILES));
		list.add(new Variant(TYPE_NO2DCML));
		list.add(new Variant(MediaType.IMAGE_PNG));
		getVariants().addAll(list);
		getVariants().addAll(list);
		getAllowedMethods().add(Method.GET);
		getAllowedMethods().add(Method.HEAD);
		
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
			else if (TYPE_NO2DCML.equals(variant.getMediaType())) {
				return getNo2dCmlRepresentation();
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
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		System.out.println(opsinResult.getMessage());
		if (opsinResult.getCml() == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		} else {
			Element cml =opsinResult.getCml();
			try{
				IMolecule mol = CMLToCDK.cmlToMolecule(cml);
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				sdg.setMolecule(mol);
				sdg.generateCoordinates();
				mol = sdg.getMolecule();
				Elements molecules = cml.getChildElements("molecule", "http://www.xml-cml.org/schema");
				if (molecules.size()!=1){
					throw new Exception("1 molecule element expected");
				}
				Elements atomArrays = molecules.get(0).getChildElements("atomArray", "http://www.xml-cml.org/schema");
				if (atomArrays.size()!=1){
					throw new Exception("1 atomArray element expected");
				}
				Elements atoms = atomArrays.get(0).getChildElements("atom", "http://www.xml-cml.org/schema");
				if (atoms.size() < 1){
					throw new Exception("At least 1 atom element expected");
				}
				int atomCount = mol.getAtomCount();
				for (int i = 0; i < atomCount; i++) {
					IAtom cdkAtom = mol.getAtom(i);
					Element opsinAtom = atoms.get(i);
					if (!cdkAtom.getID().equals(opsinAtom.getAttributeValue("id"))){
						throw new Exception("Assumption that OPSIN and CDK atoms map to each other has been violated!");
					}
					opsinAtom.addAttribute(new Attribute("x2", String.valueOf(cdkAtom.getPoint2d().x)));
					opsinAtom.addAttribute(new Attribute("y2",  String.valueOf(cdkAtom.getPoint2d().y)));
				}
				return new StringRepresentation(new XOMFormatter().elemToString(cml), TYPE_CML);
			}
			catch (Exception e) {
				return new StringRepresentation(new XOMFormatter().elemToString(cml), TYPE_CML);
			}
		}
	}
	
	private Representation getNo2dCmlRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (opsinResult.getCml() == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		} else {
			Element cml =opsinResult.getCml();
			return new StringRepresentation(new XOMFormatter().elemToString(cml), TYPE_CML);
		}
	}
	
	private Representation getInchiRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String inchi = NameToInchi.convertResultToInChI(opsinResult, false);
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

	private Representation getSmilesRepresentation() throws Exception {
		OpsinResult opsinResult = n2s.parseChemicalName(name, n2sConfig);
		if (!opsinResult.getStatus().equals(OPSIN_RESULT_STATUS.FAILURE)){
			String smiles =OpsinResultToSmiles.convertResultToSMILES(opsinResult, false);
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
			RenderedImage image = OpsinResultToDepiction.convertResultToDepction(opsinResult, false);
			if (image == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Image generation failed!");
			} else {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				baos.close();
				return new OutputRepresentation(MediaType.IMAGE_PNG) {
					
					@Override
					public void write(OutputStream outputStream) throws IOException {
						outputStream.write(baos.toByteArray());
					}
				};
			}
		}
		else{
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, opsinResult.getMessage());
		}
	}
	
	
}
