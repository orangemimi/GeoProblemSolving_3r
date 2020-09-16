package cn.edu.njnu.geoproblemsolving.domain.reproducibility.context;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.output.OutputResource;
import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  13:53
 * @Version 1.0.0
 */
@Data
@Document(collection = "ReContext")
public class ContextDefinition extends BaseEntity {
    @Id
    String id;
    String userId;
    String pid;

    List<String> themes;
    String purpose;
    String object;//sumulation object
    String methods;
    String scale;
    String discussion;
    OutputResource outputResource;
}
