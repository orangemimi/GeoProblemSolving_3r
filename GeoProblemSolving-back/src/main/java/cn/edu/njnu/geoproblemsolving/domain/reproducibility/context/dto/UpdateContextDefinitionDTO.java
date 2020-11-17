package cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.ContextDefinition;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/9/14  22:31
 * @Version 1.0.0
 */
@Data
public class UpdateContextDefinitionDTO implements ToDomainConverter<ContextDefinition> {
    List<String> themes;
    String purpose;
    String object;//sumulation object
    String methods;
    String boundary;
    String scale;
    String discussion;
}
