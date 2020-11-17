package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition.support.RelatedDataItem;
import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 15:49
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Condition extends ReNodeInfo {
    RelatedDataItem relatedDataItem;

}
