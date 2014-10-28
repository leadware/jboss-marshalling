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

/**
 * An interface which allows extending a cloner to types that it would not otherwise support.
 */
public interface CloneTable {

    /**
     * Attempt to clone the given object.  If no clone can be made or acquired from this table, return {@code null}.
     *
     * @param original the original
     * @param objectCloner the object cloner
     * @param classCloner the class cloner
     * @return the clone or {@code null} if none can be acquired
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class is not found
     */
    Object clone(Object original, ObjectCloner objectCloner, ClassCloner classCloner) throws IOException, ClassNotFoundException;

    /**
     * A null clone table.
     */
    CloneTable NULL = new CloneTable() {
        public Object clone(final Object original, final ObjectCloner objectCloner, final ClassCloner classCloner) throws IOException, ClassNotFoundException {
            return null;
        }
    };
}
