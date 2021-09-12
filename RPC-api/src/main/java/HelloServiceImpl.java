import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by GBK on 2021/9/11
 * Use...
 */
public class HelloServiceImpl implements HelloService {
    private static Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String Hello(HelloObject helloObject) {
        logger.info("接收到的对象message：" + helloObject.getMessage());
        return "返回值:id=" + helloObject.getId();
    }
}
