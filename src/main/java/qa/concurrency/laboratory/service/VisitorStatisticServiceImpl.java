package qa.concurrency.laboratory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qa.concurrency.laboratory.model.User;
import qa.concurrency.laboratory.repository.VisitorStatisticsRepository;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class VisitorStatisticServiceImpl implements VisitorStatisticService {
    private final VisitorStatisticsRepository visitorStatisticsRepository;

    public VisitorStatisticServiceImpl(VisitorStatisticsRepository visitorStatisticsRepository) {
        this.visitorStatisticsRepository = visitorStatisticsRepository;
    }

    public void visit(String name) {
        Optional<User> user = visitorStatisticsRepository.findById(name);
        if (user.isEmpty()) {
            visitorStatisticsRepository.save(new User(name));
        } else {
            User currentUser = user.get();
            currentUser.visitsAmount++;
            visitorStatisticsRepository.save(currentUser);
        }
    }

    public long totalVisits() {
        Iterable<User> allUsers = visitorStatisticsRepository.findAll();
        return StreamSupport
                .stream(allUsers.spliterator(), false)
                    .map(x->x.visitsAmount)
                    .reduce(0, Integer::sum);
    }

    public long visitsBy(String name) {
        Optional<User> user = visitorStatisticsRepository.findById(name);
        if (user.isEmpty()) {
            return 0L;
        } else {
            User currentUser = user.get();
            return currentUser.visitsAmount;
        }
    }

}
