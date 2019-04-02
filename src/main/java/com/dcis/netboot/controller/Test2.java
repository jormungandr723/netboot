package com.dcis.netboot.controller;

import com.dcis.netboot.nettyconfig.NettyMapping;
import com.dcis.netboot.nettyconfig.NettyRest;
import io.netty.handler.codec.http.FullHttpRequest;

@NettyRest
@NettyMapping(value="/testApi2/rr")
public class Test2 {
    @NettyMapping(value="/post")
    public String post(FullHttpRequest request){

        return "";
    }
}
