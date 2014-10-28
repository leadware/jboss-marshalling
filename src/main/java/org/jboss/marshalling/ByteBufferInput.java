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
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * An {@code InputStream} which implements {@code ByteInput} and reads bytes from a {@code ByteBuffer}.
 */
public class ByteBufferInput extends InputStream implements ByteInput {

    private final ByteBuffer buffer;

    /**
     * Construct a new instance.
     *
     * @param buffer the buffer to read from
     */
    public ByteBufferInput(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /** {@inheritDoc} */
    public int read() throws IOException {
        try {
            return buffer.get() & 0xff;
        } catch (BufferUnderflowException e) {
            return -1;
        }
    }

    /** {@inheritDoc} */
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /** {@inheritDoc} */
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        final int rem = buffer.remaining();
        if (rem == 0) {
            return -1;
        }
        final int c = Math.min(len, rem);
        buffer.get(b, 0, c);
        return c;
    }

    /** {@inheritDoc} */
    public int available() throws IOException {
        return buffer.remaining();
    }

    /** {@inheritDoc} */
    public long skip(final long n) throws IOException {
        if (n > 0L) {
            final long c = Math.min((long) buffer.remaining(), n);
            buffer.position(buffer.position() + (int) c);
            return c;
        } else {
            return 0L;
        }
    }

    /** {@inheritDoc} */
    public void close() throws IOException {
    }
}
