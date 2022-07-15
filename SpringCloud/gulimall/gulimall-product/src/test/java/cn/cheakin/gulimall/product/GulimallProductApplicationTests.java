package cn.cheakin.gulimall.product;

import cn.cheakin.gulimall.product.dao.BrandDao;
import cn.cheakin.gulimall.product.entity.BrandEntity;
import cn.cheakin.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void add() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");

        brandService.save(brandEntity);
        System.out.println("保存完成");
    }

    @Test
    void update() {
        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setBrandId(1L);
        brandEntity.setDescript("华为手机");

        brandService.updateById(brandEntity);
    }

    @Test
    void quey() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach(item -> {
            System.out.println("item = " + item);
        });
    }

}
