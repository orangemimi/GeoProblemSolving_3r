package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:08
 * @Version 1.0.0
 */
@Data
public class ModelItem extends BaseEntity {
//    ComputableModel computableModel;
    @Id
    String id;
    ToolItem2 modelTool;
//    List<String> toolIds;
}
