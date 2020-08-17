package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource;

import cn.edu.njnu.geoproblemsolving.domain.tool.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:41
 * @Version 1.0.0
 */
public interface ReResourceRepository extends MongoRepository<Resource,String> {
    Optional<Resource> findFirstByPid(String pid);
}
