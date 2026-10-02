package py.edu.uc.lp3.ja_taller_git_2026.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "API Minecraft funcionando correctamente";
    }
}
