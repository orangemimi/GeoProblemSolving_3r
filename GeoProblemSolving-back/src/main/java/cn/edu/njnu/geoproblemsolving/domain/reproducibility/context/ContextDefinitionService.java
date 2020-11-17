package cn.edu.njnu.geoproblemsolving.domain.reproducibility.context;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.dto.AddContextDefinitionDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.context.dto.UpdateContextDefinitionDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/9/14  22:36
 * @Version 1.0.0
 */
@Service
public class ContextDefinitionService {
    @Autowired
    ContextDefinitionRepository contextDefinitionRepository;

    public JsonResult saveContextDefinition(AddContextDefinitionDTO addContextDefinitionDTO) {
        ContextDefinition contextDefinition = new ContextDefinition();
        addContextDefinitionDTO.convertTo(contextDefinition);
        return  ResultUtils.success(contextDefinitionRepository.insert(contextDefinition));
    }

    public JsonResult getContextDefinition(String pid) {
        Optional<ContextDefinition> contextDefinition = contextDefinitionRepository.findFirstByPid(pid);
        return  ResultUtils.success(contextDefinition);
    }

    public JsonResult updateContextDefinition(String pid, UpdateContextDefinitionDTO updateContextDefinitionDTO) {
        ContextDefinition contextDefinition = contextDefinitionRepository.findFirstByPid(pid).orElseThrow(MyException::noObject);
        updateContextDefinitionDTO.updateTo(contextDefinition);
        return ResultUtils.success(contextDefinitionRepository.save(contextDefinition));
    }
}
