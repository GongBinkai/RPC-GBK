package transport;

import entity.RpcRequest;
import enumeration.SerializerCode;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = SerializerCode.KRYO.getCode();
    public Object sendRequest(RpcRequest rpcRequest);
}
