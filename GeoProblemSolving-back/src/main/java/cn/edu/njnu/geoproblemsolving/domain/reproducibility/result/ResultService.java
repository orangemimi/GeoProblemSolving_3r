package cn.edu.njnu.geoproblemsolving.domain.reproducibility.result;

import cn.edu.njnu.geoproblemsolving.Exception.MyException;
import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.dto.AddResultDTO;
import cn.edu.njnu.geoproblemsolving.domain.reproducibility.result.dto.UpdateResultDTO;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultService {
    @Autowired
    ResultRespository resultRespository;
    public JsonResult getResult(String pid) {
        Optional<Result> result = resultRespository.findFirstByPid(pid);
        return ResultUtils.success(result);

    }
    public JsonResult save(AddResultDTO addResultDTO) {
        Result result = new Result();
        addResultDTO.convertTo(result);
        return  ResultUtils.success(resultRespository.insert(result));
    }
    public JsonResult update(String pid, UpdateResultDTO updateResultDTO) {
        Result result =  resultRespository.findFirstByPid(pid).orElseThrow(MyException::noObject);
        updateResultDTO.convertTo(result);
        return  ResultUtils.success(resultRespository.save(result));
    }

}
