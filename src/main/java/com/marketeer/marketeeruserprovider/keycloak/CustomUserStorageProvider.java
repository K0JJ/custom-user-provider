package com.marketeer.marketeeruserprovider.keycloak;

import com.marketeer.marketeeruserprovider.client.AuthorizationClient;
import com.marketeer.marketeeruserprovider.client.UserManagementClient;
import com.marketeer.marketeeruserprovider.client.mock.AuthorizationClientImpl;
import com.marketeer.marketeeruserprovider.client.mock.UserManagementClientImpl;
import com.marketeer.marketeeruserprovider.keycloak.model.KeycloakUser;
import com.marketeer.marketeeruserprovider.web.dto.User;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.java.Log;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Log
public class CustomUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider,
        UserQueryProvider,
        CredentialInputUpdater,
        CredentialInputValidator {


    private final UserManagementClient userManagementClient;

    private final AuthorizationClient authorizationClient;


    private final KeycloakSession session;

    private final ComponentModel model;


    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.userManagementClient = new UserManagementClientImpl();
        this.authorizationClient = new AuthorizationClientImpl();
    }


    @Override
    public boolean supportsCredentialType(String s) {
        return PasswordCredentialModel.TYPE.endsWith(s);
    }

    // we are not allowing updating credentials from here but using custom rest endpoints
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) return false;
        if (!input.getType().equals(PasswordCredentialModel.TYPE)) return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        return false;
    }


    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        return null;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String s) {
        return supportsCredentialType(s);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        if (!this.supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }
        StorageId storageId = new StorageId(userModel.getId());
        String username = storageId.getExternalId();

        User user = userManagementClient.getUser(username);

        if (user == null || user.username() == null) {
            return false;
        }
        return authorizationClient.checkPassword(user.username(), credentialInput.getChallengeResponse());
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String s) {
        return findUser(realmModel, s);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String s) {
        return findUser(realmModel, s);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String s) {
        return findUser(realmModel, s);
    }

    private UserModel findUser(RealmModel realm, String identifier) {
        UserModel adapter;
        try {
            // the identifier comes in the form of f:<random realm id>:<actual username>
            // at times, so we need to remove the f:<random realm id> part
            if (identifier.matches("f:.*:.*")) {
                log.info(String.format("found an id with rand prefix with identifier %s found and user being %s", identifier, identifier));
                String[] split = identifier.split(":");
                identifier = split[2];
            }
            User userToLoad = userManagementClient.getUser(identifier);
            log.info(String.format("User with identifier %s found and user being %s", identifier, userToLoad.username()));
            adapter = new KeycloakUser(session, realm, model, userToLoad);
            return adapter;
        } catch (WebApplicationException e) {
            log.info(String.format("User with identifier %s could not be found, response from server: %s",
                    identifier,
                    e.getCause().getMessage()));
        } catch (Exception e) {
            log.info(
                    String.format("Some other error for user %s with message being %s",
                            identifier,
                            e.getMessage())
            );
        }

        return null;
    }

    // currently works for username only
    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer firstResult, Integer maxResult) {
        String toLookup = map.values().stream().filter(value -> value != null && !value.isBlank()).findFirst().orElse(null);
        return toUserModelStream(userManagementClient.getUsers(toLookup, 0, maxResult), realmModel);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        log.info("Getting all users");
        return toUserModelStream(userManagementClient.getUsers(s, 0, 10), realmModel);
    }

    private Stream<UserModel> toUserModelStream(Set<User> users, RealmModel realm) {

        // log.debug("Received {} users from provider", users.size());
        return users.stream().map(user -> new KeycloakUser(session, realm, model, user));
    }


    // we will add the user and the make the user fill up an extra form to fill up the rest of the details
    // this will just create a user with a username and a password specifying the user is oauth generated
    @Override
    public UserModel addUser(RealmModel realm, String username) {
        User entity = new User(
                username,
                "OAUTH_GENERATED_USER"
        );
        User user = userManagementClient.saveUser(entity);
        return new KeycloakUser(session, realm, model, user);
    }


    // don't allow user to be removed from keycloak
    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        return false;
    }
}
