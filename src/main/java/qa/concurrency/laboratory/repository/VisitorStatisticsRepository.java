package qa.concurrency.laboratory.repository;

import org.springframework.data.repository.CrudRepository;
import qa.concurrency.laboratory.model.User;

public interface VisitorStatisticsRepository extends CrudRepository<User, String> {
}
