package transport.netty.server;

import coder.CommonDecoder;
import coder.CommonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import nacos.NacosServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.DefaultServiceProvider;
import serializer.CommonSerializer;
import transport.AbstractRpcServer;

import java.util.concurrent.TimeUnit;

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
                    .channel(NioServerSocketChannel.class) // IO模型
                    .handler(new LoggingHandler(LogLevel.INFO)) // 打印日志
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        // 责任链
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS)) // reader超时 触发心跳
                                    .addLast(new CommonEncoder(serializer)) // response 对象 -> 字节流
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
            // 绑定端口,调用 sync 方法阻塞知道绑定完成
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            // 阻塞等待直到服务器Channel关闭 (closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法)
            future.channel().closeFuture().sync();
            // serverBootstrap.bind(host, port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            // 优雅关闭相关线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
