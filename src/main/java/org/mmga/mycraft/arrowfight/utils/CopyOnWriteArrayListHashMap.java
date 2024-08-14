package org.mmga.mycraft.arrowfight.utils;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created On 2022/8/7 17:29
 *
 * @author wzp
 * @version 1.0.0
 */
public class CopyOnWriteArrayListHashMap<K, V> extends HashMap<K, CopyOnWriteArrayList<V>> {
    public void incr(K key, V value) {
        CopyOnWriteArrayList<V> list = super.getOrDefault(key, new CopyOnWriteArrayList<>());
        list.add(value);
        super.put(key, list);
    }

    public void decr(K key, V value) {
        CopyOnWriteArrayList<V> list = super.getOrDefault(key, new CopyOnWriteArrayList<>());
        list.remove(value);
        super.put(key, list);
    }
}
