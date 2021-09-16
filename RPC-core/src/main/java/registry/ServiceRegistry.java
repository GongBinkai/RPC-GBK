package registry;

/**
 * Created by GBK on 2021/9/12
 * Use...
 */
public interface ServiceRegistry {
    <T> void register(T service);
    Object getService(String serviceName);
}
