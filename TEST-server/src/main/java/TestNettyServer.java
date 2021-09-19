import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import transport.netty.server.NettyRpcServer;

/**
 * Created by GBK on 2021/9/19
 * Use...
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
//        System.out.println(registry.getClass().getDeclaringClass());
        registry.register(helloService);
        NettyRpcServer server = new NettyRpcServer();
        server.start(9001);
    }
}
