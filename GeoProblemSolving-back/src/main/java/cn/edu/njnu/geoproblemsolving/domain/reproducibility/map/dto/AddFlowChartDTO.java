package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.FlowChart;
import lombok.Data;

import java.util.List;

@Data
public class AddFlowChartDTO implements ToDomainConverter<FlowChart> {
    String stepName;
    String stepId;
    String userId;
    String pid;//projectid
    String type;

//    List<String> nodes;
    List<String> modelInstanceIdList;
    String mapXml;
}
