package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.link.Link;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.data.DataNode;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.model.ModelInsideNode;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.model.ModelOutsideNode;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.output.OutputResource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  19:43
 * @Version 1.0.0
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = DataNode.class, name = "data_node"),
        @JsonSubTypes.Type(value = ModelInsideNode.class, name = "modelInside_node"),
        @JsonSubTypes.Type(value = ModelOutsideNode.class, name = "modelOutside_node"),
        @JsonSubTypes.Type(value = OutputResource.class, name = "output_node"),
        @JsonSubTypes.Type(value = Link.class, name = "link"),
})
public class Node{
    String type;
    String name;
    String description;
    String parent;
    Optional<String> url;
}
