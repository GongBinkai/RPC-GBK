import enumeration.SerializerCode;
import registry.DefaultServiceProvider;
import registry.ServiceProvider;
import serializer.CommonSerializer;
import transport.netty.server.NettyRpcServer;

/**
 * Created by GBK on 2021/9/19
 * Use...
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyRpcServer server = new NettyRpcServer("127.0.0.1", 9001, SerializerCode.KRYO.getCode());
        server.publishService(helloService, HelloService.class);
        server.start();
    }
}
