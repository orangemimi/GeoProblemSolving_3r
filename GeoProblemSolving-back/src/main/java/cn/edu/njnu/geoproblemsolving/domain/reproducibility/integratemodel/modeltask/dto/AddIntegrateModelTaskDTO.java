package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.ModelAction;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask.IntegrateModelTask;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:39
 * @Version 1.0.0
 */

@Data
public class AddIntegrateModelTaskDTO implements ToDomainConverter<IntegrateModelTask> {
    String taskId;
    String taskName;
    String description;

    List<Map<String,String>> models;

    List<ModelAction> modelActions;

    String xml;
    String mxgraph;
}
