/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
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

package org.jboss.marshalling.reflect;

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

import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumSet;
import static org.jboss.marshalling.reflect.ConcurrentReferenceHashMap.ReferenceType.WEAK;
import static org.jboss.marshalling.reflect.ConcurrentReferenceHashMap.ReferenceType.STRONG;
import static org.jboss.marshalling.reflect.ConcurrentReferenceHashMap.Option.IDENTITY_COMPARISONS;

/**
 * A registry for reflection information usable by serialization implementations.  Objects returned from this registry
 * can be used to invoke private methods without security checks, so it is important to be careful not to "leak" instances
 * out of secured implementations.
 */
public final class SerializableClassRegistry {
    private SerializableClassRegistry() {
    }

    private static final SerializableClassRegistry INSTANCE = new SerializableClassRegistry();

    private static final SerializablePermission PERMISSION = new SerializablePermission("allowSerializationReflection");

    /**
     * Get the serializable class registry instance, if allowed by the current security manager.  The caller must have
     * the {@code java.io.SerializablePermission} {@code "allowSerializationReflection"} in order to invoke this method.
     *
     * @return the registry
     * @throws SecurityException if the caller does not have sufficient privileges
     */
    public static SerializableClassRegistry getInstance() throws SecurityException {
        SecurityManager manager = System.getSecurityManager();
        if (manager != null) {
            manager.checkPermission(PERMISSION);
        }
        return INSTANCE;
    }

    static SerializableClassRegistry getInstanceUnchecked() {
        return INSTANCE;
    }

    private final ConcurrentReferenceHashMap<Class<?>, SerializableClass> cache = new ConcurrentReferenceHashMap<Class<?>, SerializableClass>(512, 0x0.Cp0f, 16, WEAK, STRONG, EnumSet.of(IDENTITY_COMPARISONS));

    /**
     * Look up serialization information for a class.  The resultant object will be cached.
     *
     * @param subject the class to look up
     * @return the serializable class information
     */
    public SerializableClass lookup(final Class<?> subject) {
        SerializableClass info = cache.get(subject);
        if (info != null) {
            return info;
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            info = AccessController.doPrivileged(new PrivilegedAction<SerializableClass>() {
                public SerializableClass run() {
                    return new SerializableClass(subject);
                }
            });
        } else {
            info = new SerializableClass(subject);
        }
        final SerializableClass old = cache.putIfAbsent(subject, info);
        return old != null ? old : info;
    }
}
