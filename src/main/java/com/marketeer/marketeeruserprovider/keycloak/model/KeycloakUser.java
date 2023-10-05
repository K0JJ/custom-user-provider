package com.marketeer.marketeeruserprovider.keycloak.model;

import com.marketeer.marketeeruserprovider.web.dto.User;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.SneakyThrows;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageUtil;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class KeycloakUser extends AbstractUserAdapter {

    private final User user;

    public KeycloakUser(KeycloakSession session, RealmModel realm, ComponentModel model, @NotNull User user) {
        super(session, realm, model);
        this.storageId = new StorageId(storageProviderModel.getId(), user.username());
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.username();
    }

    @Override
    public String getFirstName() {
        return user.firstName();
    }

    @Override
    public String getLastName() {
        return user.lastName();
    }

    @Override
    public String getEmail() {
        return user.email();
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }

    @Override
    public String getFirstAttribute(String name) {
        List<String> list = getAttributes().getOrDefault(name, List.of());
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
        attributes.add("dateOfBirth", user.dateOfBirth());
        return attributes;
    }


    @SneakyThrows
    @Override
    public Stream<String> getAttributeStream(String name) {
        Map<String, List<String>> attributes = getAttributes();
        return (attributes.containsKey(name)) ? attributes.get(name).stream() : Stream.empty();
    }

    @Override
    protected Set<GroupModel> getGroupsInternal() {
        return Set.of();
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        if (user.roles() != null) {
            return user.roles().stream().map(roleName -> new KeycloakUserRoleModel(roleName, realm))
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    @Override
    public Stream<String> getRequiredActionsStream() {
        return getFederatedStorage().getRequiredActionsStream(realm, this.getId());
    }

    @Override
    public void addRequiredAction(String action) {
        getFederatedStorage().addRequiredAction(realm, this.getId(), action);
    }

    @Override
    public void removeRequiredAction(String action) {
        getFederatedStorage().removeRequiredAction(realm, this.getId(), action);
    }

    @Override
    public void addRequiredAction(RequiredAction action) {
        getFederatedStorage().addRequiredAction(realm, this.getId(), action.name());
    }

    @Override
    public void removeRequiredAction(RequiredAction action) {
        getFederatedStorage().removeRequiredAction(realm, this.getId(), action.name());
    }


    @Override
    public void setAttribute(String name, List<String> values) {
    }

    @Override
    public void setFirstName(String firstName) {

    }

    @Override
    public void setLastName(String lastName) {
    }

    @Override
    public void setEmail(String email) {
    }

    @Override
    public void setEmailVerified(boolean verified) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
    }

    @Override
    public void setSingleAttribute(String name, String value) {
    }

    @Override
    public void removeAttribute(String name) {
    }

    UserFederatedStorageProvider getFederatedStorage() {
        return UserStorageUtil.userFederatedStorage(session);
    }
}
