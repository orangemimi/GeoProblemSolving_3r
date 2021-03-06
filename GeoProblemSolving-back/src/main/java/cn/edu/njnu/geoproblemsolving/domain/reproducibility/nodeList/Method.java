package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition.ConditionCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dataparameter.DataItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency.DependencyCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.ModelItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.tool.ToolCollection;
import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/11/12 14:43
 * @modified By：
 * @version:     1.0.0
 */

@Data
@Document(collection = "ReMethod")
public class Method extends BaseEntity {
    @Id
    String id;
    String userId;
    String pid;//projectId
    List<Node> nodeList;

    //support部分
    List<ModelItem> modelCollection;//ModelCollection
    List<DataItem> dataCollection;//dataCollection
    ToolCollection toolCollection;
    ConditionCollection conditionCollection;
    DependencyCollection dependencyCollection;

}
