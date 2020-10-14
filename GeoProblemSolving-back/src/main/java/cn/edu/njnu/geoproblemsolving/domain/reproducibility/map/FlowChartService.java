package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto.AddFlowChartDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto.UpdateFlowChartDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlowChartService {
    @Autowired
    FlowChartRepository flowChartRepository;
    public JsonResult get(String stepId) {
        Optional<FlowChart> flowChart = flowChartRepository.findFirstByStepId(stepId);
        return  ResultUtils.success(flowChart);
    }

    public JsonResult getAll(String pid) {
        List<FlowChart> flowChartList = flowChartRepository.findAllByPid(pid);
        return  ResultUtils.success(flowChartList);
    }

    public JsonResult update(String stepId, UpdateFlowChartDTO update) {
        FlowChart flowChart = flowChartRepository.findFirstByStepId(stepId).orElseThrow(MyException::noObject);
        update.updateTo(flowChart);
        return ResultUtils.success(flowChartRepository.save(flowChart));

    }

    public JsonResult create(AddFlowChartDTO add) {
        FlowChart flowChart = new FlowChart();
        add.convertTo(flowChart);
        return ResultUtils.success(flowChartRepository.insert(flowChart));
    }
}
