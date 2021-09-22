package transport.netty.server;

import coder.CommonDecoder;
import coder.CommonEncoder;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import nacos.NacosServiceRegistry;
import nacos.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.DefaultServiceProvider;
import registry.ServiceProvider;
import serializer.CommonSerializer;
import serializer.JsonSerializer;
import serializer.KryoSerializer;
import transport.AbstractRpcServer;
import transport.RpcServer;

import java.net.InetSocketAddress;

/**
 * Created by GBK on 2021/9/16
 * Use...
 */
public class NettyRpcServer extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    public NettyRpcServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new DefaultServiceProvider();
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        // 责任链
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer)) // response 对象 -> 字节流
                                    .addLast(new CommonDecoder()) // request 字节流 -> 对象
                                    .addLast(new NettyServerHandler());
                            /*
                                pipeline: encoder - decoder - handler
                                inbound: decoder -> handler
                                outbound: encoder
                                流程： 收到request -> decoder -> request字节流 -> handler
                                 -> response对象 -> encoder -> response字节流

                                 注意：ctx.writeAndFlush()和ctx.channel().writeAndFlush()是有区别的
                                 ctx.writeAndFlush()从当前节点往前查找out性质的handler
                                 ctx.channel().writeAndFlush()从链表结尾开始往前查找out性质的handler
                             */
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
