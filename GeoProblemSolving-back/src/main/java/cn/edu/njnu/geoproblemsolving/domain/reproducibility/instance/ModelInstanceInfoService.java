package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto.AddModelInstanceInfoDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.dto.UpdateModelInstanceInfoDTO;
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
public class ModelInstanceInfoService {
    @Autowired
    ModelInstanceInfoRepository modelInstanceInfoRepository;

    public Object getModelInstanceInfo(String stepId) {
        List<ModelInstanceInfo> modelInstanceInfo = modelInstanceInfoRepository.findAllByStepId(stepId);
        return  ResultUtils.success(modelInstanceInfo);
    }

    public Object saveModelInstanceInfo(AddModelInstanceInfoDTO add) {
//        JSONObject arr = JSONObject.parseObject(add.getModelInstances());
//        BeanUtil.copyProperties(add, target)
//        add.setModelInstanceList(arr.getJSONArray("States"));
        ModelInstanceInfo modelInstanceInfo = new ModelInstanceInfo();
        add.convertTo(modelInstanceInfo);
        return  ResultUtils.success(modelInstanceInfoRepository.insert(modelInstanceInfo));
    }

    public JsonResult updateModelInstanceInfo(String id, UpdateModelInstanceInfoDTO updateModelInstanceInfoDTO) {
        ModelInstanceInfo modelInstanceInfo = modelInstanceInfoRepository.findFirstById(id).orElseThrow(MyException::noObject);
        updateModelInstanceInfoDTO.updateTo(modelInstanceInfo);
        return ResultUtils.success(modelInstanceInfoRepository.save(modelInstanceInfo));
    }

    public void deleteById(String id) {
        modelInstanceInfoRepository.deleteById(id);
    }
}
