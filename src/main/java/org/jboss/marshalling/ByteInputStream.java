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

/**
 * An {@code InputStream} which implements {@code ByteInput} and reads bytes from another {@code ByteInput}.
 * Usually the {@link Marshalling#createByteInput(InputStream)} method should be used to create instances because
 * it can detect when the target already implements {@code ByteInput}.
 */
public class ByteInputStream extends SimpleByteInput {

    protected volatile ByteInput byteInput;

    /**
     * Create a new instance.
     *
     * @param byteInput the byte input to read from
     */
    public ByteInputStream(final ByteInput byteInput) {
        this.byteInput = byteInput;
    }

    /** {@inheritDoc} */
    public int read() throws IOException {
        return byteInput.read();
    }

    /** {@inheritDoc} */
    public int read(final byte[] b) throws IOException {
        return byteInput.read(b);
    }

    /** {@inheritDoc} */
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return byteInput.read(b, off, len);
    }

    /** {@inheritDoc} */
    public long skip(final long n) throws IOException {
        return byteInput.skip(n);
    }

    /** {@inheritDoc} */
    public int available() throws IOException {
        return byteInput.available();
    }

    /** {@inheritDoc} */
    public void close() throws IOException {
        byteInput.close();
    }
}
