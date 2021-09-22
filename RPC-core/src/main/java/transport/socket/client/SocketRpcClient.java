package transport.socket.client;

import entity.RpcRequest;
import nacos.NacosServiceRegistry;
import nacos.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;
import transport.RpcClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by GBK on 2021/9/16
 * Use...
 */
public class SocketRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private final ServiceRegistry serviceRegistry;

    public SocketRpcClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            return null;
        }
    }
}
