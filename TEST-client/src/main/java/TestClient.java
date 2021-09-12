import transport.RpcClientProxy;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */

public class TestClient {
    public static final String HOST = "127.0.0.1";
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy(HOST, 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
//        System.out.println(System.identityHashCode(helloService));
        HelloObject helloObject = new HelloObject(1, "message test");
        String res = helloService.Hello(helloObject);
        System.out.println(res);
    }
}
