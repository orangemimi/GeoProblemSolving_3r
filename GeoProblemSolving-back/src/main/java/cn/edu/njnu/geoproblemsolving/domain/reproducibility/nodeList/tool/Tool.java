package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.tool;

import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 15:03
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Tool extends ReNodeInfo {
    @Id
    String id;
    String toolRef;//对应tool； 标识使用的是哪个tool
}
