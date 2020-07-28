package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.support.resource.model;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  21:58
 * @Version 1.0.0
 */
@Data
@JsonTypeName(value = "modelOutside_node")
public class ModelOutsideNode {
    //OpenGMS MetaData Standard
//    String fullName;

    //是否是必须要从门户建立条目之后 进行直接的关联
    String catalog;
    String seriesName;
    ModelDeveloper modelDeveloper;//创建模型者 包含机构
    String language;//development language
    String abstractContent;
    List<String> classification;
    JSONArray reference;
    List<ModelParameter> modelParameterList;//参数说明
    Code code;
}
