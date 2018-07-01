package com.okharedia.assignment.application.internal;

import com.okharedia.assignment.application.UserActivityService;
import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultUserActivityService implements UserActivityService {

    private static UserActivityService instance;
    private UserRepository userRepository;

    private DefaultUserActivityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static synchronized UserActivityService getInstance(UserRepository userRepository) {
        if (instance == null) {
            instance = new DefaultUserActivityService(userRepository);
        }
        return instance;
    }

    @Override
    public void createUserFollowings(final String command) {
        // Validate command
        Objects.requireNonNull(command);
        final String regex = "(?<userName>[A-Za-z0-9]+)\\s+(follows)\\s+(?<followingsNames>[A-Za-z0-9,\\s+]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("command[%s] NOT valid!", command));
        }

        // Init
        String userName = matcher.group("userName");
        String[] followingsNames = matcher.group("followingsNames").split(",");


        // Find User and his Followings in Repository
        User user = findUserByNameOrCreate(userName);
        Set<User> followings = Stream.of(followingsNames)
                .map(this::findUserByNameOrCreate)
                .collect(Collectors.toSet());


        // Add Followings to User
        User.Builder userBuilder = new User.Builder(user);
        followings.forEach(userBuilder::addFollowing);
        user = userBuilder.build();


        // Save User and his Followings to Repository
        followings.forEach(f -> userRepository.saveUser(f));
        userRepository.saveUser(user);
    }

    @Override
    public void createUserPost(final String command) {
        // Validate command
        Objects.requireNonNull(command);
        final String regex = "(?<userName>[A-Za-z]+)(>\\s)(?<postText>.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("command[%s] NOT valid!", command));
        }

        // Init
        String userName = matcher.group("userName");
        String postText = matcher.group("postText");


        // Find User from Repository
        User user = findUserByNameOrCreate(userName);


        // Add Post to User
        user = new User.Builder(user).addPost(postText).build();


        // Save User
        userRepository.saveUser(user);

    }

    private User findUserByNameOrCreate(String name) {
        return userRepository.findUserByName(name)
                .orElse(new User.Builder(name).build());
    }


}
