package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support.State;
import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 16:21
 * @modified By：
 * @version:     1.0.0
 */

@Data
public class Model extends ReNodeInfo {
    @Id
    String id;
    String modelRef;//标识用的那个model的id
    List<State> stateList;
}
