package com.marketeer.marketeeruserprovider.client.mock;

import com.marketeer.marketeeruserprovider.client.UserManagementClient;
import com.marketeer.marketeeruserprovider.web.dto.User;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log
public class UserManagementClientImpl implements UserManagementClient {

    static final Set<User> USERS = new HashSet<>();

    @Override
    public Set<User> getUsers(String search, int page, int size) {
        if (USERS.isEmpty())
            createUsers();
        return USERS.stream().filter(user -> user.username().startsWith(search))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toSet());
    }

    @Override
    public int countUsers() {
        if (USERS.isEmpty())
            createUsers();
        return USERS.size();
    }

    @Override
    public User saveUser(User user) {
        if(user == null)
            return null;

        return USERS.add(user) ? user : null;
    }

    @Override
    public User getUser(String id) {
        if (USERS.isEmpty())
            createUsers();
        log.info("id: " + id + " and size is " + USERS.size());
        return USERS.stream().filter(user ->
                        user
                                .username()
                                .startsWith(id))
                .findFirst()
                .orElse(null);
    }

    static void createUsers() {
        for (int i = 0; i < 10; i++) {
            // create random user
            // add user
            User temp = new User(
                    String.format("username-%d", i),
                    String.format("firstName-%d", i),
                    String.format("lastName-%d", i),
                    String.format("{bcrypt-12}password-%d", i),
                    String.format("email@this-%d.com", i),
                    String.format("10-%d=200%d", i, i),
                    List.of("permission.read, permission.update, permission.delete" + i)
            );
            USERS.add(temp);

        }
    }
}
