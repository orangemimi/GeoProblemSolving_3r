package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.ModelAction;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:40
 * @Version 1.0.0
 */
@Data
public class UpdateModelActionDTO implements ToDomainConverter<ModelAction> {
    String states;
    String status;
    Boolean checkedForMap;

    // wzh ModelAction
    List<Map<String,Object>> outputData;
    List<Map<String,Object>> inputData;
    Integer iterationNum = 1;
    String step;

}
