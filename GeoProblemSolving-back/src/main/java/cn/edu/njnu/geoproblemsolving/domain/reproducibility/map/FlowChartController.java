package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto.AddFlowChartDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.dto.UpdateFlowChartDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping(value = "/r/flowcharts")
public class FlowChartController {
    @Autowired
    FlowChartService conceptMapService;

    //get one
    @RequestMapping(value = "/one/{stepId}", method = RequestMethod.GET)
    public JsonResult get(@PathVariable("stepId") String stepId) {
        return conceptMapService.get(stepId);
    }

    //get all
    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    public JsonResult getAll(@PathVariable("pid") String pid) {
        return conceptMapService.getAll(pid);
    }

    //update （客户端提供改变的属性）
    @RequestMapping(value = "/{stepId}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult update(@PathVariable("stepId") String stepId, @RequestBody UpdateFlowChartDTO update) {
        return conceptMapService.update(stepId, update);
    }

    //create
    @RequestMapping(value = "", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddFlowChartDTO add) {
        return conceptMapService.create(add);
    }
}
