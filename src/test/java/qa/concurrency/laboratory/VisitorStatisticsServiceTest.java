package qa.concurrency.laboratory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorStatisticsServiceTest {
    private VisitorStatisticsService visitorStatisticsService;

    @BeforeEach
    void setUp() {
        visitorStatisticsService = new VisitorStatisticsService();
    }

    @Test
    void visitSingleUserTotalVisits() {
        String userName = "Forever Alone";
        visitorStatisticsService.visit(userName);
        assertEquals(visitorStatisticsService.totalVisits(), 1L);
    }

    @Test
    void visitSingleUserVisitBy() {
        String userName = "Forever Alone";
        visitorStatisticsService.visit(userName);
        assertEquals(visitorStatisticsService.visitsBy(userName), 1L);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 15, 1000})
    void handlingConcurrencyInTotalVisits(int visitorsAmount) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(visitorsAmount);
        for (int i = 0; i < visitorsAmount; i++) {
            service.submit(() -> {
                visitorStatisticsService.visit("Concurrent User");
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(visitorsAmount, visitorStatisticsService.totalVisits());
    }

    @Test
    void trackingUsers() throws InterruptedException {
        List<UserVisitMap> visitUserMaps = new LinkedList<>();
        visitUserMaps.add(new UserVisitMap("Gandalf", 12));
        visitUserMaps.add(new UserVisitMap("Frodo", 21));
        visitUserMaps.add(new UserVisitMap("Sauron", 4));

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