package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  14:22
 * @Version 1.0.0
 */
@Data
@Document(collection = "ReFlowChart")
public class FlowChart extends BaseEntity {
    @Id
    String id;
    String stepName;
    String stepId;
    String userId;
    String pid;//projectid
    String type;

//    List<String> nodes;
    List<String> modelInstanceIdList;
    String mapXml;
//    Optional<String> constraint;
}
