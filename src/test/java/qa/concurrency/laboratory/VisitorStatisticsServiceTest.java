package qa.concurrency.laboratory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import qa.concurrency.laboratory.model.UserVisitMap;
import qa.concurrency.laboratory.repository.VisitorStatisticsRepository;
import qa.concurrency.laboratory.service.VisitorStatisticServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=QaLaboratoryApplication.class)
@Transactional
class VisitorStatisticsServiceTest {
    @Autowired
    private VisitorStatisticsRepository visitorStatisticsRepository;
    private VisitorStatisticServiceImpl visitorStatisticsService;

    @BeforeEach
    void setUp() {
        visitorStatisticsService = new VisitorStatisticServiceImpl(visitorStatisticsRepository);
    }

    @Test
    void visitSingleUserVisitBy() {
        String userName = "Forever Alone";
        visitorStatisticsService.visit(userName);
        assertEquals(1L, visitorStatisticsService.visitsBy(userName));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 15, 1000})
    void testTotalVisits(int visitorsAmount) {
        for (int i = 0; i < visitorsAmount; i++) {
            visitorStatisticsService.visit("User");
        }
        assertEquals(visitorsAmount, visitorStatisticsService.totalVisits());
    }

    @Test
    void trackingUsers() throws InterruptedException {
        List<UserVisitMap> visitUserMaps = new LinkedList<>();
        visitUserMaps.add(new UserVisitMap("Gandalf", 120));
        visitUserMaps.add(new UserVisitMap("Frodo", 210));
        visitUserMaps.add(new UserVisitMap("Sauron", 40));

        AtomicReference<Integer> totalVisits = new AtomicReference<>(0);
        visitUserMaps.forEach(x -> totalVisits.updateAndGet(v -> v + x.visitsAmount));
        Long newTotalVisits = totalVisits.get().longValue();
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalVisits.get());
        Integer totalVisitsRemain = totalVisits.get();
        while (totalVisitsRemain > 0) {
            int userId = (int) (Math.random() * 3);
            UserVisitMap currentUser;
            while (visitUserMaps.get(userId).visitsRemain <= 0) {
                userId = (int) (Math.random() * 3);
            }
            currentUser = visitUserMaps.get(userId);
            service.submit(() -> {
                visitorStatisticsService.visit(currentUser.userName);
                latch.countDown();
            });
            currentUser.visitsRemain--;
            totalVisitsRemain--;
        }
        latch.await();

        for (UserVisitMap user :
                visitUserMaps) {
            Long amount = Long.valueOf(user.visitsAmount);
            assertEquals(visitorStatisticsService.visitsBy(user.userName), amount);
        }
        assertEquals(visitorStatisticsService.totalVisits(), newTotalVisits);
    }
}