package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by GBK on 2021/9/11
 * 包的类型
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
