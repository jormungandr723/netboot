package com.dcis.netboot.nettyconfig;
import com.dcis.netboot.until.ClassUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Httpserver {
    private static Map fstPath;
    public static void start(Nettyconfig nettyconfig) throws InterruptedException, InstantiationException, IllegalAccessException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        loadController(nettyconfig);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)  {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            //聚合Request并设置最大值(防止出现多次进入处理类)
                            pipeline.addLast(new HttpObjectAggregator(65336));
                            HttpServerHandler httpServerHandler=new HttpServerHandler();
                            httpServerHandler.setFirpath(fstPath);
                            pipeline.addLast(httpServerHandler);
                        }
                    });
            //绑定端口
            ChannelFuture f = b.bind(nettyconfig.getPort()).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 装载controller
     */
    private static void loadController(Nettyconfig nettyconfig) throws IllegalAccessException, InstantiationException {
        //首先获取类
        String pkgName=nettyconfig.getControllerPackage();
        List<Class<?>> classes= ClassUtil.getAllClassByPackageName(pkgName);
        fstPath=new HashMap(classes.size());
        for (Class clz:classes) {
            Annotation annotationRest= clz.getDeclaredAnnotation(NettyRest.class);
            Object obj=clz.newInstance();
            if(annotationRest!=null){
                NettyMapping mapping= (NettyMapping)clz.getDeclaredAnnotation(NettyMapping.class);
                if(mapping!=null){
                 Method[] ms= clz.getDeclaredMethods();
                    for (Method m:ms) {
                        NettyMapping memapping=m.getDeclaredAnnotation(NettyMapping.class);
                            NettyControlllerModel nettyControlllerModel=new NettyControlllerModel();
                            nettyControlllerModel.setObj(obj);
                            nettyControlllerModel.setMethod(m);
                            //api地址寻址匹配表
                            fstPath.put(nettyconfig.getWebpath()+mapping.value()+memapping.value(),nettyControlllerModel);
                    }

                }
            }
        }
    }
    }

