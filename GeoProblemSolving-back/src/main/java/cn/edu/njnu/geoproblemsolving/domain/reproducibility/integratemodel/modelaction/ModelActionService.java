package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.dto.AddModelActionDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction.dto.UpdateModelActionDTO;
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
public class ModelActionService {
    @Autowired
    ModelActionRepository modelActionRepository;

    public Object getModelInstanceInfo(String stepId) {
        List<ModelAction> modelAction = modelActionRepository.findAllByStepId(stepId);
        return  ResultUtils.success(modelAction);
    }

    public Object getAllModelInstanceInfo(String pid) {
        List<ModelAction> modelAction = modelActionRepository.findAllByPid(pid);
        return  ResultUtils.success(modelAction);
    }

    public Object saveModelInstanceInfo(AddModelActionDTO add) {
//        JSONObject arr = JSONObject.parseObject(add.getModelInstances());
//        BeanUtil.copyProperties(add, target)
//        add.setModelInstanceList(arr.getJSONArray("States"));
        ModelAction modelAction = new ModelAction();
        add.convertTo(modelAction);
        return  ResultUtils.success(modelActionRepository.insert(modelAction));
    }

    public JsonResult updateModelInstanceInfo(String id, UpdateModelActionDTO updateModelActionDTO) {
        ModelAction modelAction = modelActionRepository.findFirstById(id).orElseThrow(MyException::noObject);
        updateModelActionDTO.updateTo(modelAction);
        return ResultUtils.success(modelActionRepository.save(modelAction));
    }

    public void deleteById(String id) {
        modelActionRepository.deleteById(id);
    }


}
