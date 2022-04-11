package qa.concurrency.laboratory.service;

import org.springframework.validation.annotation.Validated;

@Validated
public interface VisitorStatisticService {
    void visit(String name);

    long totalVisits();

    long visitsBy(String name);
}
