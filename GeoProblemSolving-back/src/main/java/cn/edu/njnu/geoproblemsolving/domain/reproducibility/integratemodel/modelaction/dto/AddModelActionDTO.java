package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.ModelAction;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:39
 * @Version 1.0.0
 */

@Data
public class AddModelActionDTO implements ToDomainConverter<ModelAction> {
    String stepName;
    String stepId;
    String type;
    String stepDescription;
    String pid;//project id
    String user;
    String userId;
    //    List<ModelInstance> modelInstances;
    String states;
    String name;//model name
    String description;//model description
    String toolId;//tool id
    String toolUrl;

    String md5;
    String status;
    String tid;// model taskid
    String ip;
    String port;

    // wzh ModelAction
    List<Map<String,Object>> outputData;
    List<Map<String,Object>> inputData;
    Integer iterationNum = 1;
    String step;
}
