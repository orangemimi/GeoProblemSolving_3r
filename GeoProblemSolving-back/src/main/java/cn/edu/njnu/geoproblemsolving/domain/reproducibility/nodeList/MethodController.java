package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dto.AddMethodDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dto.UpdateMethodDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/18 22:44
 * @modified By：
 * @version: 1.0.0
 */
@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping(value = "/r/methods")
public class MethodController {
    @Autowired
    MethodService methodService ;

    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    public JsonResult getMethod(@PathVariable("pid") String pid) {
        return methodService.getMethod(pid);
    }

    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateInstance(@PathVariable("pid") String pid, @RequestBody UpdateMethodDTO update) {
        return methodService.updateMethod(pid, update);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult saveInstance(@RequestBody AddMethodDTO add) {
        return methodService.save(add);
    }
}
