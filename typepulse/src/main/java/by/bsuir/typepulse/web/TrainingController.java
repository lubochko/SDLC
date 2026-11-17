package by.bsuir.typepulse.web;

import by.bsuir.typepulse.auth.TypePulseUserDetails;
import by.bsuir.typepulse.domain.Difficulty;
import by.bsuir.typepulse.domain.Language;
import by.bsuir.typepulse.domain.Result;
import by.bsuir.typepulse.domain.TextSnippet;
import by.bsuir.typepulse.domain.TrainingSession;
import by.bsuir.typepulse.repository.ResultRepository;
import by.bsuir.typepulse.training.TrainingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TrainingController {

    private final TrainingService trainingService;
    private final ResultRepository resultRepository;

    public TrainingController(TrainingService trainingService, ResultRepository resultRepository) {
        this.trainingService = trainingService;
        this.resultRepository = resultRepository;
    }

    @GetMapping("/training/setup")
    public String setup(Model model) {
        model.addAttribute("languages", Language.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("durations", new int[]{30, 60, 120});
        return "training-setup";
    }

    @PostMapping("/training/start")
    public String start(@AuthenticationPrincipal TypePulseUserDetails principal,
                        @RequestParam Language language,
                        @RequestParam Difficulty difficulty,
                        @RequestParam int durationSec,
                        RedirectAttributes redirectAttributes) {
        try {
            TrainingSession session = trainingService.start(
                    principal.getUser().getId(), language, difficulty, durationSec);
            return "redirect:/training/" + session.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/training/setup";
        }
    }

    @GetMapping("/training/{id}")
    public String training(@PathVariable Long id,
                           @AuthenticationPrincipal TypePulseUserDetails principal,
                           Model model) {
        TrainingSession session = trainingService.requireSession(id);
        if (!session.getUserId().equals(principal.getUser().getId())) {
            return "redirect:/training/setup";
        }
        TextSnippet text = trainingService.requireText(session.getTextId());
        model.addAttribute("session", session);
        model.addAttribute("text", text.getBody());
        return "training";
    }

    @PostMapping("/training/{id}/finish")
    public String finish(@PathVariable Long id,
                         @AuthenticationPrincipal TypePulseUserDetails principal,
                         @RequestParam String typedText,
                         @RequestParam long elapsedMs,
                         RedirectAttributes redirectAttributes) {
        try {
            Result result = trainingService.finish(id, principal.getUser().getId(), typedText, elapsedMs);
            return "redirect:/training/result/" + result.getId();
        } catch (RuntimeException ex) {
            trainingService.cancel(id, principal.getUser().getId());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/training/setup";
        }
    }

    @PostMapping("/training/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         @AuthenticationPrincipal TypePulseUserDetails principal,
                         RedirectAttributes redirectAttributes) {
        trainingService.cancel(id, principal.getUser().getId());
        redirectAttributes.addFlashAttribute("error", "Сеанс отменён. Успешный результат не сохранён.");
        return "redirect:/training/setup";
    }

    @GetMapping("/training/result/{resultId}")
    public String result(@PathVariable Long resultId,
                         @AuthenticationPrincipal TypePulseUserDetails principal,
                         Model model) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Результат не найден"));
        if (!result.getUserId().equals(principal.getUser().getId())) {
            return "redirect:/training/setup";
        }
        model.addAttribute("result", result);
        return "result";
    }
}
