/*
 * Copyright (C) 2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniMethod;

import static org.fusesource.hawtjni.runtime.ArgFlag.BY_VALUE;
import static org.fusesource.hawtjni.runtime.ArgFlag.NO_OUT;
import static org.fusesource.hawtjni.runtime.ClassFlag.CPP;
import static org.fusesource.hawtjni.runtime.MethodFlag.*;

/**
 * Provides a java interface to the C++ leveldb::WriteBatch class.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class NativeWriteBatch extends NativeObject {

    @JniClass(name="leveldb::WriteBatch", flags={CPP})
    private static class WriteBatchJNI {
        static {
            NativeDB.LIBRARY.load();
        }

        @JniMethod(flags={CPP_NEW})
        public static final native long create();
        @JniMethod(flags={CPP_DELETE})
        public static final native void delete(
                long self);

        @JniMethod(flags={CPP_METHOD})
        static final native void Put(
                long self,
                @JniArg(flags={BY_VALUE, NO_OUT}) NativeSlice key,
                @JniArg(flags={BY_VALUE, NO_OUT}) NativeSlice value
                );

        @JniMethod(flags={CPP_METHOD})
        static final native void Delete(
                long self,
                @JniArg(flags={BY_VALUE, NO_OUT}) NativeSlice key
                );

        @JniMethod(flags={CPP_METHOD})
        static final native void Clear(
                long self
                );

    }

    public NativeWriteBatch() {
        super(WriteBatchJNI.create());
    }

    public void delete() {
        assertAllocated();
        WriteBatchJNI.delete(self);
        self = 0;
    }

    public void put(byte[] key, byte[] value) {
        NativeDB.checkArgNotNull(key, "key");
        NativeDB.checkArgNotNull(value, "value");
        NativeBuffer keyBuffer = new NativeBuffer(key);
        try {
            NativeBuffer valueBuffer = new NativeBuffer(value);
            try {
                put(keyBuffer, valueBuffer);
            } finally {
                valueBuffer.delete();
            }
        } finally {
            keyBuffer.delete();
        }
    }

    private void put(NativeBuffer keyBuffer, NativeBuffer valueBuffer) {
        put(new NativeSlice(keyBuffer), new NativeSlice(valueBuffer));
    }

    private void put(NativeSlice keySlice, NativeSlice valueSlice) {
        assertAllocated();
        WriteBatchJNI.Put(self, keySlice, valueSlice);
    }


    public void delete(byte[] key) {
        NativeDB.checkArgNotNull(key, "key");
        NativeBuffer keyBuffer = new NativeBuffer(key);
        try {
            delete(keyBuffer);
        } finally {
            keyBuffer.delete();
        }
    }

    private void delete(NativeBuffer keyBuffer) {
        delete(new NativeSlice(keyBuffer));
    }

    private void delete(NativeSlice keySlice) {
        assertAllocated();
        WriteBatchJNI.Delete(self, keySlice);
    }

    public void clear() {
        assertAllocated();
        WriteBatchJNI.Clear(self);
    }

}