package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool.dto.AddToolItemDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/10/14 11:27
 * @modified By：
 * @version:     1.0.0
 */
@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping("/r/toolItems")
public class ToolItemController {
    @Autowired
    ToolItemService toolItemService;

    @RequestMapping(value = "/{pid}",method = RequestMethod.GET)
    public JsonResult getAll(@PathVariable("pid") String pid) {
        return toolItemService.getAll(pid);
    }

    //更新部分字段
//    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"},method = RequestMethod.PATCH)
//    public JsonResult update(@PathVariable("id") String id,@RequestBody UpdateDataItemDTO updateDataItemDTO){
//        return dataItemService.update(id,updateDataItemDTO);
//    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public JsonResult save(@RequestBody AddToolItemDTO add){
        return toolItemService.save(add);
    }

    @RequestMapping(value = "/{tid}",method = RequestMethod.DELETE)
    public JsonResult delete(@PathVariable("tid") String tid){
        toolItemService.del(tid);
        return ResultUtils.success();
    }
}
