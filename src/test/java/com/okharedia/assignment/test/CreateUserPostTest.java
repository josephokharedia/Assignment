package com.okharedia.assignment.test;

import com.okharedia.assignment.application.UserActivityService;
import com.okharedia.assignment.application.internal.DefaultUserActivityService;
import com.okharedia.assignment.domain.Post;
import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;
import com.okharedia.assignment.infrastructure.persistence.InMemoryUserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserPostTest extends ApplicationTest{

    UserRepository userRepository;
    UserActivityService userActivityService;
    User peter;

    @Before
    public void setup() {
        resetSingletons();
        userRepository = InMemoryUserRepository.getInstance();
        userActivityService = DefaultUserActivityService.getInstance(userRepository);

        peter = new User.Builder("Peter").build();
        userRepository.saveUser(peter);
    }


    @Test
    public void invalidCommandShouldThrowExceptions() {
        try {
            userActivityService.createUserPost(null);
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }

        try {
            userActivityService.createUserPost("invalid command");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
        }
    }

    @Test
    public void validCommandShouldAddPostToUserInRepository() {
        String post = "This is a post!";
        String command = String.format("%s> %s", peter.getName(), post);
        userActivityService.createUserPost(command);
        assertThat(userRepository.findUserByName(peter.getName()).isPresent(), is(true));

        List<Post> posts = userRepository.findUserByName(peter.getName()).get().getPosts();
        assertThat(posts.size(), is(1));

        boolean containsPostAdded = posts.stream()
                .filter(p -> p.getUsername().equals(peter.getName()))
                .filter(p -> Objects.nonNull(p.getTimestamp()))
                .allMatch(p -> p.getText().equals(post));

        assertThat(containsPostAdded, is(true));
    }
}
