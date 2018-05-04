package jaxrsclient;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.vaish.java.messenger.model.Message;

import com.books.model.Volume;

public class RestApiClient {

	public static void main(String[] args) {
		
		Client client=ClientBuilder.newClient();
		//Message message=client.target("http://localhost:8080/messenger/webapi/messages/1")
		//.request().get(Message.class);
		//System.out.println(message.getAuthor());
		
		WebTarget target=client.target("http://localhost:8080/messenger/webapi");
		WebTarget messagesTarget = target.path("messages");
		WebTarget singleMessageTarget=messagesTarget.path("{messageId}");
		Message message1=singleMessageTarget.resolveTemplate("messageId", 1).request().get(Message.class);
		
		
		Message message2=singleMessageTarget.resolveTemplate("messageId", 2).request().get(Message.class);
		
		System.out.println(message1.getAuthor());
		System.out.println(message2.getAuthor());
		
		//making a POST request
		Message message3=new Message(3L,"Hello JAX-RS","shatu");
		Response response=messagesTarget.request().post(Entity.json(message3));
		System.out.println(response.getStatus());
		
		List<Message> messages=messagesTarget.request().get(new GenericType<List<Message>>() {});
		
		System.out.println(messages.size());
		
	}

}
