package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.Node;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  14:22
 * @Version 1.0.0
 */
@Data
public class ConceptMap {
    List<Node> nodes;
    String conceptXml;
    Optional<String> constraint;
}
