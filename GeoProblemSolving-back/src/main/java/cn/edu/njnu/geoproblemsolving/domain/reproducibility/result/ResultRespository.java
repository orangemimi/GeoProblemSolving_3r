package cn.edu.njnu.geoproblemsolving.domain.reproducibility.result;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResultRespository  extends MongoRepository<Result,String> {
    Optional<Result> findFirstByPid(String pid);
}
