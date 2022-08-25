package cn.cheakin.gulimall.product.web;

import cn.cheakin.gulimall.product.entity.CategoryEntity;
import cn.cheakin.gulimall.product.service.CategoryService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create by botboy on 2022/08/24.
 **/
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})   // thymeleaf中自动自动配置前缀`classpath:/templates/`
    public String indexPage(Model model) {

        // TODO 1.查出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        model.addAttribute("categorys", categoryEntities);
        return "index"; // thymeleaf中自动配置后缀`.html`
    }
}
