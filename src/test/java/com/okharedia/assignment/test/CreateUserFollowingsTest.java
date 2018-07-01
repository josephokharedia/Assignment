package com.okharedia.assignment.test;

import com.okharedia.assignment.application.UserActivityService;
import com.okharedia.assignment.application.internal.DefaultUserActivityService;
import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;
import com.okharedia.assignment.infrastructure.persistence.InMemoryUserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserFollowingsTest extends ApplicationTest {

    UserRepository userRepository;
    UserActivityService userActivityService;

    @Before
    public void setup() {
        resetSingletons();
        userRepository = InMemoryUserRepository.getInstance();
        userActivityService = DefaultUserActivityService.getInstance(userRepository);
    }


    @Test
    public void invalidCommandShouldThrowExceptions() {
        try {
            userActivityService.createUserFollowings(null);
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }

        try {
            userActivityService.createUserFollowings("invalid command");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
        }
    }

    @Test
    public void validCommandShouldAddUserAndFollowingsToRepository() {
        userActivityService.createUserFollowings("peter follows john");
        assertThat(userRepository.findUserByName("peter").isPresent(), is(true));
        assertThat(userRepository.findUserByName("john").isPresent(), is(true));

        Set<User> followings = userRepository.findUserByName("peter").get().getFollowings();
        assertThat(followings.size(), is(1));

        boolean containsJohn = followings.stream().anyMatch(f -> f.getName().equals("john"));
        assertThat(containsJohn, is(true));
    }

    @Test
    public void commaSeparatedUserFollowingsAreAddedCorrectly() {
        userActivityService.createUserFollowings("peter follows john,phillip");
        assertThat(userRepository.findUserByName("peter").isPresent(), is(true));

        Set<User> followings = userRepository.findUserByName("peter").get().getFollowings();
        assertThat(followings.size(), is(2));

        boolean containsJohn = followings.stream().anyMatch(f -> f.getName().equals("john"));
        boolean containsPhillip = followings.stream().anyMatch(f -> f.getName().equals("phillip"));

        assertThat(containsJohn, is(true));
        assertThat(containsPhillip, is(true));
    }

    @Test
    public void multipleCommandsShouldAddUserToRepositoryOnce() {
        userActivityService.createUserFollowings("peter follows john");
        userActivityService.createUserFollowings("peter follows phillip");
        userActivityService.createUserFollowings("john follows peter,phillip");
        long peterCount = userRepository.findAll().stream().filter(u -> u.getName().equals("peter")).count();
        long johnCount = userRepository.findAll().stream().filter(u -> u.getName().equals("john")).count();
        long phillipCount = userRepository.findAll().stream().filter(u -> u.getName().equals("phillip")).count();
        assertThat(peterCount, is(1L));
        assertThat(johnCount, is(1L));
        assertThat(phillipCount, is(1L));
    }


}
