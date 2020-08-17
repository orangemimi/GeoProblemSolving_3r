package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.DataItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.ToolItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.Resource;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:58
 * @Version 1.0.0
 */
@Data
public class AddResourceDTO implements ToDomainConverter<Resource> {
    String userId;
    String pid;
    List<DataItem> dataItemList;
    List<ToolItem> toolItemList;
}
