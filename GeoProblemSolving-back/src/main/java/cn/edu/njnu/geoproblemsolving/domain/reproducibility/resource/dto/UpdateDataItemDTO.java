package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/9/3  14:34
 * @Version 1.0.0
 */
@Data
public class UpdateDataItemDTO {
    List<String> modelInstanceInputList; //作为输入数据的使用过的instance
    List<String> modelInstanceOutputList;//作为输出数据使用过的instance
}
