```
   _____                       _   
/ ____|                     | |  
| (___  _ __  _ __ ___  _   _| |_
\___ \| '_ \| '__/ _ \| | | | __|
____) | |_) | | | (_) | |_| | |_
|_____/| .__/|_|  \___/ \__,_|\__|
| |                        
|_|                        
```
```
🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱
A Spring-Inspired micro framework
with Dependency Injection
and REST request mapping
🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱🌱
```
```
@SproutApplication
public class SproutApplicationMain {

    public static void main(String[] args) {
        SproutApplicationRunner.run(SproutApplicationMain.class);
    }

}

...

@Controller
public class TestController {

    private final TestService testService;

    @InjectDependencies
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test")
    public String getTest() {
        return "Success!";
    }


}

```