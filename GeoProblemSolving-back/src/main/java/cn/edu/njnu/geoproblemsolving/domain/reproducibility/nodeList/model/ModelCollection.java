package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class ModelCollection {
//    List<ModelItem> modelItemList;
    @Id
    String id;
    List<Model> modelList;
//    List<Map<String,Object>> stateList;
}
