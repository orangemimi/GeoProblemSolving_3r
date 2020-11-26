package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.Method;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.Node;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.condition.ConditionCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dataparameter.DataItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency.DependencyCollection;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.ModelItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.tool.ToolCollection;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/18 22:52
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class AddMethodDTO implements ToDomainConverter<Method> {
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
