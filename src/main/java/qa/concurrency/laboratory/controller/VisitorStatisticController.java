package qa.concurrency.laboratory.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import qa.concurrency.laboratory.service.VisitorStatisticService;
import qa.concurrency.laboratory.model.User;

@Controller
@RequestMapping("/")
public class VisitorStatisticController {
    private final VisitorStatisticService visitorStatisticsService;

    public VisitorStatisticController(VisitorStatisticService visitorStatisticsService) {
        this.visitorStatisticsService = visitorStatisticsService;
    }

    @GetMapping("/visit")
    public String greetingForm(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "visit";
    }

    @PostMapping("/visit")
    public String userVisit(@ModelAttribute @NotNull User user, Model model) {
        visitorStatisticsService.visit(user.getUserName());
        return "visit";
    }
}
