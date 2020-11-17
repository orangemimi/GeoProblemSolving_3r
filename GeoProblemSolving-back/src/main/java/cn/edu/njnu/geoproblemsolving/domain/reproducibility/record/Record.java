package cn.edu.njnu.geoproblemsolving.domain.reproducibility.record;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 20:20
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document(collection = "ReRecord")
public class Record extends BaseEntity {
    @Id
    String id;
    String userId;
    String projectId;
    String description;
    String content;
    String eventType; //context definition or resource collection or process...
    String eventId; //查询条件Id
}
