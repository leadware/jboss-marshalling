/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
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

package org.jboss.marshalling.cloner;

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

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.IdentityHashMap;

/**
 * An object cloner which clones objects that implement {@link Cloneable}.
 */
class CloneableCloner implements ObjectCloner {
    private final CloneTable cloneTable;

    /**
     * Create a new instance.
     *
     * @param configuration the configuration
     */
    CloneableCloner(final ClonerConfiguration configuration) {
        final CloneTable cloneTable = configuration.getCloneTable();
        this.cloneTable = cloneTable == null ? CloneTable.NULL : cloneTable;
    }

    private static final Method CLONE = AccessController.doPrivileged(new PrivilegedAction<Method>() {
        public Method run() {
            final Method method;
            try {
                method = Object.class.getDeclaredMethod("clone");
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
            method.setAccessible(true);
            return method;
        }
    });

    /** {@inheritDoc} */
    public void reset() {
        synchronized (this) {
            clones.clear();
        }
    }

    private final IdentityHashMap<Object, Object> clones = new IdentityHashMap<Object, Object>();

    /** {@inheritDoc} */
    public Object clone(final Object orig) throws IOException, ClassNotFoundException {
        synchronized (this) {
            if (orig == null) {
                return null;
            }
            Object cached;
            if ((cached = clones.get(orig)) != null) {
                return cached;
            }
            if ((cached = cloneTable.clone(cached, this, ClassCloner.IDENTITY)) != null) {
                clones.put(orig, cached);
                return cached;
            }
            try {
                final Object clone = CLONE.invoke(orig);
                clones.put(orig, clone);
                return clone;
            } catch (IllegalAccessException e) {
                throw new InvalidClassException(orig.getClass().getName(), "Can't access clone() method: " + e);
            } catch (InvocationTargetException e) {
                throw new InvalidObjectException("Error invoking clone() method: " + e);
            }
        }
    }
}
