package transport;

import entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public interface RpcClient {
    public Object sendRequest(RpcRequest rpcRequest);
}
