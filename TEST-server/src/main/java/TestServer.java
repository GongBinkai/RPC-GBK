import transport.RpcServer;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
//        System.out.println(System.identityHashCode(helloService));
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
