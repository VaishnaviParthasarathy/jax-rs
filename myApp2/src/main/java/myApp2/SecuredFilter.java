package myApp2;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

@Provider
public class SecuredFilter implements ContainerRequestFilter {
	
	public static final String AUTHORIZATION="Authorization";
	public static final String AUTHORIZATION_PREFIX="Basic ";


	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		List<String> authHeaders=requestContext.getHeaders().get(AUTHORIZATION);
		if(authHeaders!=null & authHeaders.size() > 0)
		{
			
			String authHeader =authHeaders.get(0);
			authHeader=authHeader.replaceFirst(AUTHORIZATION_PREFIX, "");
			String decodedString=Base64.decodeAsString(authHeader);
			
			StringTokenizer tokenizer=new StringTokenizer(decodedString, ":");
			String username=tokenizer.nextToken();
			String password=tokenizer.nextToken();
			
			if(username.equalsIgnoreCase("user") && password.equalsIgnoreCase("lionking")){
				return;
			}
			
		}
		
		Response unauthorizedResponse=Response.status(Status.UNAUTHORIZED).entity("User not authorized").build();
		
		requestContext.abortWith(unauthorizedResponse);
	}

}
