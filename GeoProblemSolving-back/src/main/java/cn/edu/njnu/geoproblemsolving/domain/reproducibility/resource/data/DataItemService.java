package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data.dto.AddDataItemDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data.dto.UpdateDataItemDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:25
 * @Version 1.0.0
 */
@Service
public class DataItemService {
    @Autowired
    private DataItemRepository dataItemRepository;

    public JsonResult getAll(String pid) {
        List<DataItem> dataItemList=dataItemRepository.findAllByPid(pid).orElseThrow(MyException::noObject);
        return ResultUtils.success(dataItemList);
    }

    public JsonResult update(String id, UpdateDataItemDTO updateDataItemDTO) {
        DataItem dataItem=dataItemRepository.findById(id).orElseThrow(MyException::noObject);
        updateDataItemDTO.updateTo(dataItem);
        return ResultUtils.success(dataItemRepository.save(dataItem));
    }

    public JsonResult save(AddDataItemDTO add) {
        DataItem dataItem = new DataItem();
        add.convertTo(dataItem);
        return ResultUtils.success(dataItemRepository.insert(dataItem));
    }

    public void del(String id) {
        dataItemRepository.deleteById(id);
    }
}
