package transport.socket.server;

import factory.ThreadPoolFactory;
import nacos.NacosServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.DefaultServiceProvider;
import transport.AbstractRpcServer;
import transport.RpcServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by GBK on 2021/9/16
 * Use...
 */
public class SocketRpcServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public SocketRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new DefaultServiceProvider();
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-server-handler");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }
}
