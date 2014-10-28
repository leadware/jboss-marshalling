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
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Serializable;

/**
 * A replacement serializer for an object class.
 */
public interface Externalizer extends Serializable {
    /**
     * Write the external representation of an object.  The object's class and the externalizer's class will
     * already have been written.
     *
     * @param subject the object to externalize
     * @param output the output
     * @throws IOException if an error occurs
     */
    void writeExternal(Object subject, ObjectOutput output) throws IOException;

    /**
     * Create an instance of a type.  The object may then be initialized from {@code input}, or that may be deferred
     * to the {@code readExternal()} method.  Instances may simply delegate the task to the given {@code Creator}.
     * Note that this method is called only on the leaf class, so externalizers for non-final classes that initialize
     * the instance from the stream need to be aware of this.
     *
     * @param subjectType the type of object to create
     * @param input the input
     * @param defaultCreator the configured creator
     * @return the new instance
     * @throws IOException if an error occurs
     * @throws ClassNotFoundException if a class could not be found during read
     */
    Object createExternal(Class<?> subjectType, ObjectInput input, Creator defaultCreator) throws IOException, ClassNotFoundException;

    /**
     * Read the external representation of an object.  The object will already be instantiated, but may be uninitialized, when
     * this method is called.
     *
     * @param subject the object to read
     * @param input the input
     * @throws IOException if an error occurs
     * @throws ClassNotFoundException if a class could not be found during read
     */
    void readExternal(Object subject, ObjectInput input) throws IOException, ClassNotFoundException;
}
