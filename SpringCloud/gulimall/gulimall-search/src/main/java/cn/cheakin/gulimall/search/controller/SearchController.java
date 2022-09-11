package cn.cheakin.gulimall.search.controller;

import cn.cheakin.gulimall.search.service.MallSearchService;
import cn.cheakin.gulimall.search.vo.SearchParam;
import cn.cheakin.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by botboy on 2022/09/07.
 **/
@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * 自动将页面提交过来的所有请求参数封装成我们指定的对象
     */
    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.setQueryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);

        model.addAttribute("result", result);

        return "list";
    }

    /*@GetMapping("/list.html")
    public String listPage() {
        return "list";
    }*/

}
