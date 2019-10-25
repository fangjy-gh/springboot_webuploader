package controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/11
 * 描述
 */
@Controller
@SpringBootApplication
public class HelloController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/hello.do")
    @ResponseBody
    public String hello(){
        return "hello springboot";
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloController.class,args);
    }

}
