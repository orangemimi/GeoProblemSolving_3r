package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 10:52
 * @modified By：
 * @version:     1.0.0
 */

@Data
public class State extends ReNodeInfo {
    @Id
    String id;
    List<Map<String,Object>> eventList;
}
