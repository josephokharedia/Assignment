package com.okharedia.assignment.test;


import com.okharedia.assignment.domain.Post;
import com.okharedia.assignment.domain.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class UserTest extends ApplicationTest {

    @Test
    public void ensureUserIsConstructedCorrectly() {
        try {
            new User.Builder("").build();
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
        }

        try {
            new User.Builder(" ").build();
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void ensureThatUserIsImmutable() {
        User peter = new User.Builder("Peter").build();
        try {
            peter.getPosts().add(new Post(peter.getName(), "This is Peter 's post!"));
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

        try {
            peter.getFeed().add(new Post(peter.getName(), "This is Peter's post!"));
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

        try {
            peter.getFollowings().add(new User.Builder("John").build());
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void ensureUsersWithTheSameNameAreEqual() {
        User peter = new User.Builder("Peter").build();
        User alsoPeter = new User.Builder("Peter").build();
        User john = new User.Builder("John").build();
        User yetAnotherPeter = new User.Builder("Peter")
                .addFollowing(john)
                .addPost("This is a post!")
                .build();

        assertThat(peter, equalTo(alsoPeter));
        assertThat(peter, not(equalTo(john)));
        assertThat(peter, equalTo(yetAnotherPeter));
    }

    @Test
    public void ensureUserCannotAddHimselfAsAFollowing() {
        User peter = new User.Builder("Peter").build();

        try {
            User alsoPeter = new User.Builder("Peter").build();
            new User.Builder(peter).addFollowing(alsoPeter).build();
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
            assertThat(e.getMessage(), equalTo("User cannot follow himself"));
        }
    }

    @Test
    public void ensureThatUserBuiltFromBuilderIsComplete() {
        User peter, john, phillip;

        john = new User.Builder("John").addPost("This is John's post!").build();
        phillip = new User.Builder("Phillip").addPost("This is Philip's post!").build();
        peter = new User.Builder("Peter")
                .addFollowing(john)
                .addFollowing(phillip)
                .addPost("This is a Peter's post!")
                .build();

        assertThat(peter.getName(), equalTo("Peter"));
        assertThat(peter.getPosts().size(), is(1));
        assertThat(peter.getPosts().get(0).getText(), equalTo("This is a Peter's post!"));
        assertThat(peter.getPosts().get(0).getUsername(), equalTo(peter.getName()));
        assertThat(peter.getFollowings().size(), is(2));
        assertThat(peter.getFollowings(), containsInAnyOrder(john, phillip));
    }

    @Test
    public void ensureThatUserCannotFollowTheSameUserMoreThanOnce() {
        User john = new User.Builder("John").build();
        User alsoJohn = new User.Builder("John").build();
        User peter = new User.Builder("Peter")
                .addFollowing(john)
                .addFollowing(alsoJohn)
                .build();

        assertThat(peter.getFollowings().size(), is(1));
    }

    @Test
    public void ensureThatUserFeedShouldIncludeOwnPostsAndFollowingsPost() {
        String petersPostText = "This is Peter's post!";
        String johnsPostText = "This is John's post!";
        String phillipsPostText = "This is Phillip's post!";


        User john = new User.Builder("John").addPost(johnsPostText).build();
        User phillip = new User.Builder("Phillip").addPost(phillipsPostText).build();

        // Peter follows John. Feed should include Peters post and Johns post but not Phillips;
        User peter = new User.Builder("Peter").addFollowing(john).addPost(petersPostText).build();

        assertThat(john.getFeed().size(), is(1));
        assertThat(phillip.getFeed().size(), is(1));

        assertThat(john.getFeed().stream().allMatch(p -> p.getText().equals(johnsPostText)), is(true));
        assertThat(phillip.getFeed().stream().allMatch(p -> p.getText().equals(phillipsPostText)), is(true));

        assertThat(peter.getFeed().size(), is(2));
        assertThat(peter.getFeed().stream().anyMatch(p -> p.getText().equals(petersPostText)), is(true));
        assertThat(peter.getFeed().stream().anyMatch(p -> p.getText().equals(johnsPostText)), is(true));
        assertThat(peter.getFeed().stream().noneMatch(p -> p.getText().equals(phillipsPostText)), is(true));
    }


}
