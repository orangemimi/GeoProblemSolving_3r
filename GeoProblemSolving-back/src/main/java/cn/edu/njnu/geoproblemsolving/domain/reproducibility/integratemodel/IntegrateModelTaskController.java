package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.dto.AddIntegrateModelTaskDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.dto.UpdateIntegratedModelTaskDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        return integrateModelTaskService.getModelTaskInfo(id);
    }

    @RequestMapping(value = "/all/{pid}", method = RequestMethod.GET)
    public JsonResult getAllTask(@PathVariable("pid") String pid) {
        return integrateModelTaskService.getAllModelTaskInfo(pid);
    }
    //model action数据配置引发更新，其他的均可选另存为一个task，或更新
    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateTask(@PathVariable("id") String id, @RequestBody UpdateIntegratedModelTaskDTO UpdateResourceDTO) {
        return integrateModelTaskService.updateModelTaskInfo(id, UpdateResourceDTO);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult saveTask(@RequestBody AddIntegrateModelTaskDTO add) {
        return integrateModelTaskService.saveModelTaskInfo(add);
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JsonResult deleteTask(@PathVariable("id") String id){
        integrateModelTaskService.deleteById(id);
        return ResultUtils.success();
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public JsonResult runTask(@RequestParam("file") MultipartFile file,
                              @RequestParam("taskName") String taskName,
                              HttpServletRequest request) throws IOException, ServletException {
        HttpSession session = request.getSession();
        if(session.getAttribute("userId")==null) {
            return ResultUtils.error(-1, "no login");
        }
        else {
            String username = session.getAttribute("userName").toString();
            return integrateModelTaskService.runTask(file, taskName,username);
        }
    }
}
