package cn.edu.njnu.geoproblemsolving.domain.reproducibility.record.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.record.Record;
import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 20:21
 * @modified By：
 * @version: 1.0.0
 */

@Data
public class AddRecordDTO implements ToDomainConverter<Record> {
    String userId;
    String projectId;
    String description;
    String content;
    String eventType; //context definition or resource collection or process...
    String eventId; //查询条件Id
}
