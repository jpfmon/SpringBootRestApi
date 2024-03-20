package com.montojo.restapi.repository;

public interface UsernamesCache {
    void addUsernameToCache(String username);
    int cacheSize();
    boolean isUsernameInCache(String username);
    void removeUsernameFromCache(String username);
}
