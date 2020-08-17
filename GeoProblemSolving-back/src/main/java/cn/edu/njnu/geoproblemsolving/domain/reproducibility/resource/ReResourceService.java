package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto.AddResourceDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.dto.UpdateResourceDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:25
 * @Version 1.0.0
 */
@Service
public class ReResourceService {
    @Autowired
    private ReResourceRepository resourceRepository;
    public JsonResult getResources(String pid) {
        Resource resource =resourceRepository.findFirstByPid(pid).orElseThrow(MyException::noObject);
        return  ResultUtils.success(resource);
    }

    public JsonResult updateResources(String pid, UpdateResourceDTO updateResourceDTO) {
        Resource resource =resourceRepository.findFirstByPid(pid).orElseThrow(MyException::noObject);
        updateResourceDTO.updateTo(resource);
        return ResultUtils.success(resourceRepository.save(resource));
    }


    public JsonResult saveResources(AddResourceDTO add) {
        Resource resource = new Resource();
        add.convertTo(resource);
        return  ResultUtils.success(resourceRepository.insert(resource));
    }
}
