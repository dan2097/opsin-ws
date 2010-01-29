package uk.ac.cam.ch.opsin.ws;

import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

public class OpsinStatusService extends StatusService{

	public OpsinStatusService() {
		super(true);
	}

	public Representation getRepresentation(Status status, Request request, Response response) {
		 if (status.isError()){
			 System.out.println(request.toString());
			 System.out.println(request.getResourceRef().toString());
			return new StringRepresentation("This is an OPSIN error <a href='/opsin/cyclobutane.png'>link</a>", MediaType.TEXT_HTML);
		 } 

		return null;
		
	}
}
