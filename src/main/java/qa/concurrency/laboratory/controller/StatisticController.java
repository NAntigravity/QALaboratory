package qa.concurrency.laboratory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qa.concurrency.laboratory.service.VisitorStatisticService;

@RestController
@RequestMapping("/stat")
public class StatisticController {
    private final VisitorStatisticService visitorStatisticsService;

    public StatisticController(VisitorStatisticService visitorStatisticsService) {
        this.visitorStatisticsService = visitorStatisticsService;
    }

    @GetMapping("/total")
    public String getTotalVisitsCount() {
        return String.valueOf(visitorStatisticsService.totalVisits());
    }
}
