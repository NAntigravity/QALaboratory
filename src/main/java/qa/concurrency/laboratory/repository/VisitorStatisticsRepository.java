package qa.concurrency.laboratory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import qa.concurrency.laboratory.model.User;

@Repository
public interface VisitorStatisticsRepository extends CrudRepository<User, String> {
}
