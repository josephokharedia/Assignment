package com.okharedia.assignment.domain;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    void saveUser(User user);

    Set<User> findAll();

    Optional<User> findUserByName(String name);
}
