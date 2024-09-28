package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class HelloWorldController {

    public HelloWorldController() {
    }

    @GetMapping("/hello")
    public String helloWorld() {
        var resultMap = new HashMap<String, List<String>>();
//        template.query("SELECT * FROM widgets",
//                (RowMapper<Object>) (rs, rowNum) ->
//                        resultMap.put(Integer.toString(rs.getRow()), List.of(
//                                rs.getString("id"),
//                                rs.getString("name"),
//                                rs.getString("purpose"))
//                        )
//        );

        System.out.println(resultMap);
        return "Hello not quarkus!!";
    }
}
