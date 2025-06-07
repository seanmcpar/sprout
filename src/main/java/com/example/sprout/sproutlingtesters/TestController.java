package com.example.sprout.sproutlingtesters;

import com.example.sprout.annotations.ioc.InjectDependencies;
import com.example.sprout.annotations.ioc.sproutling.Controller;
import com.example.sprout.annotations.rest.GetMapping;

@Controller
public class TestController {

    private final TestService testService;

    @InjectDependencies
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test")
    public String getTest() {
        return "GET Test Success!";
    }


}
