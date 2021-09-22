package transport.socket.server;

import entity.RpcRequest;
import entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by GBK on 2021/9/16
 * Use...
 */
public class RequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceProvider serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceProvider serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = requestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误:", e);
        }
    }
}
