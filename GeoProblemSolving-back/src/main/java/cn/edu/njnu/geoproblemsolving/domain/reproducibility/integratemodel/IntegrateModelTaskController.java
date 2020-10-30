package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.dto.AddIntegrateModelTaskDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResult getTask(@PathVariable("id") String id) {
        return ResultUtils.success(integrateModelTaskService.getModelTaskInfo(id));
    }

    @RequestMapping(value = "/all/{pid}", method = RequestMethod.GET)
    public JsonResult getAllTask(@PathVariable("pid") String pid) {
        return ResultUtils.success(integrateModelTaskService.getAllModelTaskInfo(pid));
    }

//    @RequestMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
//    public JsonResult updateTask(@PathVariable("id") String id, @RequestBody UpdateIntegratedModelTaskDTO UpdateResourceDTO) {
//        return ResultUtils.success(integrateModelTaskService.updateModelTaskInfo(id, UpdateResourceDTO));
//    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult saveTask(@RequestBody AddIntegrateModelTaskDTO add) {
            return ResultUtils.success(integrateModelTaskService.saveModelTaskInfo(add));
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JsonResult deleteTask(@PathVariable("id") String id){
        integrateModelTaskService.deleteById(id);
        return ResultUtils.success();
    }
}
