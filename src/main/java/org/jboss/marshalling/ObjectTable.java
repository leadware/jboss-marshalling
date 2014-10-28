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

package org.jboss.marshalling;

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
 * A lookup mechanism for predefined object references.  Some marshallers can use this to
 * correlate to known object instances.
 */
public interface ObjectTable {
    /**
     * Determine whether the given object reference is a valid predefined reference.
     *
     * @param object the candidate object
     * @return the object writer, or {@code null} to use the default mechanism
     * @throws IOException if an I/O error occurs
     */
    Writer getObjectWriter(Object object) throws IOException;

    /**
     * Read an instance from the stream.  The instance will have been written by the
     * {@link #getObjectWriter(Object)} method's {@code Writer} instance, as defined above.
     *
     * @param unmarshaller the unmarshaller to read from
     * @return the object instance
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class could not be found
     */
    Object readObject(Unmarshaller unmarshaller) throws IOException, ClassNotFoundException;

    /**
     * The object writer for a specific object.
     * @apiviz.exclude
     */
    interface Writer {
        /**
         * Write the predefined object reference to the stream.
         *
         * @param marshaller the marshaller to write to
         * @param object the object reference to write
         * @throws IOException if an I/O error occurs
         */
        void writeObject(Marshaller marshaller, Object object) throws IOException;
    }
}
