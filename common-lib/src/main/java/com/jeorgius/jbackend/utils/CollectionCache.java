package com.jeorgius.jbackend.utils;

import com.jeorgius.jbackend.config.CacheConfig;
import com.jeorgius.jbackend.dto.IdDto;
import org.springframework.cache.Cache;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

public class CollectionCache implements Cache {
    private final Cache cache;

    public CollectionCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        if (key instanceof Iterable) {
            Iterable<?> keys = (Iterable<?>) key;
            if (!isAllKeysPresentInCache(keys)) {
                return null;
            }

            Collection<Object> values = new ArrayList<>();
            for (Object singleKey : keys) {
                ValueWrapper value = cache.get(singleKey);
                if (value != null) {
                    values.add(value.get());
                }
            }

            return () -> values;
        }

        ValueWrapper result = cache.get(key);

        if (result != null && result.get() != null && cache.getName().startsWith(CacheConfig.RESPONSE_ENTITY_CACHE_PREFIX)) {
            return () -> ResponseEntity.of(Optional.ofNullable(result.get()));
        }

        return result;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (key instanceof Iterable) {
            return (T) Optional.ofNullable(get(key)).map(ValueWrapper::get).orElse(null);
        }
        return cache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (key instanceof Iterable) {
            return (T) get(key, Object.class);
        }
        return cache.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        value = getValue(value);
        if (value instanceof Iterable) {
            putIterable(key, (Iterable<?>) value);
        } else {
            cache.put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (value instanceof Iterable) {
            return () -> putIterable(key, (Iterable<?>) getValue(value));
        } else {
            return cache.putIfAbsent(key, getValue(value));
        }
    }

    @Override
    public void evict(Object key) {
        cache.evict(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private Iterable<?> putIterable(Object key, Iterable<?> iterableValues) {
        cache.put(key, iterableValues);
        iterableValues.forEach(iterableValue -> {
            if (iterableValue instanceof IdDto) {
                cache.put(((IdDto) iterableValue).getId(), iterableValue);
            }
        });
        return iterableValues;
    }

    private boolean isAllKeysPresentInCache(Iterable<?> keys) {
        return StreamSupport.stream(keys.spliterator(), false).allMatch(o -> get(o) != null);
    }

    private Object getValue(Object value) {
        if (value instanceof ResponseEntity) {
            return ((ResponseEntity) value).getBody();
        }
        return value;
    }
}

