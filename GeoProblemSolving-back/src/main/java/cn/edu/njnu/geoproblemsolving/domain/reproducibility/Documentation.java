package cn.edu.njnu.geoproblemsolving.domain.reproducibility;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.ContextDefinition;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.Method;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data.DataItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool.ToolItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.Result;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.support.DocumentationInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  14:15
 * @Version 1.0.0
 */
@Data
@Document(collection = "ProcessDocumentations")
public class Documentation extends DocumentationInfo {
    @Id
    String id;
    ContextDefinition contextDefinition;
    DataItem dataItem;
    ToolItem toolItem;
    Result result;
    Method method;
}
