package cn.edu.njnu.geoproblemsolving.domain.reproducibility.resource.tool;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author     ：Zhiyi
 * @Date       ：2020/10/14 14:33
 * @modified By：
 * @version:     1.0.0
 */
public interface ToolItemRepository extends MongoRepository<ToolItem,String> {
    Optional<List<ToolItem>> findAllByPid(String pid);
    void deleteByTid(String tid);
}
