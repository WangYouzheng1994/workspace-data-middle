package org.jeecg.yqwl.datamiddle.demo.controller;

import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/5/6 16:54
 * @Version: V1.0
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @PostMapping("/test/demo")
    public Result testDemo() {
        return Result.error("错误了哈");
    }
}
