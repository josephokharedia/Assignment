package com.okharedia.assignment.infrastructure.persistence;

import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {

    private static UserRepository instance;
    private Set<User> users = new HashSet<>();

    private InMemoryUserRepository() {
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryUserRepository();
        }

        return instance;
    }

    @Override
    public void saveUser(User user) {
        users.remove(user);
        users.add(user);
    }

    @Override
    public Set<User> findAll() {
        return Collections.unmodifiableSet(users.stream().map(this::refreshFollowings).collect(Collectors.toSet()));
    }

    @Override
    public Optional<User> findUserByName(String name) {
        return users.stream()
                .filter(p -> p.getName().equals(name))
                .map(this::refreshFollowings)
                .findFirst();
    }

    /**
     * Used to keep followings list up to date that might be stale.
     * Analogous to JPA entity refresh. A tool like hibernate will effectively handle this seamlessly
     *
     * @param user - The user that needs it's followings list refreshed
     * @return same user in param with up to date followings
     */
    private User refreshFollowings(User user) {
        Function<String, User> findFollowingByName = name -> users.stream().filter(usr -> usr.getName().equals(name)).findFirst().orElse(null);

        User.Builder userBuilder = new User.Builder(user);
        user.getFollowings().stream()
                .map(User::getName)
                .map(findFollowingByName)
                .filter(Objects::nonNull)
                .forEach(userBuilder::addFollowing);

        return userBuilder.build();
    }
}
