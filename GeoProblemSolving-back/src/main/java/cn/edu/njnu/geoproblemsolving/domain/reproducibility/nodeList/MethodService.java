package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.map.FlowChart;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dto.AddMethodDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList.dto.UpdateMethodDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/18 22:46
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class MethodService {
    @Autowired
    MethodRepository methodRepository;

    public JsonResult getMethod(String pid) {
        Optional<Method> method = methodRepository.findFirstByPid(pid);
        return  ResultUtils.success(method);
    }

    public JsonResult updateMethod(String pid, UpdateMethodDTO update) {
        Method method = methodRepository.findFirstByPid(pid).orElseThrow(MyException::noObject);
        update.updateTo(method);
        return ResultUtils.success(methodRepository.save(method));
    }

    public JsonResult save(AddMethodDTO add) {
        Method method = new Method();
        add.convertTo(method);
        return ResultUtils.success(methodRepository.insert(method));
    }
}
