package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.restlet.data.CharacterSet;
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
	
	private static String errorHtml;
	private static Pattern matchRegexReplacement;
	
	static {
		try {
			errorHtml = IOUtils.toString(OpsinStatusService.class.getResourceAsStream("/error.html"));
		} catch (IOException e) {
			errorHtml = null;
		}
		matchRegexReplacement = Pattern.compile("%.*?%");
	}

	public Representation getRepresentation(Status status, Request request, Response response) {
		if (status.isError()){
			StringBuilder pageHtml = new StringBuilder();
			Matcher m = matchRegexReplacement.matcher(errorHtml);
			int position = 0;
			while(m.find()) {//replace sections enclosed in %..% with the appropriate expression
				pageHtml.append(errorHtml.substring(position, m.start()));
				if (m.group().equals("%INPUT%")){
					pageHtml.append(request.getResourceRef().getRemainingPart());
				}
				else if (m.group().equals("%ERRORMESSAGE%")){
					pageHtml.append(status.getDescription());
				}
				else if (m.group().equals("%ERRORCODE%")){
					pageHtml.append(status.getCode());
				}
				position = m.end();
			}
			pageHtml.append(errorHtml.substring(position));
			return new StringRepresentation(pageHtml.toString(), MediaType.TEXT_HTML, null, CharacterSet.UTF_8);
		} 
		return null;
	}
}
