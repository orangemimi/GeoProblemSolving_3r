package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:44
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/r/integrateTasks")
public class ModelActionController {
    @Autowired
    ModelActionService modelActionService;

//    @RequestMapping(value = "/{stepId}", method = RequestMethod.GET)
//    public JsonResult getStepInstances(@PathVariable("stepId") String stepId) {
//        return ResultUtils.success(modelActionService.getModelInstanceInfo(stepId));
//    }
//
//    @RequestMapping(value = "/all/{pid}", method = RequestMethod.GET)
//    public JsonResult getAllInstances(@PathVariable("pid") String pid) {
//        return ResultUtils.success(modelActionService.getAllModelInstanceInfo(pid));
//    }
//
//    @RequestMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
//    public JsonResult updateInstance(@PathVariable("id") String id, @RequestBody UpdateModelActionDTO UpdateResourceDTO) {
//        return ResultUtils.success(modelActionService.updateModelInstanceInfo(id, UpdateResourceDTO));
//    }

//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public JsonResult saveInstance(@RequestBody AddModelActionDTO add) {
//            return ResultUtils.success(modelActionService.saveModelInstanceInfo(add));
//    }
//    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
//    public JsonResult deleteInstance(@PathVariable("id") String id){
//        modelActionService.deleteById(id);
//        return ResultUtils.success();
//    }
}
