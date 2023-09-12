package com.keepcode.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory storage that uses a {@link ConcurrentHashMap} to store data where each key maps to a list of values.
 * The maximum number of keys and the maximum capacity of each value list can be configured.
 *
 * @param <K> The type of keys stored in the map.
 * @param <V> The type of values stored in the lists associated with keys.
 */
@Component
public class InMemoryMapListStorage<K, V> {

    private final Map<K, List<V>> storage;
    private final int keyCapacity;
    private final int valueCapacity;

    public InMemoryMapListStorage(
            @Value("${storage.key-capacity}") int keyCapacity,
            @Value("${storage.value-capacity}") int valueCapacity
    ) {
        this.keyCapacity = keyCapacity;
        this.valueCapacity = valueCapacity;
        this.storage = new ConcurrentHashMap<>(keyCapacity);
    }

    /**
     * Clears the current storage and replaces it with new data. If the number of keys or the capacity of
     * any list exceeds the configured limits, excess data will be ignored.
     *
     * @param replaceData The data to replace the current storage with.
     */
    public void clearAndReplaceStoredData(Map<K, List<V>> replaceData) {
        Map<K, List<V>> replacedStorage = new ConcurrentHashMap<>(keyCapacity);
        replaceData.forEach((key, values) -> {
            if (!replacedStorage.containsKey(key) && replacedStorage.keySet().size() >= keyCapacity - 1) {
                return;
            }
            if (replacedStorage.containsKey(key)) {
                List<V> replacesValues = replacedStorage.get(key);
                int availableCapacity = valueCapacity - replacesValues.size();
                if (availableCapacity > 0) {
                    int elementsToAdd = Math.min(availableCapacity, values.size());

                    replacesValues.addAll(values.subList(0, elementsToAdd));
                }
                return;
            }
            if (values.size() <= valueCapacity) {
                replacedStorage.put(key, values);
            }
        });

        storage.clear();
        storage.putAll(replacedStorage);
    }

    public List<V> get(K key) {
        return storage.get(key);
    }

    public Map<K, List<V>> getStorage() {
        return Collections.unmodifiableMap(storage);
    }
}
