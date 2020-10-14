package cn.edu.njnu.geoproblemsolving.domain.reproducibility.support;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.ContextDefinition;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.FlowChart;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.Resource;
import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/7/9  14:19
 * @Version 1.0.0
 */

@Data
public class DocumentationInfo extends BaseEntity {
    String name;
    JSONObject creator;
    JSONArray members;
    String author;
    Boolean isPublished;
    ContextDefinition contextDefinition;
    Resource resourceCollection;
    FlowChart flowChart;
    String version; //版本号

    /**
     * User
     * creator contains: userId, name, avatar
     * members contains: userId, name, avatar, role
     * role: creator, administrator, decision-maker, researcher, stakeholder, normal member(workers...), visitor(the public)
     */
}
