package com.dfoff.demo.Controller;


import lombok.AllArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
public class MainController {

    @GetMapping("/")
    public ModelAndView main() throws ParseException {
        return new ModelAndView("index");
    }

}
