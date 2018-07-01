package com.okharedia.assignment.test;

import com.okharedia.assignment.domain.Post;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostTest extends ApplicationTest{

    @Test
    public void ensurePostIsConstructedCorrectly() {
        try {
            new Post(null, null);
            new Post(null, "");
            new Post("", null);
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }

        try {
            new Post("", "");
            new Post(" ", "");
            new Post("", " ");
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void ensurePostsTimestampsAndHashCodeAreNeverEqual() {
        Post petersPost = new Post("Peter", "This is Peter's post!");
        Post anotherPostFromPeter = new Post("Peter", "This is Peter's post!");
        assertThat(petersPost.getTimestamp(), not(equalTo(anotherPostFromPeter.getTimestamp())));
        assertThat(petersPost.hashCode(), not(equalTo(anotherPostFromPeter.hashCode())));
    }

    @Test
    public void ensurePostsWithSameValuesAreNeverEqual() {
        Post petersPost = new Post("Peter", "This is Peter's post!");
        Post anotherPostFromPeter = new Post("Peter", "This is Peter's post!");
        assertThat(petersPost, not(equalTo(anotherPostFromPeter)));
    }
}
