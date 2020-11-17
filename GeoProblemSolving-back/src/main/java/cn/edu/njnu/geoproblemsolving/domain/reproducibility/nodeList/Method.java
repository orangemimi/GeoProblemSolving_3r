package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition.ConditionCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dataparameter.DataCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency.DependencyCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.ModelCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.tool.ToolCollection;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 14:43
 * @modified By：
 * @version:     1.0.0
 */

@Data
//@Document(collection = "ReMethod")
public class Method {
    @Id
    String id;
    String userId;
    String pid;//projectId
    List<Node> nodeList;

    //support部分
    ModelCollection modelCollection;//ModelCollection
    DataCollection dataCollection;//dataCollection
    ToolCollection toolCollection;
    ConditionCollection conditionCollection;
    DependencyCollection dependencyCollection;

}
