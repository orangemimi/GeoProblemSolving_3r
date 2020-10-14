package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.ModelInstanceInfo;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:40
 * @Version 1.0.0
 */
@Data
public class UpdateModelInstanceInfoDTO implements ToDomainConverter<ModelInstanceInfo> {
    String states;
    String status;
    Boolean checkedForMap;

}
