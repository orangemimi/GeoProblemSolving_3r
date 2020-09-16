package cn.edu.njnu.geoproblemsolving.domain.reproducibility.context;

import cn.edu.njnu.geoproblemsolving.domain.reproducibility.instance.ModelInstanceInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/9/14  22:35
 * @Version 1.0.0
 */
public interface ContextDefinitionRepository extends MongoRepository<ContextDefinition,String> {
    Optional<ContextDefinition> findFirstByPid(String pid);
}
