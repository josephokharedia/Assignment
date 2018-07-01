package com.okharedia.assignment.test;

import com.okharedia.assignment.application.internal.DefaultUserActivityService;
import com.okharedia.assignment.infrastructure.persistence.InMemoryUserRepository;

import java.lang.reflect.Field;

public class ApplicationTest {

    protected void resetSingletons() {
        try {
            Field instance = InMemoryUserRepository.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);

            instance = DefaultUserActivityService.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
