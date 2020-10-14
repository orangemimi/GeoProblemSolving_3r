package cn.edu.njnu.geoproblemsolving.domain.remote;

import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ModelContainerController
 * @Description
 * @Author Zhiyi
 * @Date 2019/12/16  21:58
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/modelItem")
public class ModelContainerController {

    SunModelItemService sunModelItemService;

    @RequestMapping(value = "/getModelItem/{id}",method = RequestMethod.GET)
    public JsonResult getModelItem(@PathVariable("id") String id) {
        return ResultUtils.success(sunModelItemService.getModelItem(id));
    }

    @RequestMapping(value = "/getModelItems",method = RequestMethod.GET)
    public JsonResult getModelItems() {
        return ResultUtils.success(sunModelItemService.getModelItems());
    }

    @RequestMapping(value = "/getModelInstance/{id}",method = RequestMethod.GET)
    public JsonResult getModelInstance(@PathVariable("id") String id) {
        return ResultUtils.success(sunModelItemService.getModelInstance(id));
    }

    @RequestMapping(value = "/addModelInstance",method = RequestMethod.POST)
    public JsonResult addModelInstance(@RequestBody JSONObject modelinstance) {
        return ResultUtils.success(sunModelItemService.addModelInstance(modelinstance));
    }

    @RequestMapping(value = "/modelInstance/{id}/invoke",method = RequestMethod.POST)
    public JsonResult invoke(@PathVariable("id") String id) throws InterruptedException {
        return ResultUtils.success(sunModelItemService.invokeModelInstance(id));
    }

    @RequestMapping(value = "/saveRecord",method = RequestMethod.POST)
    public JsonResult saveRecord(@RequestBody JSONObject toolRecord) {
        return ResultUtils.success(sunModelItemService.saveToolRecord(toolRecord));
    }

    @RequestMapping(value = "/getAllRecords/{stepId}",method = RequestMethod.GET)
    public JsonResult getAllRecords(@PathVariable("stepId") String stepId) {
        return ResultUtils.success(sunModelItemService.getAllRecords(stepId));
    }

    @RequestMapping(value = "/activity",method = RequestMethod.POST)
    public JsonResult activate(@RequestBody JSONObject obj) {
        return ResultUtils.success(sunModelItemService.activate(obj));
    }



}
