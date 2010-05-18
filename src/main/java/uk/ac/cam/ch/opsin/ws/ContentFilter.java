package uk.ac.cam.ch.opsin.ws;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.routing.Filter;

public class ContentFilter extends Filter {
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		String path = request.getResourceRef().getPath();
		if (path.endsWith(".no2d.cml")) {
			request.getClientInfo().getAcceptedMediaTypes().clear();
			request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(OPSINResource.TYPE_NO2DCML));
			request.getResourceRef().setPath(path.substring(0, path.length()-9));
		}
		else if (path.endsWith(".cml")) {
			request.getClientInfo().getAcceptedMediaTypes().clear();
			request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(OPSINResource.TYPE_CML));
			request.getResourceRef().setPath(path.substring(0, path.length()-4));
		}
		else if (path.endsWith(".png")) {
			request.getClientInfo().getAcceptedMediaTypes().clear();
			request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.IMAGE_PNG));
			request.getResourceRef().setPath(path.substring(0, path.length()-4));
		}
		else if (path.endsWith(".inchi")) {
			request.getClientInfo().getAcceptedMediaTypes().clear();
			request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(OPSINResource.TYPE_INCHI));
			request.getResourceRef().setPath(path.substring(0, path.length()-6));
		}
		else if (path.endsWith(".smi")) {
			request.getClientInfo().getAcceptedMediaTypes().clear();
			request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(OPSINResource.TYPE_SMILES));
			request.getResourceRef().setPath(path.substring(0, path.length()-4));
		}

		return CONTINUE;
	}

}
