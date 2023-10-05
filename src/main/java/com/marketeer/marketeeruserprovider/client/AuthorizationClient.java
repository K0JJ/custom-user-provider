package com.marketeer.marketeeruserprovider.client;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AuthorizationClient {


    @GET
    @Path("api/v1/auth/check-password")
    boolean checkPassword(String userId, String password);
}
