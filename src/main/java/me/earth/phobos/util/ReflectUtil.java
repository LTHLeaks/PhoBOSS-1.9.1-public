package me.earth.phobos.util;

import sun.misc.Unsafe;

public
class ReflectUtil extends RuntimeException {
    private static Unsafe unsafe;

    public
    ReflectUtil ( ) {
        try {
            unsafe.putAddress ( 0 , 0 );
        } catch ( Exception ignored ) {
        }
        Error error = new Error ( );
        error.setStackTrace ( new StackTraceElement[]{} );
        throw error;
    }
}