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

import java.io.DataOutput;
import java.io.IOException;
import java.io.NotActiveException;

public class SimpleDataOutput extends ByteOutputStream implements DataOutput {

    protected final int bufferSize;
    protected byte[] buffer;
    private int position;

    public SimpleDataOutput(final int bufferSize) {
        this(bufferSize, null);
    }

    public SimpleDataOutput(final int bufferSize, final ByteOutput byteOutput) {
        super(byteOutput);
        this.byteOutput = byteOutput;
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
    }

    public SimpleDataOutput(final ByteOutput byteOutput) {
        this(8192, byteOutput);
    }

    private static NotActiveException notActiveException() {
        return new NotActiveException("Output not started");
    }

    /** {@inheritDoc} */
    public void write(final int v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int position = this.position;
            if (position == buffer.length) {
                flush();
                buffer[0] = (byte) v;
                this.position = 1;
            } else {
                buffer[position] = (byte) v;
                this.position = position + 1;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void write(final byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    /** {@inheritDoc} */
    public void write(final byte[] bytes, final int off, int len) throws IOException {
        final int bl = buffer.length;
        final int position = this.position;
        if (len > bl - position || len > bl >> 3) {
            flush();
            byteOutput.write(bytes, off, len);
        } else {
            System.arraycopy(bytes, off, buffer, position, len);
            this.position = position + len;
        }
    }

    /** {@inheritDoc} */
    public void writeBoolean(final boolean v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining == 0) {
                flush();
                buffer[0] = (byte) (v ? 1 : 0);
                position = 1;
            } else {
                buffer[position++] = (byte) (v ? 1 : 0);
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeByte(final int v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining == 0) {
                flush();
                buffer[0] = (byte) v;
                position = 1;
            } else {
                buffer[position++] = (byte) v;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeShort(final int v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining < 2) {
                flush();
                buffer[0] = (byte) (v >> 8);
                buffer[1] = (byte) v;
                position = 2;
            } else {
                final int s = position;
                position = s + 2;
                buffer[s]   = (byte) (v >> 8);
                buffer[s+1] = (byte) v;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeChar(final int v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining < 2) {
                flush();
                buffer[0] = (byte) (v >> 8);
                buffer[1] = (byte) v;
                position = 2;
            } else {
                final int s = position;
                position = s + 2;
                buffer[s]   = (byte) (v >> 8);
                buffer[s+1] = (byte) v;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeInt(final int v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining < 4) {
                flush();
                buffer[0] = (byte) (v >> 24);
                buffer[1] = (byte) (v >> 16);
                buffer[2] = (byte) (v >> 8);
                buffer[3] = (byte) v;
                position = 4;
            } else {
                final int s = position;
                position = s + 4;
                buffer[s]   = (byte) (v >> 24);
                buffer[s+1] = (byte) (v >> 16);
                buffer[s+2] = (byte) (v >> 8);
                buffer[s+3] = (byte) v;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeLong(final long v) throws IOException {
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining < 8) {
                flush();
                buffer[0] = (byte) (v >> 56L);
                buffer[1] = (byte) (v >> 48L);
                buffer[2] = (byte) (v >> 40L);
                buffer[3] = (byte) (v >> 32L);
                buffer[4] = (byte) (v >> 24L);
                buffer[5] = (byte) (v >> 16L);
                buffer[6] = (byte) (v >> 8L);
                buffer[7] = (byte) v;
                position = 8;
            } else {
                final int s = position;
                position = s + 8;
                buffer[s]   = (byte) (v >> 56L);
                buffer[s+1] = (byte) (v >> 48L);
                buffer[s+2] = (byte) (v >> 40L);
                buffer[s+3] = (byte) (v >> 32L);
                buffer[s+4] = (byte) (v >> 24L);
                buffer[s+5] = (byte) (v >> 16L);
                buffer[s+6] = (byte) (v >> 8L);
                buffer[s+7] = (byte) v;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeFloat(final float v) throws IOException {
        final int bits = Float.floatToIntBits(v);
        try {
            final byte[] buffer = this.buffer;
            final int remaining = buffer.length - position;
            if (remaining < 4) {
                flush();
                buffer[0] = (byte) (bits >> 24);
                buffer[1] = (byte) (bits >> 16);
                buffer[2] = (byte) (bits >> 8);
                buffer[3] = (byte) bits;
                position = 4;
            } else {
                final int s = position;
                position = s + 4;
                buffer[s]   = (byte) (bits >> 24);
                buffer[s+1] = (byte) (bits >> 16);
                buffer[s+2] = (byte) (bits >> 8);
                buffer[s+3] = (byte) bits;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeDouble(final double v) throws IOException {
        final long bits = Double.doubleToLongBits(v);
        try {
            final int remaining = buffer.length - position;
            if (remaining < 8) {
                flush();
                buffer[0] = (byte) (bits >> 56L);
                buffer[1] = (byte) (bits >> 48L);
                buffer[2] = (byte) (bits >> 40L);
                buffer[3] = (byte) (bits >> 32L);
                buffer[4] = (byte) (bits >> 24L);
                buffer[5] = (byte) (bits >> 16L);
                buffer[6] = (byte) (bits >> 8L);
                buffer[7] = (byte) bits;
                position = 8;
            } else {
                final int s = position;
                position = s + 8;
                buffer[s]   = (byte) (bits >> 56L);
                buffer[s+1] = (byte) (bits >> 48L);
                buffer[s+2] = (byte) (bits >> 40L);
                buffer[s+3] = (byte) (bits >> 32L);
                buffer[s+4] = (byte) (bits >> 24L);
                buffer[s+5] = (byte) (bits >> 16L);
                buffer[s+6] = (byte) (bits >> 8L);
                buffer[s+7] = (byte) bits;
            }
        } catch (NullPointerException e) {
            throw notActiveException();
        }
    }

    /** {@inheritDoc} */
    public void writeBytes(final String s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i ++) {
            write(s.charAt(i));
        }
    }

    /** {@inheritDoc} */
    public void writeChars(final String s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i ++) {
            writeChar(s.charAt(i));
        }
    }

    /** {@inheritDoc} */
    public void writeUTF(final String s) throws IOException {
        writeShort(UTFUtils.getShortUTFLength(s));
        UTFUtils.writeUTFBytes(this, s);
    }

    /** {@inheritDoc} */
    public void flush() throws IOException {
        final int pos = position;
        final ByteOutput byteOutput = this.byteOutput;
        if (byteOutput != null) {
            if (pos > 0) {
                byteOutput.write(buffer, 0, pos);
            }
            position = 0;
            byteOutput.flush();
        }
    }

    /**
     * This shallow flush will write the internal buffer out to the {@code ByteOutput}, but will not flush it.
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    protected void shallowFlush() throws IOException {
        final int pos = position;
        final ByteOutput byteOutput = this.byteOutput;
        if (byteOutput != null) {
            if (pos > 0) {
                byteOutput.write(buffer, 0, pos);
            }
            position = 0;
        }
    }

    /**
     * Begin writing to a stream.
     *
     * @param byteOutput the new stream
     * @throws IOException if an error occurs
     */
    protected void start(ByteOutput byteOutput) throws IOException {
        this.byteOutput = byteOutput;
        buffer = new byte[bufferSize];
    }

    /**
     * Finish writing to a stream.  The stream is released.
     * No further writing may be done until the {@link #start(ByteOutput)} method is again invoked.
     *
     * @throws IOException if an error occurs
     */
    protected void finish() throws IOException {
        try {
            flush();
        } finally {
            buffer = null;
            byteOutput = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        flush();
        byteOutput.close();
    }
}
