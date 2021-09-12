package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by GBK on 2021/9/11
 * 序列化信息
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;

}
