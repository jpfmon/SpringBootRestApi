package com.montojo.restapi.repository.impl;

import com.montojo.restapi.repository.UsernamesCache;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class UsernamesCacheImpl implements UsernamesCache {

    private final Set<String> usernamesCache;

    public UsernamesCacheImpl() {
        this.usernamesCache = new HashSet<>();
    }

    public void addUsernameToCache(String username) {
        usernamesCache.add(username);
    }

    public int cacheSize() {
        return usernamesCache.size();
    }

    public boolean isUsernameInCache(String username) {
        return usernamesCache.contains(username);
    }

    public void removeUsernameFromCache(String username) {
        usernamesCache.remove(username);
    }
}
