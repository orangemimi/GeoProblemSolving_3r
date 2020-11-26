package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support;

import lombok.Data;

import java.util.Map;

/**
 * @version : 1.0
 * @Author ：Zhiyi
 * @Date ：2020/11/12 10:56
 * @modified By：
 */
@Data
public class Event {
    String name;
    String description;
    String dataRef;
    String type;
    Map<String,Object> datasetItem;
}
