/****************************************************************************
* Copyright (C) 2011 Daniel Lowe
*
* This file is part of the OPSIN Web Service
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* A copy of the GNU General Public License version 3 is included in LICENSE.GPL
***************************************************************************/
package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.Request;
import org.restlet.Response;
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
