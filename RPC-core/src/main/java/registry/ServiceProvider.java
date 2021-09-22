package registry;

/**
 * Created by GBK on 2021/9/12
 * Use...
 */
public interface ServiceProvider {
    <T> void addService(T service);
    Object getService(String serviceName);
}
