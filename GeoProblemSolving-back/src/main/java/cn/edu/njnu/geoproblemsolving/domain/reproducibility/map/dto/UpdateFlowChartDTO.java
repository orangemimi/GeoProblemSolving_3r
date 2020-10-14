package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto;

import cn.edu.njnu.geoproblemsolving.Dto.ToDomainConverter;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.FlowChart;
import lombok.Data;

import java.util.List;

@Data
public class UpdateFlowChartDTO implements ToDomainConverter<FlowChart> {
//    List<String> nodes;
    List<String> modelInstanceIdList;
    String mapXml;
}
