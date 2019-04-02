package com.dcis.netboot.nettyconfig;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Reverie
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private Map firpath;
    private static final String GET_FLAG="?";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {

        if (msg instanceof FullHttpRequest) {

            // 请求，解码器将请求转换成HttpRequest对象
            FullHttpRequest request = (FullHttpRequest) msg;
            actReq(request);
            if(request.content().isReadable()){
                String json=request.content().toString(Charset.forName("Utf-8"));
            }

            // 获取请求参数
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            String name = "netty";

            if(queryStringDecoder.parameters().get("name") != null) {
                name = queryStringDecoder.parameters().get("name").get(0);
            }

            // 响应HTML
            String responseHtml = "<html><body>Hello, " + name + "</body></html>";
            byte[] responseBytes = responseHtml.getBytes("UTF-8");
            int contentLength = responseBytes.length;

            // 构造FullHttpResponse对象，FullHttpResponse包含message body
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
            response.headers().set("Content-Type", "text/html; charset=utf-8");
            response.headers().set("Content-Length", Integer.toString(contentLength));

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
     
    public void setFirpath(Map map){
        this.firpath=map;
    }


    private  void actReq(FullHttpRequest request){
        String uri=request.uri();

        NettyControlllerModel controlllerModel=null;
        if(uri.contains(GET_FLAG)){
            String url=uri.substring(0,uri.indexOf(GET_FLAG));
          if(firpath.containsKey(url)){
              controlllerModel= (NettyControlllerModel) firpath.get(url);
          }else {
              if(firpath.containsKey(uri)){
                  controlllerModel= (NettyControlllerModel) firpath.get(uri);
              }
          }
            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
          if(controlllerModel==null){
              response.setStatus(HttpResponseStatus.NOT_FOUND);
          }else {
            invokeNamedMethod(request,response,controlllerModel);
          }
        }
    }

    private void  invokeNamedMethod(FullHttpRequest request,FullHttpResponse response,NettyControlllerModel controlllerModel){
        controlllerModel.getMethod().getTypeParameters();
        Class<?>[] types=controlllerModel.getMethod().getParameterTypes();
        List<Object> params = new ArrayList<Object>(4);


    }








}
