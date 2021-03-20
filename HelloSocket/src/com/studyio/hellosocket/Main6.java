package com.studyio.hellosocket;

import org.w3c.dom.Node;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jiayq
 * @Date 2021-03-20
 */
public class Main6 {

    public static void main(String[] args) {
        System.out.println("请完善TestMap类，要求只实现get、put、remove、size四个方法");
        System.out.println("要求不能使用第三方包，不能使用JDK中Map实现类");
        System.out.println("请对完成的方法进行测试，在main方法中调用验证");
        TestMap<String ,String > map = new TestMap<>();
        map.put("x", "x");
        System.out.println(map.get("x"));
    }

    private static class TestMap<K, V> implements Map<K, V> {

        private volatile transient int size = 8;

        private volatile transient int index = 0;

        private volatile transient Node[] maps = new Node[size];

        private volatile transient Lock lock = new ReentrantLock();

        private class Node<K, V> {
            private K key;
            private V value;

            public Node(K key, V value) {
                this.key = key;
                this.value = value;
            }

            public K getKey() {
                return key;
            }

            public void setKey(K key) {
                this.key = key;
            }

            public V getValue() {
                return value;
            }

            public void setValue(V value) {
                this.value = value;
            }
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public V get(Object key) {
            try {
                lock.lock();
                for (Node node : maps) {
                    if (node.getKey().equals(key)) {
                        return (V) node.getValue();
                    }
                }
            } finally {
                lock.unlock();
            }
            return null;
        }

        @Override
        public V put(K key, V value) {
            try {
                lock.lock();
                if (maps.length >= size) {
                    Node<K, V>[] newMap = new Node[size * 2];
                    size *= 2;
                    for (int i = 0; i < maps.length; i++) {
                        newMap[i] = maps[i];
                    }
                    maps = newMap;
                }
                maps[index++] = new Node(key, value);
                return value;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V remove(Object key) {
            try {
                lock.lock();
                V value= null;
                for (int i = 0; i < maps.length; i++) {
                    if(maps[i] != null && maps[i].getKey().equals(key)) {
                        value = (V) maps[i].getValue();
                        for(int j = i;j < maps.length-1;j++) {
                            maps[j] = maps[j+1];
                        }
                        size--;
                        index--;
                        return value;
                    }
                }
            } finally {
                lock.unlock();
            }
            return null;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Set<K> keySet() {
            return null;
        }

        @Override
        public Collection<V> values() {
            return null;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return null;
        }
    }

}
