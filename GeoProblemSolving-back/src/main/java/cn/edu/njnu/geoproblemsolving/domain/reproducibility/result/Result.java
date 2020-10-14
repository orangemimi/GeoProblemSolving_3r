package cn.edu.njnu.geoproblemsolving.domain.reproducibility.result;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ReResult")
public class Result extends BaseEntity{
    @Id
    String id;
    String name;
    String description;
    String detail;//tinymce
    String map;//all the node map
    String pid;//project id
}
