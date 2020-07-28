package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  21:59
 * @Version 1.0.0
 */
@Data
@JsonTypeName(value = "modelInside_node")
public class ModelInsideNode {
    String url;//门户的模型条目及计算模型
    ComputableModel computableModel;//获得的计算属性
}
