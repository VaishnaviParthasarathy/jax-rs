package com.contacts.service;

import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

public class SimpleOAuthService {

	 private static String accessToken = null;
	    /**
	     * Contains null or actually authorization flow.
	     */
	    private static GoogleAuthorizationCodeFlow flow=null;
	    private static ClientIdentifier clientIdentifier;

	    public static String getAccessToken() {
	        return accessToken;
	    }

	    public static void setAccessToken(String accessToken) {
	        SimpleOAuthService.accessToken = accessToken;
	    }

	    public static GoogleAuthorizationCodeFlow getFlow() {
	        return flow;
	    }

	    public static void setFlow(GoogleAuthorizationCodeFlow flow) {
	        SimpleOAuthService.flow = flow;
	    }

	    public static ClientIdentifier getClientIdentifier() {
	        return clientIdentifier;
	    }

	    public static void setClientIdentifier(ClientIdentifier clientIdentifier) {
	        SimpleOAuthService.clientIdentifier = clientIdentifier;
	    }
}
