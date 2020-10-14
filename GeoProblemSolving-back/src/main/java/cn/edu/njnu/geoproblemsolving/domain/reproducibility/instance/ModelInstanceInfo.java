package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:11
 * @Version 1.0.0
 */
@Data
@Document(collection = "ReModelInstance")
public class ModelInstanceInfo extends BaseEntity {
    @Id
    String id;
    String stepName;
    String stepId;
    String type;
    String stepDescription;
    String pid;//project id
    String userName;
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
}
