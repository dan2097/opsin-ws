package uk.ac.cam.ch.opsin.ws;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

public class OpsinStatusService extends StatusService {

	public OpsinStatusService() {
		super(true);
	}

	@Override
	public Representation getRepresentation(Status status, Request request, Response response) {
		if (status.isError()) {
			StringBuilder sb = new StringBuilder();
			sb.append("OPSIN has failed to process the following request:\n");
			sb.append(request.getResourceRef().getLastSegment(true)).append("\n");
			sb.append("The reason given for this failure is as follows:\n");
			sb.append(status.getDescription()).append("\n");
			return new StringRepresentation(sb.toString());
		} 
		return null;
	}
}
