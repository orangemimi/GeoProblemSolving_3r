package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition.support;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 16:50
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Branch extends ReNodeInfo {
    @Id
    String id;
    String relatedId;//modelId or toolId
}
