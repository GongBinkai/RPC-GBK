package transport;

public interface RpcServer {
    public void start();
    <T> void publishService(Object service, Class<T> serviceClass);
}
