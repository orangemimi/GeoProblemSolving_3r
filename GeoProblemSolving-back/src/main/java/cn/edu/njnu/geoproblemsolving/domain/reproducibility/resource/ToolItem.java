package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/14  18:49
 * @Version 1.0.0
 */
@Data
public class ToolItem {
    private String tid;
    private String toolName;
    private String toolUrl;//stateId, oid,mdlId,模型条目？
    private JSONObject modelInfo; //stateId, oid,mdlId,模型条目？
    private String description; // 可在多个不同的toolset内，或没有dataset
    private ArrayList<String> recomStep; // step类型 or general
    private ArrayList<String> categoryTag;
    private String provider;
    private String privacy;
    private String toolImg;
    private String detail;

}
