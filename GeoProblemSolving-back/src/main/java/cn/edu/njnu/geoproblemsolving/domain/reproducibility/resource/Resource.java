package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:04
 * @Version 1.0.0
 */
@Data
@Document(collection = "ReResource")
public class Resource extends BaseEntity {
    @Id
    String id;
    String userId;
    String pid;
    List<DataItem> dataItemList;
    List<ToolItem> toolItemList;
    List<DataItem> intermediateDataItemList;

}
