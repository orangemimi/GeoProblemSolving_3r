package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto.AddModelInstanceInfoDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto.UpdateModelInstanceInfoDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:44
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/r/modelInstance")
public class ModelInstanceInfoController {
    @Autowired
    ModelInstanceInfoService modelInstanceInfoService;

    @RequestMapping(value = "/get/{stepId}", method = RequestMethod.GET)
    public JsonResult getStepInstances(@PathVariable("stepId") String stepId) {
        return ResultUtils.success(modelInstanceInfoService.getModelInstanceInfo(stepId));
    }

    @RequestMapping(value = "/getAll/{pid}", method = RequestMethod.GET)
    public JsonResult getAllInstances(@PathVariable("pid") String pid) {
        return ResultUtils.success(modelInstanceInfoService.getAllModelInstanceInfo(pid));
    }

    @RequestMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    public JsonResult updateInstance(@PathVariable("id") String id, @RequestBody UpdateModelInstanceInfoDTO UpdateResourceDTO) {
        return ResultUtils.success(modelInstanceInfoService.updateModelInstanceInfo(id, UpdateResourceDTO));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResult saveInstance(@RequestBody AddModelInstanceInfoDTO add) {
            return ResultUtils.success(modelInstanceInfoService.saveModelInstanceInfo(add));
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JsonResult deleteInstance(@PathVariable("id") String id){
        modelInstanceInfoService.deleteById(id);
        return ResultUtils.success();
    }
}
