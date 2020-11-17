package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency.support.From;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dependency.support.To;
import cn.edu.njnu.geoproblemsolving.domain.support.ReNodeInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 15:52
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Dependency extends ReNodeInfo {
    @Id
    String id;
    From from;
    To to;
}
