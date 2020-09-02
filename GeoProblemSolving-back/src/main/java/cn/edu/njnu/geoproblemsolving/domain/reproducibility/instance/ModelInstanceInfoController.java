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
    public JsonResult getStepInfo(@PathVariable("stepId") String stepId) {
        return ResultUtils.success(modelInstanceInfoService.getModelInstanceInfo(stepId));
    }

    @RequestMapping(value = "/update/{stepId}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    public JsonResult updateStepInfo(@PathVariable("stepId") String stepId, @RequestBody UpdateModelInstanceInfoDTO UpdateResourceDTO) {
        return ResultUtils.success(modelInstanceInfoService.updateModelInstanceInfo(stepId, UpdateResourceDTO));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResult saveStepInfo(@RequestBody AddModelInstanceInfoDTO add) {
            return ResultUtils.success(modelInstanceInfoService.saveModelInstanceInfo(add));
    }
}
