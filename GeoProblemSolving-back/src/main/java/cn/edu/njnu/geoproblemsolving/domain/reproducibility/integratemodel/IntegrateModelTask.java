package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:11
 * @Version 1.0.0
 */
@Data
@Document(collection = "ReIntegratedTask")
public class IntegrateModelTask extends BaseEntity {
    @Id
    String id;
    String projectId;//project id
    String userName;
    String userId;

    String taskId;//tid-->mangerserver fanhui
    String taskName;//自己起的
    String taskDescription;

    List<Map<String,String>> models;

    List<String> modelActions;

    String xml;
    String mxgraph;

    Boolean integrate;

    Integer status;//Started: 1, Finished: 2, Inited: 0, Error: -1

    Date lastRunTime;

}
