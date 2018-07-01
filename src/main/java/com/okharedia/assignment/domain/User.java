package com.okharedia.assignment.domain;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class User {

    private String name;
    private Set<User> followings;
    private List<Post> posts;

    public User(Builder builder) {
        this.name = builder.name;
        this.followings = Collections.unmodifiableSet(builder.followings);
        this.posts = Collections.unmodifiableList(builder.posts);
    }

    public String getName() {
        return name;
    }

    public Set<User> getFollowings() {
        return this.followings;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public List<Post> getFeed() {
        return Stream.concat(this.posts.stream(), this.followings.stream().flatMap(f -> f.posts.stream()))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", followings=" + followings +
                ", posts=" + posts +
                '}';
    }

    public static class Builder {
        private String name;
        private Set<User> followings = new HashSet<>();
        private List<Post> posts = new ArrayList<>();

        public Builder(User user) {
            Objects.requireNonNull(user);
            this.name = user.name;
            this.followings = new HashSet<>(user.followings);
            this.posts = new ArrayList<>(user.posts);
        }

        public Builder(String name) {
            Objects.requireNonNull(name);
            if(name.trim().isEmpty()){
                throw new RuntimeException("User Name cannot be empty");
            }
            this.name = name.trim();
        }

        public Builder addFollowing(User user) {
            Objects.requireNonNull(user);
            if (user.name.equals(this.name)) {
                throw new RuntimeException("User cannot follow himself");
            }
            this.followings.remove(user);
            this.followings.add(user);
            return this;
        }

        public Builder addPost(String text) {
            Objects.requireNonNull(text);
            this.posts.add(new Post(this.name, text));
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
