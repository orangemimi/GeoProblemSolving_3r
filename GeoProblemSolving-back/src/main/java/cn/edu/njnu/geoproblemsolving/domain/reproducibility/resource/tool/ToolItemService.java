package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool.dto.AddToolItemDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/10/14 14:42
 * @modified By：
 * @version:     1.0.0
 */
@Service
public class ToolItemService {
    @Autowired
    private ToolItemRepository toolItemRepository;
    public JsonResult getAll(String pid) {
        List<ToolItem> toolItemList = toolItemRepository.findAllByPid(pid).orElseThrow(MyException::noObject);
        return ResultUtils.success(toolItemList);
    }

//    public JsonResult update(String id, UpdateDataItemDTO updateDataItemDTO) {
//        ModelItem modelItem=modelItemRepository.findById(id).orElseThrow(MyException::noObject);
//        updateDataItemDTO.updateTo(modelItem);
//        return ResultUtils.success(modelItemRepository.save(modelItem));
//    }

    public JsonResult save(AddToolItemDTO add) {
        ToolItem toolItem = new ToolItem();
        add.convertTo(toolItem);
        return ResultUtils.success(toolItemRepository.insert(toolItem));
    }

    public void del(String tid) {
        toolItemRepository.deleteByTid(tid);
    }
}
