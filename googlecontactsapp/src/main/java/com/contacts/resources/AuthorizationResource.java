package com.contacts.resources;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.contacts.service.SimpleOAuthService;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

@Path("oauth2")
public class AuthorizationResource {
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("authorize")
    public Response authorize(@QueryParam("code") String code, @QueryParam("state") String state) throws IOException {
       GoogleAuthorizationCodeFlow flow=SimpleOAuthService.getFlow();
        
	if(SimpleOAuthService.getAccessToken()==null){
	    TokenRequest tokenRequest=flow.newTokenRequest(code).setRedirectUri(uriInfo.getAbsolutePath().toString());
	    
	    System.out.println(code);
    
	    //tokenRequest.
	    SimpleOAuthService.setAccessToken(tokenRequest.execute().getAccessToken());
	}

        // authorization is finished -> now redirect back to the task resource
        final URI uri = UriBuilder.fromUri(uriInfo.getBaseUri()).path("contact/googlecontact").build();
        return Response.seeOther(uri).build();
    }
}
