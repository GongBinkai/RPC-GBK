import transport.RpcClient;
import transport.RpcClientProxy;
import transport.netty.client.NettyRpcClient;

/**
 * Created by GBK on 2021/9/19
 * Use...
 */
public class TestNettyClient {
    public static final String HOST = "127.0.0.1";
    public static void main(String[] args) {
        RpcClient rpcClient = new NettyRpcClient();
        RpcClientProxy proxy = new RpcClientProxy(rpcClient);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(1, "message test");
        String res = helloService.Hello(helloObject);
        System.out.println(res);
    }
}
