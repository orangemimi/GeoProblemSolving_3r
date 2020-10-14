package cn.edu.njnu.geoproblemsolving.domain.reproducibility.map;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FlowChartRepository extends MongoRepository<FlowChart,String> {
    Optional<FlowChart> findFirstByStepId(String stepId);
    List<FlowChart> findAllByPid(String pid);
}
