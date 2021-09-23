package handler;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.DefaultServiceProvider;
import registry.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by GBK on 2021/9/15
 * Use...
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new DefaultServiceProvider();
    }


    public Object handle(RpcRequest rpcRequest) {
        Object result = null;
        Object service = serviceProvider.getService(rpcRequest.getInterfaceName());
        try {
            result = invokeMethod(rpcRequest, service);
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (Exception e) {
            logger.error("error:", e);
        }
        return result;
    }

    private Object invokeMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (Exception e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
