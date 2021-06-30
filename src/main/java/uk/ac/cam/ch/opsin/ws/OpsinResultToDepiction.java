package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import com.epam.indigo.IndigoRenderer;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OpsinResultToDepiction {
	
	/**
	 * Converts an OPSIN result to a byte array representing a PNG. An exception is thrown if indigo fails to initialise
	 * @param result
	 * @param imageFormat 
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertResultToDepiction(OpsinResult result, String imageFormat) throws IOException {
		String smiles = result.getSmiles();
		if (smiles != null){
			Indigo indigo = new Indigo();
			IndigoRenderer renderer = new IndigoRenderer(indigo);
			indigo.setOption("render-output-format", imageFormat);
			indigo.setOption("render-background-color", "1, 1, 1");
			indigo.setOption("render-coloring", true);
			indigo.setOption("render-stereo-style", "none");
			indigo.setOption("render-bond-length", "35");
			indigo.setOption("render-label-mode", "hetero");
			IndigoObject mol = indigo.loadMolecule(smiles);
			return renderer.renderToBuffer(mol);
		}
		return null;
	}
}
