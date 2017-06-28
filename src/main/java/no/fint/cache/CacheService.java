package no.fint.cache;

import lombok.extern.slf4j.Slf4j;
import no.fint.cache.model.CacheObject;
import no.fint.cache.utils.CacheUri;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class CacheService<T> {

    private Map<String, Cache<T>> caches = new HashMap<>();

    public Set<String> getKeys() {
        return caches.keySet();
    }

    public Cache<T> createCache(String orgId) {
        FintCache<T> cache = new FintCache<>();
        caches.put(CacheUri.create(orgId, getModel()),cache);
        return cache;
    }

    public void put(String orgId, FintCache<T> cache) {
        caches.put(CacheUri.create(orgId, getModel()), cache);
    }

    @SuppressWarnings("unchecked")
    public long getLastUpdated(String orgId) {
        FintCache<T> fintCache = (FintCache) caches.get(CacheUri.create(orgId, getModel()));
        return fintCache.getLastUpdated();
    }

    public Optional<Cache<T>> getCache(String orgId) {
        return Optional.ofNullable(caches.get(CacheUri.create(orgId, getModel())));
    }

    public List<T> getAll(String orgId) {
        Optional<Cache<T>> cache = getCache(orgId);
        if (cache.isPresent()) {
            List<CacheObject<T>> cacheObjects = cache.get().get();
            return cacheObjects.stream().map(CacheObject::getObject).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public List<T> getAll(String orgId, long sinceTimestamp) {
        Optional<Cache<T>> cache = getCache(orgId);
        if (cache.isPresent()) {
            List<CacheObject<T>> cacheObjects = cache.get().getSince(sinceTimestamp);
            return cacheObjects.stream().map(CacheObject::getObject).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void update(String orgId, List<T> objects) {
        Optional<Cache<T>> cache = getCache(orgId);
        cache.ifPresent(c -> c.update(objects));
    }

    public void add(String orgId, List<T> objects) {
        Optional<Cache<T>> cache = getCache(orgId);
        cache.ifPresent(c -> c.add(objects));
    }

    public void flush(String orgId) {
        Optional<Cache<T>> cache = getCache(orgId);
        cache.ifPresent(Cache::flush);
    }

    public void remove(String orgId) {
        Optional<Cache<T>> cache = getCache(orgId);
        cache.ifPresent(c -> {
            c.flush();
            caches.remove(orgId);
        });
    }

    protected abstract String getModel();
}
