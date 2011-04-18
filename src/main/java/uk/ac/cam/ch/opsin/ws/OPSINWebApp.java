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

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

/**
 *
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OPSINWebApp extends Application {

    public OPSINWebApp() {
        this.setStatusService(new OpsinStatusService());
    }
    
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attachDefault(OPSINResource.class); 

        // Filter to override content negotiation by file extension
        // MUST be applied before template, otherwise file extension ends up in name!
        ContentFilter filter = new ContentFilter();
        filter.setNext(router);
        return filter;
    }

    @Override
    public void start() throws Exception {
        super.start();
        try {
            NameToStructure.getInstance();//initialise OPSIN early
        }
        catch (Exception e) {
            throw new RuntimeException("OPSIN failed to intialise", e);
        }
    }
}
