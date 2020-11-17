package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.model.support;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @version : 1.0
 * @Author ：Zhiyi
 * @Date ：2020/11/12 10:56
 * @modified By：
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = ResponseEvent.class, name = "response")
        , @JsonSubTypes.Type(value = NoResponseEvent.class, name = "noresponse")
})
public class Event {
    String name;
    String description;
    String optional;
    String type;
}
