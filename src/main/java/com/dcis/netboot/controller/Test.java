package com.dcis.netboot.controller;

import com.dcis.netboot.nettyconfig.NettyMapping;
import com.dcis.netboot.nettyconfig.NettyParams;
import com.dcis.netboot.nettyconfig.NettyRest;
import com.dcis.netboot.nettyconfig.RequestBody;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;


@NettyRest
@NettyMapping(value="/testApi")
public class Test {
    @NettyMapping(value="/post")
    public String post(FullHttpRequest request, @RequestBody Map map, NettyParams params, FullHttpResponse response){
        System.out.println(map);
        System.out.println(params);
        return "113132";
    }
}
