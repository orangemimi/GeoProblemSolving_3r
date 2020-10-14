package cn.edu.njnu.geoproblemsolving.domain.reproducibility.result;


import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.dto.AddResultDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.dto.UpdateResultDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:44
 * @Version 1.0.0
 */
@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping(value = "/r/result")
public class ResultController {
    @Autowired
    ResultService resultService;

    @RequestMapping(value = "/get/{pid}", method = RequestMethod.GET)
    public JsonResult getStepInstances(@PathVariable("pid") String pid) {
        return ResultUtils.success(resultService.getResult(pid));
    }

    @RequestMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    public JsonResult updateInstance(@PathVariable("pid") String pid, @RequestBody UpdateResultDTO updateResultDTO) {
        return ResultUtils.success(resultService.update(pid, updateResultDTO));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResult saveInstance(@RequestBody AddResultDTO add) {
        return ResultUtils.success(resultService.save(add));
    }
}
