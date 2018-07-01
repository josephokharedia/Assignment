package com.okharedia.assignment;

import com.okharedia.assignment.application.UserActivityService;
import com.okharedia.assignment.application.internal.DefaultUserActivityService;
import com.okharedia.assignment.domain.Post;
import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;
import com.okharedia.assignment.infrastructure.persistence.InMemoryUserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        UserRepository userRepository = InMemoryUserRepository.getInstance();
        UserActivityService userActivityService = DefaultUserActivityService.getInstance(userRepository);

        List<String> files = Arrays.asList(args);
        if (files.size() < 2) {
            throw new IllegalArgumentException("Please provide 2 files in the correct order e.g users.txt tweets.txt");
        }

        // Read files from commandline
        try (Stream<String> usersFileStream = Files.lines(Paths.get(files.get(0)));
             Stream<String> tweetsFileStream = Files.lines(Paths.get(files.get(1)))) {

            List<String> users = usersFileStream.collect(Collectors.toList());
            List<String> tweets = tweetsFileStream.collect(Collectors.toList());

            // Invoke the import process
            users.forEach(userActivityService::createUserFollowings);
            tweets.forEach(userActivityService::createUserPost);


        } catch (IOException e) {
            e.printStackTrace();
        }


        userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getName))
                .peek(u -> System.out.println(String.format("%s", u.getName())))
                .forEach(u -> u.getFeed().stream()
                        .sorted(Comparator.comparing(Post::getTimestamp))
                        .forEach(post -> System.out.println(String.format("\t@%s: %s", post.getUsername(), post.getText()))));
    }
}
