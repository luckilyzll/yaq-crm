package org.jeecg.modules.cable.utils;


import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ruan
 * ClassName: CollectionCopyUtil <br/>
 * Description: <br/>
 * date: 2020/5/27 15:02<br/>
 *
 * @author Administrator<br />
 * @since JDK 1.8
 */
public class CollectionCopyUtil {
    public static <T> List copyList(List<T> list, Class tClass) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList();
        }
        return JSON.parseArray(JSON.toJSONString(list), tClass);
    }

    public static Map<String, Object> copyMap(Map map) {
        return JSON.parseObject(JSON.toJSONString(map));
    }
}
