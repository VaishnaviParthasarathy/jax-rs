package com.contacts.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;

import com.contacts.service.SimpleOAuthService;

@Path("oauth2")
public class AuthorizationResource {
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("authorize")
    public Response authorize(@QueryParam("code") String code, @QueryParam("state") String state) {
        final OAuth2CodeGrantFlow flow = SimpleOAuthService.getFlow();

        final TokenResult tokenResult = flow.finish(code, state);

        SimpleOAuthService.setAccessToken(tokenResult.getAccessToken());

        // authorization is finished -> now redirect back to the task resource
        final URI uri = UriBuilder.fromUri(uriInfo.getBaseUri()).path("contact").build();
        return Response.seeOther(uri).build();
    }
}
