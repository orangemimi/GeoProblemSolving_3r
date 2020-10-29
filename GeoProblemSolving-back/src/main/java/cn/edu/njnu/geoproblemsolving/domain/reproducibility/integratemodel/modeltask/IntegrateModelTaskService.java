package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask.dto.AddIntegrateModelTaskDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modeltask.dto.UpdateIntegratedModelTaskDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:46
 * @Version 1.0.0
 */
@Service
public class IntegrateModelTaskService {
    @Autowired
    IntegrateModelTaskRepository integrateModelTaskRepository;

//    public Object getModelTaskInfo(String stepId) {
//        List<IntegrateModelTask> integrateModelTask = integrateModelTaskRepository.findAllByStepId(stepId);
//        return  ResultUtils.success(integrateModelTask);
//    }

    public Object getAllModelTaskInfo(String pid) {
        List<IntegrateModelTask> integrateModelTask = integrateModelTaskRepository.findAllByPid(pid);
        return  ResultUtils.success(integrateModelTask);
    }

    public Object saveModelTaskInfo(AddIntegrateModelTaskDTO add) {
//        JSONObject arr = JSONObject.parseObject(add.getModelInstances());
//        BeanUtil.copyProperties(add, target)
//        add.setModelInstanceList(arr.getJSONArray("States"));
        IntegrateModelTask integrateModelTask = new IntegrateModelTask();
        add.convertTo(integrateModelTask);
        return  ResultUtils.success(integrateModelTaskRepository.insert(integrateModelTask));
    }

    public JsonResult updateModelTaskInfo(String id, UpdateIntegratedModelTaskDTO updateIntegratedModelTaskDTO) {
        IntegrateModelTask integrateModelTask = integrateModelTaskRepository.findFirstById(id).orElseThrow(MyException::noObject);
        updateIntegratedModelTaskDTO.updateTo(integrateModelTask);
        return ResultUtils.success(integrateModelTaskRepository.save(integrateModelTask));
    }

    public void deleteById(String id) {
        integrateModelTaskRepository.deleteById(id);
    }


}
