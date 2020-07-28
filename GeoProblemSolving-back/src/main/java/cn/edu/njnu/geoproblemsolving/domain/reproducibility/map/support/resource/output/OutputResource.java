package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.output;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  20:44
 * @Version 1.0.0
 */
@Data
@JsonTypeName(value = "output_node")
public class OutputResource {
    OutputConstraint outputConstraint;
    OutputForm outputForm;//show form
}
