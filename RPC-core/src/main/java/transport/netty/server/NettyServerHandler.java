package transport.netty.server;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import server.RequestHandler;

/**
 * Created by GBK on 2021/9/19
 * Use...
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器接收到请求: {}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            System.out.println("server res");
            /*
                注意：ctx.writeAndFlush()和ctx.channel().writeAndFlush()是有区别的
                ctx.writeAndFlush()从当前节点往前查找out性质的handler
                ctx.channel().writeAndFlush()从链表结尾开始往前查找out性质的handler
             */

//            加上channel 会从pipeline结尾回头搜索outbound
//            ChannelFuture future = channelHandlerContext.channel().writeAndFlush(RpcResponse.success(result));

//            不加channel 从inbound处理位置回头搜索outbound
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result));

            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(channelHandlerContext);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
