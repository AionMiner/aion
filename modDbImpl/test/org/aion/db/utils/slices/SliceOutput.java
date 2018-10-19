/**
 * ***************************************************************************** Copyright (c)
 * 2017-2018 Aion foundation.
 *
 * <p>This file is part of the aion network project.
 *
 * <p>The aion network project is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 *
 * <p>The aion network project is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with the aion network
 * project source files. If not, see <https://www.gnu.org/licenses/>.
 *
 * <p>The aion network project leverages useful source code from other open source projects. We
 * greatly appreciate the effort that was invested in these projects and we thank the individual
 * contributors for their work. For provenance information and contributors please see
 * <https://github.com/aionnetwork/aion/wiki/Contributors>.
 *
 * <p>Contributors to the aion source files in decreasing order of code volume: Aion foundation.
 * <ether.camp> team through the ethereumJ library. Ether.Camp Inc. (US) team through Ethereum
 * Harmony. John Tromp through the Equihash solver. Samuel Neves through the BLAKE2 implementation.
 * Zcash project team. Bitcoinj team. H2 Group.
 * ****************************************************************************
 */
package org.aion.db.utils.slices;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class SliceOutput extends OutputStream implements DataOutput {
    /** Resets this stream to the initial position. */
    public abstract void reset();

    /** Returns the {@code writerIndex} of this buffer. */
    public abstract int size();

    /**
     * Returns the number of writable bytes which is equal to {@code (this.capacity -
     * this.writerIndex)}.
     */
    public abstract int writableBytes();

    /**
     * Returns {@code true} if and only if {@code (this.capacity - this.writerIndex)} is greater
     * than {@code 0}.
     */
    public abstract boolean isWritable();

    @Override
    public final void writeBoolean(boolean value) {
        writeByte(value ? 1 : 0);
    }

    @Override
    public final void write(int value) {
        writeByte(value);
    }

    /**
     * Sets the specified byte at the current {@code writerIndex} and increases the {@code
     * writerIndex} by {@code 1} in this buffer. The 24 high-order bits of the specified value are
     * ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 1}
     */
    @Override
    public abstract void writeByte(int value);

    /**
     * Sets the specified 16-bit short integer at the current {@code writerIndex} and increases the
     * {@code writerIndex} by {@code 2} in this buffer. The 16 high-order bits of the specified
     * value are ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 2}
     */
    @Override
    public abstract void writeShort(int value);

    /**
     * Sets the specified 32-bit integer at the current {@code writerIndex} and increases the {@code
     * writerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 4}
     */
    @Override
    public abstract void writeInt(int value);

    /**
     * Sets the specified 64-bit long integer at the current {@code writerIndex} and increases the
     * {@code writerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 8}
     */
    @Override
    public abstract void writeLong(long value);

    /**
     * Transfers the specified source buffer's data to this buffer starting at the current {@code
     * writerIndex} until the source buffer becomes unreadable, and increases the {@code
     * writerIndex} by the number of the transferred bytes. This method is basically same with
     * {@link #writeBytes(Slice, int, int)}, except that this method increases the {@code
     * readerIndex} of the source buffer by the number of the transferred bytes while {@link
     * #writeBytes(Slice, int, int)} does not.
     *
     * @throws IndexOutOfBoundsException if {@code source.readableBytes} is greater than {@code
     *     this.writableBytes}
     */
    public abstract void writeBytes(Slice source);

    /**
     * Transfers the specified source buffer's data to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes (=
     * {@code length}). This method is basically same with {@link #writeBytes(Slice, int, int)},
     * except that this method increases the {@code readerIndex} of the source buffer by the number
     * of the transferred bytes (= {@code length}) while {@link #writeBytes(Slice, int, int)} does
     * not.
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code
     *     this.writableBytes} or if {@code length} is greater then {@code source.readableBytes}
     */
    public abstract void writeBytes(SliceInput source, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes (=
     * {@code length}).
     *
     * @param sourceIndex the first index of the source
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code sourceIndex} is less than {@code
     *     0}, if {@code sourceIndex + length} is greater than {@code source.capacity}, or if {@code
     *     length} is greater than {@code this.writableBytes}
     */
    public abstract void writeBytes(Slice source, int sourceIndex, int length);

    @Override
    public final void write(byte[] source) throws IOException {
        writeBytes(source);
    }

    /**
     * Transfers the specified source array's data to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes (=
     * {@code source.length}).
     *
     * @throws IndexOutOfBoundsException if {@code source.length} is greater than {@code
     *     this.writableBytes}
     */
    public abstract void writeBytes(byte[] source);

    @Override
    public final void write(byte[] source, int sourceIndex, int length) {
        writeBytes(source, sourceIndex, length);
    }

    /**
     * Transfers the specified source array's data to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes (=
     * {@code length}).
     *
     * @param sourceIndex the first index of the source
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code sourceIndex} is less than {@code
     *     0}, if {@code sourceIndex + length} is greater than {@code source.length}, or if {@code
     *     length} is greater than {@code this.writableBytes}
     */
    public abstract void writeBytes(byte[] source, int sourceIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at the current {@code
     * writerIndex} until the source buffer's position reaches its limit, and increases the {@code
     * writerIndex} by the number of the transferred bytes.
     *
     * @throws IndexOutOfBoundsException if {@code source.remaining()} is greater than {@code
     *     this.writableBytes}
     */
    public abstract void writeBytes(ByteBuffer source);

    /**
     * Transfers the content of the specified stream to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes.
     *
     * @param length the number of bytes to transfer
     * @return the actual number of bytes read in from the specified stream
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code
     *     this.writableBytes}
     * @throws java.io.IOException if the specified stream threw an exception during I/O
     */
    public abstract int writeBytes(InputStream in, int length) throws IOException;

    /**
     * Transfers the content of the specified channel to this buffer starting at the current {@code
     * writerIndex} and increases the {@code writerIndex} by the number of the transferred bytes.
     *
     * @param length the maximum number of bytes to transfer
     * @return the actual number of bytes read in from the specified channel
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code
     *     this.writableBytes}
     * @throws java.io.IOException if the specified channel threw an exception during I/O
     */
    public abstract int writeBytes(ScatteringByteChannel in, int length) throws IOException;

    public abstract int writeBytes(FileChannel in, int position, int length) throws IOException;

    /**
     * Fills this buffer with <tt>NUL (0x00)</tt> starting at the current {@code writerIndex} and
     * increases the {@code writerIndex} by the specified {@code length}.
     *
     * @param length the number of <tt>NUL</tt>s to write to the buffer
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code
     *     this.writableBytes}
     */
    public abstract void writeZero(int length);

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content of the returned buffer
     * or this buffer affects each other's content while they maintain separate indexes and marks.
     * This method is identical to {@code buf.slice(buf.readerIndex(), buf.readableBytes())}. This
     * method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     */
    public abstract Slice slice();

    /**
     * Converts this buffer's readable bytes into a NIO buffer. The returned buffer might or might
     * not share the content with this buffer, while they have separate indexes and marks. This
     * method is identical to {@code buf.toByteBuffer(buf.readerIndex(), buf.readableBytes())}. This
     * method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     */
    public abstract ByteBuffer toByteBuffer();

    /**
     * Decodes this buffer's readable bytes into a string with the specified character set name.
     * This method is identical to {@code buf.toString(buf.readerIndex(), buf.readableBytes(),
     * charsetName)}. This method does not modify {@code readerIndex} or {@code writerIndex} of this
     * buffer.
     *
     * @throws java.nio.charset.UnsupportedCharsetException if the specified character set name is
     *     not supported by the current VM
     */
    public abstract String toString(Charset charset);

    //
    // Unsupported operations
    //

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeChar(int value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeFloat(float v) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeDouble(double v) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeChars(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeUTF(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void writeBytes(String s) {
        throw new UnsupportedOperationException();
    }
}
