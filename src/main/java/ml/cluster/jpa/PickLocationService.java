package ml.cluster.jpa;

import ml.cluster.to.PickLocationViewDO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickLocationService extends CrudRepository<Long, PickLocationViewDO> {

    List<PickLocationViewDO> findByLine(String line);
}
