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

import org.restlet.Request;
import org.restlet.Response;
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
