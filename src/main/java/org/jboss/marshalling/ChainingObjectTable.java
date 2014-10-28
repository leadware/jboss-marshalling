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

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.StreamCorruptedException;

/**
 * An object table that multiplexes up to 256 class tables.  The protocol works by prepending the custom object table
 * with an identifier byte.
 */
public class ChainingObjectTable implements ObjectTable {
    private static Pair<ObjectTable, Writer> pair(final ObjectTable objectTable, final Writer writer) {
        return Pair.create(objectTable, writer);
    }

    private final List<Pair<ObjectTable, Writer>> writers;
    private final ObjectTable[] readers;

    /**
     * Construct a new instance.  The given array may be sparse, but it may not be more than
     * 256 elements in length.  Object tables are checked in order of increasing array index.
     *
     * @param objectTables the object tables to delegate to
     */
    public ChainingObjectTable(final ObjectTable[] objectTables) {
        if (objectTables == null) {
            throw new NullPointerException("objectTables is null");
        }
        readers = objectTables.clone();
        if (readers.length > 256) {
            throw new IllegalArgumentException("Object table array is too long (limit is 256 elements)");
        }
        writers = new ArrayList<Pair<ObjectTable, Writer>>();
        for (int i = 0; i < readers.length; i++) {
            final ObjectTable objectTable = readers[i];
            if (objectTable != null) {
                final int idx = i;
                writers.add(pair(objectTable, new Writer() {
                    public void writeObject(final Marshaller marshaller, final Object obj) throws IOException {
                        marshaller.writeByte(idx);
                        objectTable.getObjectWriter(obj).writeObject(marshaller, obj);
                    }
                }));
            }
        }
    }

    /** {@inheritDoc} */
    public Writer getObjectWriter(final Object obj) throws IOException {
        for (Pair<ObjectTable, Writer> entry : writers) {
            final ObjectTable table = entry.getA();
            final Writer writer = entry.getB();
            if (table.getObjectWriter(obj) != null) {
                return writer;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public Object readObject(final Unmarshaller unmarshaller) throws IOException, ClassNotFoundException {
        final int v = unmarshaller.readByte() & 0xff;
        final ObjectTable table = readers[v];
        if (table == null) {
            throw new StreamCorruptedException(String.format("Unknown object table ID %02x encountered", Integer.valueOf(v)));
        }
        return table.readObject(unmarshaller);
    }
}