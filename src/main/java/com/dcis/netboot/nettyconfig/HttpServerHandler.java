package com.dcis.netboot.nettyconfig;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import sun.reflect.annotation.AnnotationType;


import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Reverie
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private Map firpath;
    private static final String GET_FLAG="?";
    private static  final String UTF8="UTF-8";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException, InvocationTargetException, IllegalAccessException {

        if (msg instanceof FullHttpRequest) {

            // 请求，解码器将请求转换成HttpRequest对象
            FullHttpRequest request = (FullHttpRequest) msg;
            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
            actReq(request,response);
//            if(request.content().isReadable()){
//                String json=request.content().toString(Charset.forName("Utf-8"));
//            }
//
//            // 获取请求参数
//            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
//            String name = "netty";
//
//            if(queryStringDecoder.parameters().get("name") != null) {
//                name = queryStringDecoder.parameters().get("name").get(0);
//            }
//
//            // 响应HTML
//            String responseHtml = "<html><body>Hello, " + name + "</body></html>";
//            byte[] responseBytes = responseHtml.getBytes("UTF-8");
//            int contentLength = responseBytes.length;
//
//            // 构造FullHttpResponse对象，FullHttpResponse包含message body
//            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
//            response.headers().set("Content-Type", "text/html; charset=utf-8");
//            response.headers().set("Content-Length", Integer.toString(contentLength));

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


    private  void actReq(FullHttpRequest request,FullHttpResponse response) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
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

          if(controlllerModel==null){
              response.headers().set("Content-Type", "application/json; charset=utf-8");
              response.setStatus(HttpResponseStatus.NOT_FOUND);
              response.headers().set("Content-Length", 0);
          }else {
            invokeNamedMethod(request,response,controlllerModel);
          }
        }
    }

    private void  invokeNamedMethod(FullHttpRequest request,FullHttpResponse response,
                                    NettyControlllerModel controlllerModel) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
        List params=paramsHandle(request,controlllerModel,response);
        String reJson="";
      if(void.class.equals(controlllerModel.getMethod().getReturnType())){
          controlllerModel.getMethod().invoke(controlllerModel.getObj(),
                  params.toArray(new Object[params.size()]));
      }else {
          Object obj=controlllerModel.getMethod().invoke(controlllerModel.getObj(),
                  params.toArray(new Object[params.size()]));
          reJson=JSON.toJSONString(obj);
      }
        byte[] responseBytes = reJson.getBytes(UTF8);
        int contentLength = responseBytes.length;
        response.content().writeBytes(responseBytes);
        response.headers().set("Content-Type", "application/json; charset=utf-8");
        response.headers().set("Content-Length", Integer.toString(contentLength));

    }

    private Object transCusObj(FullHttpRequest request,Class beencls){
        if(request.content().isReadable()){
            String json=request.content().toString(Charset.forName(UTF8));
            return   JSON.parseObject(json,beencls);
        }else {
            return null;
        }

    }


    private List<Object> paramsHandle(FullHttpRequest request,NettyControlllerModel controlllerModel,FullHttpResponse response){
        controlllerModel.getMethod().getTypeParameters();
        Class<?>[] types=controlllerModel.getMethod().getParameterTypes();

        List<Object> params = new ArrayList<>(4);
        for (int j = 0; j < types.length; j++) {
            Class cls=types[j];
            if(cls.getName().equals(FullHttpRequest.class.getName())){

                params.add(request);

            }else
            if(cls.getName().equals(FullHttpResponse.class.getName())){
                params.add(response);

            }
            if(cls.equals(NettyParams.class)){
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> listMap=queryStringDecoder.parameters();
                NettyParams pa=JSON.parseObject(JSON.toJSON(listMap).toString(),NettyParams.class);

                params.add(pa);
            }else {
                Annotation[][] annotations=  controlllerModel.getMethod().getParameterAnnotations();
                if(annotations[j].length>0&&annotations[j][0]!=null&&annotations[j][0].annotationType().equals(RequestBody.class)){
                    Object obj=transCusObj(request,cls);
                    params.add(obj);
                }

            }
        }




        return params;
    }





}
