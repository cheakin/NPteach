package cn.cheakin.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Create by botboy on 2022/09/07.
 **/
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }

}
