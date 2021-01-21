package com.xhtech.hermes.core.netty.exception;

import java.io.IOException;

public class ProtoDecodeException extends IOException {

    public ProtoDecodeException(String message)  {
        super(message);
    }
}
