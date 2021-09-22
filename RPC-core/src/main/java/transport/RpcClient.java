package transport;

import entity.RpcRequest;
import enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = SerializerCode.KRYO.getCode();
    public Object sendRequest(RpcRequest rpcRequest);
}
