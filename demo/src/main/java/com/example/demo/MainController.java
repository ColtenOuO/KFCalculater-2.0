package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    @RequestMapping("/MainPage")
    public String index(@RequestParam(value="title", required=false, defaultValue="") String title, Model model) {
        model.addAttribute("name", title);
        return "index";  // src/main/resources/templates/index.html
    }
}
 