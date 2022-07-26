package cn.cheakin.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {  // 实现时可以指定不同的类型
    private Set<Integer> set=new HashSet<>();

    // 初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] value = constraintAnnotation.vals();
        for (int val : value) {
            set.add(val);
        }
    }

    /**
     * 判断是否校验成功
     * @param value 需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return  set.contains(value);
    }
}