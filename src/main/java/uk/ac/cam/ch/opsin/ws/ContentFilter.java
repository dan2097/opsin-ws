package uk.ac.cam.ch.opsin.ws;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Reference;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.routing.Filter;

public class ContentFilter extends Filter {
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		String path = request.getResourceRef().getPath();
        if (path.equals("/opsin") || path.equals("/opsin/")) {
            response.redirectSeeOther(new Reference(request.getResourceRef().getBaseRef(), "/instructions.html"));
            return STOP;
        }

		List<Preference<MediaType>> acceptedMediaTypes = request.getClientInfo().getAcceptedMediaTypes();
		if (path.endsWith(".no2d.cml")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_NO2DCML));
			request.getResourceRef().setPath(path.substring(0, path.length() - 9));
		}
		else if (path.endsWith(".cml")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_CML));
			request.getResourceRef().setPath(path.substring(0, path.length() - 4));
		}
		else if (path.endsWith(".json")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_JSON));
			request.getResourceRef().setPath(path.substring(0, path.length() - 5));
		}
		else if (path.endsWith(".png")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(MediaType.IMAGE_PNG));
			request.getResourceRef().setPath(path.substring(0, path.length() - 4));
		}
		else if (path.endsWith(".inchi")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_INCHI));
			request.getResourceRef().setPath(path.substring(0, path.length() - 6));
		}
		else if (path.endsWith(".stdinchi")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_STDINCHI));
			request.getResourceRef().setPath(path.substring(0, path.length() - 9));
		}
		else if (path.endsWith(".stdinchikey")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_STDINCHIKEY));
			request.getResourceRef().setPath(path.substring(0, path.length() - 12));
		}
		else if (path.endsWith(".smi")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(OpsinResource.TYPE_SMILES));
			request.getResourceRef().setPath(path.substring(0, path.length() - 4));
		}
		else if (path.endsWith(".svg")) {
			acceptedMediaTypes.clear();
			acceptedMediaTypes.add(new Preference<MediaType>(MediaType.IMAGE_SVG));
			request.getResourceRef().setPath(path.substring(0, path.length() - 4));
		}
		return CONTINUE;
	}

}
