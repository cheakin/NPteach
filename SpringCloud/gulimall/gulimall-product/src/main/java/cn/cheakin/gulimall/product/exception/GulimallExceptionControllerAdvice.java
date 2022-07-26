package cn.cheakin.gulimall.product.exception;

import cn.cheakin.common.exception.BizCodeEnum;
import cn.cheakin.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理所有异常
 * Create by botboy on 2022/07/26.
 **/
@Slf4j
@ResponseBody
@ControllerAdvice(basePackages = "cn.cheakin.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = Exception.class) // 也可以返回ModelAndView
    public R handleValidException(MethodArgumentNotValidException exception) {

        Map<String, String> map = new HashMap<>();
        // 获取数据校验的错误结果
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            map.put(field, message);
        });

//        log.error("数据校验出现问题{},异常类型{}", exception.getMessage(), exception.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
//        log.error("未知异常{},异常类型{}", throwable.getMessage(), throwable.getClass());
        return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(), BizCodeEnum.UNKNOW_EXEPTION.getMsg());
    }

}
