package cn.edu.njnu.geoproblemsolving.domain.reproducibility.record;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.record.dto.AddRecordDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:46
 * @Version 1.0.0
 */
@Service
public class RecordService {
    @Autowired
    RecordRepository recordRepository;

    public JsonResult getModelTaskInfo(String id) {
        Optional<Record> record = recordRepository.findFirstById(id);
        return  ResultUtils.success(record);
    }

    public JsonResult getAllRecords(String pid) {
        List<Record> recordList = recordRepository.findAllByProjectId(pid);
        return  ResultUtils.success(recordList);
    }

    public JsonResult saveRecord(AddRecordDTO add) {
        Record record = new Record();
        add.convertTo(record);
        return  ResultUtils.success(recordRepository.insert(record));
    }

    public void deleteById(String id) {
        recordRepository.deleteById(id);
    }

}
