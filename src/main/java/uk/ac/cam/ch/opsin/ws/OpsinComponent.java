package uk.ac.cam.ch.opsin.ws;

import org.restlet.Component;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

public class OpsinComponent extends Component {

	public OpsinComponent() {
		OpsinWebApp webapp = new OpsinWebApp();
		this.getDefaultHost().attach(webapp);
		try {
			// initialise OPSIN so there's no delay on first request
			NameToStructure.getInstance();
		} catch (Exception e) {
			throw new RuntimeException("OPSIN failed to intialise!", e);
		}
	}
}
