package transport;

import enumeration.RpcError;
import exception.RpcException;
import nacos.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceProvider;
import serializer.CommonSerializer;
import handler.RequestHandler;

import java.net.InetSocketAddress;

/**
 * @author ziyang
 */
public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;
    protected CommonSerializer serializer;
    protected RequestHandler requestHandler = new RequestHandler();

    public void start() {

    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addService(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

}
