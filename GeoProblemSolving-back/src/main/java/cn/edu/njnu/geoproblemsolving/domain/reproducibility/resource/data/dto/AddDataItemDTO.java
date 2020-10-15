package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data.DataItem;
import lombok.Data;

import java.util.List;

@Data
public class AddDataItemDTO implements ToDomainConverter<DataItem> {
    String userId;
    String pid;

    String name;
    String url;
    Boolean isDirect;//false--中间处理数据 //true--直接上传数据
    List<String> toModelInstanceList; //作为输入数据的使用过的instance
    String fromModelInstance;//作为输出数据使用过的instance
    String stepBindId;//stepBind
    String stepBindName;//stepBind
    List<String> stepInherit;//数据在哪些步骤中使用的
}
