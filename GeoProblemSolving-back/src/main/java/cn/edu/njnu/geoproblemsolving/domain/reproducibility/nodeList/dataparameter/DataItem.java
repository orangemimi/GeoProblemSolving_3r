package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dataparameter;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 11:23
 * @modified By：
 */
@Data
public class DataItem extends ReNodeInfo {
    String reference;//对应上文中的限制条件
    String value;// data->url; parameter->value
    Boolean isDirect;
    String eventId;
}
