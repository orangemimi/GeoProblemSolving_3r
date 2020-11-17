package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dataparameter;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 11:23
 * @modified By：
 */
@Data
public class DataItem extends ReNodeInfo {
    @Id
    String id;

    String reference;//对应上文中的限制条件
    String value;// data->url; parameter->value
}
