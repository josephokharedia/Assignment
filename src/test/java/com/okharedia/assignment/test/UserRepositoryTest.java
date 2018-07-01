package com.okharedia.assignment.test;

import com.okharedia.assignment.domain.User;
import com.okharedia.assignment.domain.UserRepository;
import com.okharedia.assignment.infrastructure.persistence.InMemoryUserRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserRepositoryTest extends ApplicationTest {

    UserRepository userRepository;

    @Before
    public void setup() {
        resetSingletons();
        userRepository = InMemoryUserRepository.getInstance();
    }

    @Test
    public void testSaveUserIsSavedCorrectly() {
        User peter = new User.Builder("Peter").build();
        userRepository.saveUser(peter);
        assertThat(userRepository.findAll().size(), is(1));
        assertThat(userRepository.findUserByName("Peter").isPresent(), is(true));
    }

    @Test
    public void usersCollectionFromRepositoryIsImmutable() {
        try {
            userRepository.findAll().add(new User.Builder("Peter").build());
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testThatFollowingsAreUpToDateWhenRetrievingUsersFromRepository() {
        User peter = new User.Builder("Peter").build();
        User john = new User.Builder("John").build();

        userRepository.saveUser(peter);
        userRepository.saveUser(john);

        // Peter follows John
        peter = new User.Builder(peter).addFollowing(john).build();
        userRepository.saveUser(peter);

        // John decides to add a post
        String johnsPostText = "This is John's post!";
        john = new User.Builder(john).addPost(johnsPostText).build();
        userRepository.saveUser(john);

        // Now at this point, Peter has a copy of John in his followings list that
        // does not have this new Post. Observe Peter's feed not including John's post
        assertThat(peter.getFeed().stream().noneMatch(p -> p.getText().equals(johnsPostText)), is(true));

        // But going back to retrieve Peter from the repository will update his followings list
        // with the copy of John that has a Post
        assertThat(userRepository.findUserByName(peter.getName()).isPresent(), is(true));
        peter = userRepository.findUserByName(peter.getName()).get();

        // We have a new peter from repository. Now this peter will have the feed from John
        assertThat(peter.getFeed().stream().anyMatch(p -> p.getText().equals(johnsPostText)), is(true));
    }

}
