package com.marketeer.marketeeruserprovider.keycloak;


import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

// class use to register the custom user storage provider in the keycloak server
public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public CustomUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomUserStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "markeeter-user-storage-provider";
    }
}
