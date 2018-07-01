package com.okharedia.assignment.domain;

import java.util.Objects;

public final class Post {
    private long timestamp;
    private String text;
    private String username;

    public Post(String username, String text) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(text);
        if(username.trim().isEmpty() || text.trim().isEmpty()){
            throw new RuntimeException("username or text cannot be empty");
        }
        this.username = username;
        this.timestamp = System.nanoTime();
        this.text = text;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getText() {
        return this.text;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return timestamp == post.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "Post{" +
                "timestamp=" + timestamp +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
