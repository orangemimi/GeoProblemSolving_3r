package cn.edu.njnu.geoproblemsolving.domain.reproducibility;

import cn.edu.njnu.geoproblemsolving.domain.tool.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Zhiyi
 * @Date 2020/7/10  21:06
 * @Version 1.0.0
 */
public interface DocumentationRepository extends MongoRepository<Tool,String> {

}
