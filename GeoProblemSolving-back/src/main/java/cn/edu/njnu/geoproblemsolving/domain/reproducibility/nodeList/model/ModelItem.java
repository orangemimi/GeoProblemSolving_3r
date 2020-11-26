package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support.Event;
import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;

import java.util.List;
/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 16:21
 * @modified By：
 * @version:     1.0.0
 */

@Data
public class ModelItem extends ReNodeInfo {
    String modelRef;//标识用的那个model的id
    String nodeRef;//标识node

    List<Event> stateList;
}
