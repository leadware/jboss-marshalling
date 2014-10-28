/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.marshalling.util;

/*
 * #%L
 * JBoss Marshalling API (Patch for JBossAS 7.1.1.Final)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2014 Leadware
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;

/**
 * An efficient object map whose keys are objects and whose values are {@code int}s.
 */
public final class IntMap<T> implements Cloneable {
    private int[] values;
    private Object[] keys;
    private int count;
    private int resizeCount;

    /**
     * Construct a new instance with the given initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor the load factor
     */
    public IntMap(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be > 0");
        }
        if (loadFactor <= 0.0f || loadFactor >= 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0.0 and < 1.0");
        }
        if (initialCapacity < 16) {
            initialCapacity = 16;
        } else {
            // round up
            final int c = Integer.highestOneBit(initialCapacity) - 1;
            initialCapacity = Integer.highestOneBit(initialCapacity + c);
        }
        keys = new Object[initialCapacity];
        values = new int[initialCapacity];
        resizeCount = (int) ((double) initialCapacity * (double) loadFactor);
    }

    /**
     * Clone this map.
     *
     * @return a cloned map
     */
    @SuppressWarnings({ "unchecked" })
    public IntMap<T> clone() {
        try {
            final IntMap<T> clone = (IntMap<T>) super.clone();
            clone.values = values.clone();
            clone.keys = keys.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

    /**
     * Construct a new instance with the given load factor and an initial capacity of 64.
     *
     * @param loadFactor the load factor
     */
    public IntMap(final float loadFactor) {
        this(64, loadFactor);
    }

    /**
     * Construct a new instance with the given initial capacity and a load factor of {@code 0.5}.
     *
     * @param initialCapacity the initial capacity
     */
    public IntMap(final int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    /**
     * Construct a new instance with an initial capacity of 64 and a load factor of {@code 0.5}.
     */
    public IntMap() {
        this(0.5f);
    }

    /**
     * Get a value from the map.
     *
     * @param key the key
     * @param defVal the value to return if the key is not found
     * @return the map value at the given key, or the value of {@code defVal} if it's not found
     */
    public int get(T key, int defVal) {
        final Object[] keys = this.keys;
        final int mask = keys.length - 1;
        int hc = key.hashCode() & mask;
        Object v;
        for (;;) {
            v = keys[hc];
            if (key.equals(v)) {
                return values[hc];
            }
            if (v == null) {
                // not found
                return defVal;
            }
            hc = (hc + 1) & mask;
        }
    }

    /**
     * Put a value into the map.  Any previous mapping is discarded silently.
     *
     * @param key the key
     * @param value the value to store
     */
    public void put(T key, int value) {
        final Object[] keys = this.keys;
        final int mask = keys.length - 1;
        final int[] values = this.values;
        Object v;
        int hc = key.hashCode() & mask;
        for (int idx = hc;; idx = hc++ & mask) {
            v = keys[idx];
            if (v == null) {
                keys[idx] = key;
                values[idx] = value;
                if (++count > resizeCount) {
                    resize();
                }
                return;
            }
            if (key.equals(v)) {
                values[idx] = value;
                return;
            }
        }
    }

    private void resize() {
        final Object[] oldKeys = keys;
        final int oldsize = oldKeys.length;
        final int[] oldValues = values;
        if (oldsize >= 0x40000000) {
            throw new IllegalStateException("Table full");
        }
        final int newsize = oldsize << 1;
        final int mask = newsize - 1;
        final Object[] newKeys = new Object[newsize];
        final int[] newValues = new int[newsize];
        keys = newKeys;
        values = newValues;
        if ((resizeCount <<= 1) == 0) {
            resizeCount = Integer.MAX_VALUE;
        }
        for (int oi = 0; oi < oldsize; oi ++) {
            final Object key = oldKeys[oi];
            if (key != null) {
                int ni = key.hashCode() & mask;
                for (;;) {
                    final Object v = newKeys[ni];
                    if (v == null) {
                        // found
                        newKeys[ni] = key;
                        newValues[ni] = oldValues[oi];
                        break;
                    }
                    ni = (ni + 1) & mask;
                }
            }
        }
    }

    public void clear() {
        Arrays.fill(keys, null);
        count = 0;
    }

    /**
     * Get a string summary representation of this map.
     *
     * @return a string representation
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Map length = ").append(keys.length).append(", count = ").append(count).append(", resize count = ").append(resizeCount).append('\n');
        for (int i = 0; i < keys.length; i ++) {
            builder.append('[').append(i).append("] = ");
            if (keys[i] != null) {
                final int hc = keys[i].hashCode();
                builder.append("{ ").append(keys[i]).append(" (hash ").append(hc).append(", modulus ").append(hc % keys.length).append(") => ").append(values[i]).append(" }");
            } else {
                builder.append("(blank)");
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}