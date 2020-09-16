package cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:47
 * @Version 1.0.0
 */
public interface ModelInstanceInfoRepository extends MongoRepository<ModelInstanceInfo,String> {
    Optional<ModelInstanceInfo> findFirstById(String id);
    List<ModelInstanceInfo> findAllByStepId(String stepId);
}
