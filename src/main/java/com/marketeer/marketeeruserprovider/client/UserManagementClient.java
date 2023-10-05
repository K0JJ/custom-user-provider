package com.marketeer.marketeeruserprovider.client;

import com.marketeer.marketeeruserprovider.web.dto.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserManagementClient {


    @GET
    @Path("api/v1/users")
    Set<User> getUsers(@QueryParam("search") String search, @QueryParam("page") int page, @QueryParam("size") int size);


    @GET
    @Path("api/v1/users/count")
    int countUsers();


    @POST
    @Path("api/v1/users")
    User saveUser(User user);

    @GET
    @Path("api/v1/users/{id}")
    User getUser(@PathParam("id") String id);


}
