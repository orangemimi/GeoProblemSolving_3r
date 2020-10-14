package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/10/14 14:25
 * @modified By：
 * @version:     1.0.0
 */
@Data
@Document(collection = "ReToolItem")
public class ToolItem extends BaseEntity {
    @Id
    String id;
    String userId;
    String pid;

    String tid;
    String toolName;
    String toolUrl;//stateId, oid,mdlId,模型条目？
    JSONObject modelInfo; //stateId, oid,mdlId,模型条目？
    String description; // 可在多个不同的toolset内，或没有dataset
    ArrayList<String> recomStep; // step类型 or general
    ArrayList<String> categoryTag;
    String provider;
    String privacy;
    String toolImg;
    String detail;
}
