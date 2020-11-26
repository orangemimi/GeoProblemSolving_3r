package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;

import java.util.List;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 10:52
 * @modified By：
 * @version:     1.0.0
 */

@Data
public class State extends ReNodeInfo {
    List<Event> Event;
}
