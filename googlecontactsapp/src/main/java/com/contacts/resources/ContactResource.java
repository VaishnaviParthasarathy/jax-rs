package com.contacts.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.OAuth2FlowGoogleBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.contacts.service.SimpleOAuthService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleService.People;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PersonMetadata;

@Path("contact")
public class ContactResource {

    private static final String GOOGLE_TASKS_BASE_URI = "https://people.googleapis.com/v1";

    @Context
    private UriInfo uriInfo;

    @Context
    private ServletContext servletContext;

    @GET
    @Produces("text/html")
    public Response getContacts() {

	// check oauth setup

	if (SimpleOAuthService.getClientIdentifier() == null) {

	    final URI uri = UriBuilder.fromUri(servletContext.getContextPath())
		    .path("/index.jsp") // to show "Enter your Client Id and  Secret" setup page
		    .build();
	    return Response.seeOther(uri).build();

	}

	// check access token
	if (SimpleOAuthService.getAccessToken() == null) {
	    	return googleAuthRedirect();

	}
	// We have already an access token. Query the data from Google API.
	//final Client client = SimpleOAuthService.getFlow().getAuthorizedClient();
	//return getContactsResponse(client);
	return Response.ok().build();

    }

    private Response createContactResponse(Client client) {
	client.register(JacksonFeature.class);
	final WebTarget baseTarget = client.target(GOOGLE_TASKS_BASE_URI);

	Person person=new Person();
	List<Name> names = new ArrayList<Name>();
	Name name=new Name();
	name.setGivenName("Shatu");
	names.add(name);
	person.setNames(names);
	
	
	
	
	//Gson gson=new Gson();
	
	
	final Response response = baseTarget.path("people:createContact").queryParam("parent", "people/me").request()
		.post(Entity.json(person));

	System.out.println(response);

	

	switch (response.getStatus()) {

	case 401: // Response.Status.UNAUTHORIZED

	    SimpleOAuthService.setAccessToken(null);
	    return googleAuthRedirect();

	case 200: // Response.Status.CREATED

	    System.out.println("response status created");
	    Person person2 = response.readEntity(Person.class);
	   // System.out.println(person2.getResourceName());
	    break;

	default:
	    person = null;

	}

	// final AllTaskListsModel tasks = new
	// AllTaskListsModel(listOfTaskLists);

	return Response.ok().build();
    }

    private Response getContactsResponse(Client client) {

	client.register(JacksonFeature.class);

	// client.register(new
	// LoggingFeature(Logger.getLogger("example.client.tasks"),
	// LoggingFeature.Verbosity.PAYLOAD_ANY));

	final WebTarget baseTarget = client.target(GOOGLE_TASKS_BASE_URI);

	final Response response = baseTarget.path("people/me").queryParam("personFields", "birthdays").request().get();

	System.out.println(response.toString());

	Person person;

	switch (response.getStatus()) {

	case 401: // Response.Status.UNAUTHORIZED

	    SimpleOAuthService.setAccessToken(null);
	    return googleAuthRedirect();

	case 200: // Response.Status.OK

	    System.out.println("response status ok");
	    person = response.readEntity(Person.class);
	    System.out.println(person.getBirthdays().get(0));
	    break;

	default:
	    person = null;

	}

	// final AllTaskListsModel tasks = new
	// AllTaskListsModel(listOfTaskLists);

	return Response.ok().build();

    }

    /**
     * 
     * Prepare redirect response to Google Tasks API auth consent request.
     *
     * 
     * 
     * @return redirect response to Google Tasks API auth consent request
     * 
     */

    private Response googleAuthRedirect() {

	final String redirectURI = UriBuilder.fromUri(uriInfo.getBaseUri())
		.path("oauth2/authorize").build().toString();

	final OAuth2CodeGrantFlow flow = OAuth2ClientSupport.googleFlowBuilder(
		SimpleOAuthService.getClientIdentifier(),
		redirectURI,
		"https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/contacts")
		.prompt(OAuth2FlowGoogleBuilder.Prompt.CONSENT).build();

	//SimpleOAuthService.setFlow(flow);

	// start the flow
	final String googleAuthURI = flow.start();

	// redirect user to Google Authorization URI.
	return Response.seeOther(UriBuilder.fromUri(googleAuthURI).build()).build();

    }
    
    
    @GET
    @Path("googlecontact")
    @Produces("text/plain")
    public Response  getGoogleAPIContacts() throws IOException, GeneralSecurityException {

	//GoogleAuthorizationCodeFlow flow = null;
	if(SimpleOAuthService.getFlow() == null){
	    GoogleAuthorizationCodeFlow flow = initializePeopleFlow();
	    SimpleOAuthService.setFlow(flow);
	    
	    GoogleAuthorizationCodeRequestUrl requestUrl=flow.newAuthorizationUrl();
	    
	    final String redirectURI = UriBuilder.fromUri(uriInfo.getBaseUri())
			.path("oauth2/authorize").build().toString();
	    
	    requestUrl.setRedirectUri(redirectURI);
	  
	    System.out.println(requestUrl);
	    return Response.seeOther(requestUrl.toURI()).build();
	}
	System.out.println("back from oauth");
	if(SimpleOAuthService.getAccessToken()!=null){
	    GoogleCredential credential = new GoogleCredential().setAccessToken(SimpleOAuthService.getAccessToken());
	 
	    PeopleService peopleService = new PeopleService.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
	        .setApplicationName("ContactsWebApp")
	        .build();
	    Person person=peopleService.people().get("people/me").setPersonFields("names").execute();
	   System.out.println(person.getNames().get(0).getGivenName());
	    //System.out.println(person);
	}
	
        return Response.ok().build();

    }

    
    private GoogleAuthorizationCodeFlow initializePeopleFlow() throws IOException, GeneralSecurityException {
	
	
	/*HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	GoogleClientSecrets secrets=GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
		(new Reader("/Users/vijayarajm/EclipseProjects/jaxrs/googlecontactsapp/client_secret.json"));

	   

	    
	*/
	return new GoogleAuthorizationCodeFlow
		.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),"230064408021-vansbke3qi43j15065iosdqjbs78phfl.apps.googleusercontent.com"
			,"0yOsct6w-FbKYhDTO3QmBISR"
			,Collections.singleton("https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/contacts"))
		.setAccessType("online").setApprovalPrompt("force").build();
		    
		    
		  
		        
    
    }
}
