package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import lombok.Data;

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
}
