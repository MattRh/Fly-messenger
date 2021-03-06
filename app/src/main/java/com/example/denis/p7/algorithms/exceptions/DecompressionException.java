package com.example.denis.p7.algorithms.exceptions;

/**
 * Exception of an unresolved mistake while doing the decompression.
 * <p>
 * Created by Sergey Malyutkin on 2017-11-05
 */
public class DecompressionException extends Exception {

    public DecompressionException() {
        super();
    }

    public DecompressionException(String message) {
        super(message);
    }

}
