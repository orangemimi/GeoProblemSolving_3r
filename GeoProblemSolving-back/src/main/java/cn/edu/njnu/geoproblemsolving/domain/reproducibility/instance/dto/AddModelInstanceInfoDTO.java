package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.ModelInstanceInfo;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:39
 * @Version 1.0.0
 */

@Data
public class AddModelInstanceInfoDTO implements ToDomainConverter<ModelInstanceInfo> {
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
    String tid;//tool id
    String toolUrl;
    String status;
    String md5;
}
