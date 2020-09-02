package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance;

import cn.edu.njnu.geoproblemsolving.domain.support.BaseEntity;
import lombok.Data;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  18:37
 * @Version 1.0.0
 */
@Data
public class ModelTask extends BaseEntity {
    Object Event;
    ModelInstanceInfo modelInstanceInfo;
    String taskId;//模型运行的tid 根据task id去查询模型运行记录
}
