package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2020/11/18 22:46
 * @modified By：
 * @version: 1.0.0
 */
public interface MethodRepository extends MongoRepository<Method,String> {
    Optional<Method> findFirstByPid(String pid);
}
