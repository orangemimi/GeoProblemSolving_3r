package cn.edu.njnu.geoproblemsolving.domain.reproducibility.context;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.output.OutputResource;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  13:53
 * @Version 1.0.0
 */
@Data
public class ContextDefinition {
    List<String> themes;
    String purpose;
    String simulationObject;//sumulation object
    String methods;
    String discussion;
    OutputResource outputResource;
}
