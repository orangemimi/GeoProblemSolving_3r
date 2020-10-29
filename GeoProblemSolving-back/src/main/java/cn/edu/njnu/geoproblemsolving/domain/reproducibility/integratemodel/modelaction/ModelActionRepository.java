package cn.edu.njnu.geoproblemsolving.domain.reproducibility.integratemodel.modelaction;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/8/19  15:47
 * @Version 1.0.0
 */
public interface ModelActionRepository extends MongoRepository<ModelAction,String> {
    Optional<ModelAction> findFirstById(String id);
    List<ModelAction> findAllByStepId(String stepId);
    List<ModelAction> findAllByPid(String userId);
}
