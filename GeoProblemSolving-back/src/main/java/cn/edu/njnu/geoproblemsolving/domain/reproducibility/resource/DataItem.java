package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:34
 * @Version 1.0.0
 */
@Data
public class DataItem {
    String name;
    String url;
    Boolean isDirect;//false--中间处理数据 //true--直接上传数据
    List<String> toModelInstanceList; //作为输入数据的使用过的instance
    String fromModelInstance;//作为输出数据使用过的instance
    StepBind stepBind;//stepBind
    List<String> stepInherit;//数据在哪些步骤中使用的
}
