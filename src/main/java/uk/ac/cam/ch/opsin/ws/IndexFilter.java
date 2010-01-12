package uk.ac.cam.ch.opsin.ws;

import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.routing.Filter;


public class IndexFilter extends Filter {
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		if ("/".equals(request.getResourceRef().getPath())) {
			request.getResourceRef().setPath("/index.html");
		}
		return CONTINUE;
	}

}
