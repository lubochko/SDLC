package by.bsuir.typepulse.web;

import by.bsuir.typepulse.auth.TypePulseUserDetails;
import by.bsuir.typepulse.repository.ResultRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {

    private final ResultRepository resultRepository;

    public HistoryController(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal TypePulseUserDetails principal, Model model) {
        model.addAttribute("results",
                resultRepository.findTop50ByUserIdOrderByFinishedAtDesc(principal.getUser().getId()));
        return "history";
    }
}
