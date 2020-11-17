package cn.edu.njnu.geoproblemsolving.domain.reproducibility.record;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.record.dto.AddRecordDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/12 20:43
 * @modified By：
 * @version: 1.0.0
 */
@RestController
@RequestMapping(value = "/r/records")
public class RecordController {
    @Autowired
    RecordService recordService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResult getRecordById(@PathVariable("id") String id) {
        return recordService.getModelTaskInfo(id);
    }

    @RequestMapping(value = "/all/{pid}", method = RequestMethod.GET)
    public JsonResult getAllRecords(@PathVariable("pid") String pid) {
        return recordService.getAllRecords(pid);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult saveRecord(@RequestBody AddRecordDTO add) {
        return recordService.saveRecord(add);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JsonResult deleteTask(@PathVariable("id") String id){
        recordService.deleteById(id);
        return ResultUtils.success();
    }
}
