package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:41
 * @Version 1.0.0
 */
public interface DataItemRepository extends MongoRepository<DataItem,String> {
    Optional<List<DataItem>> findAllByPid(String pid);
}
