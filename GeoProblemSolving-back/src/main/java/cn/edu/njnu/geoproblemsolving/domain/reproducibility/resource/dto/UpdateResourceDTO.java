package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.DataItem;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.Resource;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.ToolItem2;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:37
 * @Version 1.0.0
 */
@Data
public class UpdateResourceDTO implements ToDomainConverter<Resource> {
    List<DataItem> dataItemList;
    List<ToolItem2> toolItem2List;
}
