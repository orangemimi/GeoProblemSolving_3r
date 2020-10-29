package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask;

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
public class IntegrateModelTaskController {
    @Autowired
    IntegrateModelTaskService integrateModelTaskService;

//    @RequestMapping(value = "/{stepId}", method = RequestMethod.GET)
//    public JsonResult getTask(@PathVariable("stepId") String stepId) {
//        return ResultUtils.success(integrateModelTaskService.getModelTaskInfo(stepId));
//    }

//    @RequestMapping(value = "/all/{pid}", method = RequestMethod.GET)
//    public JsonResult getAllTask(@PathVariable("pid") String pid) {
//        return ResultUtils.success(integrateModelTaskService.getAllModelTaskInfo(pid));
//    }
//
//    @RequestMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
//    public JsonResult updateTask(@PathVariable("id") String id, @RequestBody UpdateIntegratedModelTaskDTO UpdateResourceDTO) {
//        return ResultUtils.success(integrateModelTaskService.updateModelTaskInfo(id, UpdateResourceDTO));
//    }

//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public JsonResult saveTask(@RequestBody AddIntegrateModelTaskDTO add) {
//            return ResultUtils.success(integrateModelTaskService.saveModelTaskInfo(add));
//    }
//    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
//    public JsonResult deleteTask(@PathVariable("id") String id){
//        integrateModelTaskService.deleteById(id);
//        return ResultUtils.success();
//    }
}
