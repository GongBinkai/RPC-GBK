package exception;

/**
 * 序列化异常
 *
 * @author GBK
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String msg) {
        super(msg);
    }
}
