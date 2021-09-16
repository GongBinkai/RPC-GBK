import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import transport.RpcServer;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
