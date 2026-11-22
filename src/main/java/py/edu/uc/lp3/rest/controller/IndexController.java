package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Estado del servicio", description = "Confirma que el servicio está vivo.")
@RestController
public class IndexController {

    @Operation(summary = "Comprobar que el servicio responde")
    @GetMapping("/")
    public String index() {
        return "API Minecraft funcionando correctamente";
    }
}
