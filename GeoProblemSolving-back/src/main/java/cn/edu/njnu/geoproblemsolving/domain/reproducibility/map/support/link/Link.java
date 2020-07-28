package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.link;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  16:21
 * @Version 1.0.0
 */
@Data
@JsonTypeName(value = "link")
public class Link {
    String target;//箭头的目标
    String source;//箭头初始
    String edge;//线
    LinkTypeEnum linkTypeEnum;//节点之间相互关联的类型
}
