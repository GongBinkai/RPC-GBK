package transport.netty.client;

import coder.CommonDecoder;
import coder.CommonEncoder;
import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import nacos.NacosServiceRegistry;
import nacos.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;
import serializer.JsonSerializer;
import serializer.KryoSerializer;
import transport.RpcClient;

import java.net.InetSocketAddress;

/**
 * Created by GBK on 2021/9/16
 * Use...
 */
public class NettyRpcClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    private static final Bootstrap bootstrap;
    private final ServiceRegistry serviceRegistry;
    private final CommonSerializer serializer;

    public NettyRpcClient() {
        this.serviceRegistry = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonDecoder()) // response 字节流 -> 对象
                                .addLast(new CommonEncoder(new KryoSerializer())) // request 对象 -> 字节流
                                .addLast(new NettyClientHandler());
                        /*
                             pipeline: encoder - decoder - handler
                             outbound: encoder
                             inbound: decoder -> handler
                             流程： request对象 -> decoder -> request字节流 -> server响应response字节流
                              -> decoder -> response对象 -> handler
                        */
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            logger.info("客户端连接到服务器");
            if(channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
