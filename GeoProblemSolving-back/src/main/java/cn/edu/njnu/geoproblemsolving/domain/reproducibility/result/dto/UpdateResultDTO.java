package cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.Result;
import lombok.Data;

@Data
public class UpdateResultDTO implements ToDomainConverter<Result> {
    String name;
    String description;
    String detail;//tinymce
    String map;//all the node map
    String pid;//project id
}
