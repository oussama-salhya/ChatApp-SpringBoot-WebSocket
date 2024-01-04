package ma.ouss.mychatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class pageController {

     @GetMapping("/")
        public String home() {
            return "index";
        }
}
