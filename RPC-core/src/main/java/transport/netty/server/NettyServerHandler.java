package transport.netty.server;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import handler.RequestHandler;

/**
 * Created by GBK on 2021/9/19
 * Use...
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
//    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
//    private static final ExecutorService threadPool;

    static {
        requestHandler = new RequestHandler();
//        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            if(rpcRequest.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求: {}", rpcRequest);
            Object result = requestHandler.handle(rpcRequest);
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

//            future.addListener(ChannelFutureListener.CLOSE);
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

    /**
     * server心跳触发器
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
