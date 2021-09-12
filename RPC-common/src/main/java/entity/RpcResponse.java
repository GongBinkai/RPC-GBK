package entity;

import enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
  * Created by GBK on 2021/9/11
  * RPC回复格式
  */
@Data
public class RpcResponse<T> implements Serializable {
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(ResponseCode.SUCCESS.getCode());
        rpcResponse.setData(data);
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(code.getCode());
        rpcResponse.setMessage(code.getMessage());
        return rpcResponse;
    }
}
