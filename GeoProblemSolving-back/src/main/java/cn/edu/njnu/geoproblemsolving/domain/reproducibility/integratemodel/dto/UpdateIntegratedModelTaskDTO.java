package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.IntegrateModelTask;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:40
 * @Version 1.0.0
 */
@Data
public class UpdateIntegratedModelTaskDTO implements ToDomainConverter<IntegrateModelTask> {
    String taskName;
    String taskDescription;

    List<Map<String,String>> models;
    List<String> modelActions;

    String xml;
    String mxgraph;

}
