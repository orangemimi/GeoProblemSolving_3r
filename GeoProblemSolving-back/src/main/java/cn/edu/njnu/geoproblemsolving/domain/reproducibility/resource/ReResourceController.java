package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto.AddResourceDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto.UpdateResourceDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:20
 * @Version 1.0.0
 */
@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping("/r/resource")
public class ReResourceController {
    @Autowired
    ReResourceService resourceService;

    @RequestMapping(value = "/get/{pid}",method = RequestMethod.GET)
    public JsonResult getResources(@PathVariable("pid") String pid) {
        return ResultUtils.success(resourceService.getResources(pid));
    }

    @RequestMapping(value = "/update/{pid}", produces = {"application/json;charset=UTF-8"},method = RequestMethod.POST)
    public JsonResult updateResources(@PathVariable("pid") String pid,@RequestBody UpdateResourceDTO UpdateResourceDTO){
        return ResultUtils.success(resourceService.updateResources(pid,UpdateResourceDTO));
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public JsonResult saveResources(@RequestBody AddResourceDTO add){
        return ResultUtils.success(resourceService.saveResources(add));
    }
}
