package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pro.jgsu.common.R;
import com.pro.jgsu.entity.User;
import com.pro.jgsu.service.UserService;
import com.pro.jgsu.utils.MailUtils;
import com.pro.jgsu.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取邮箱验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        //获得前端传来的邮箱
        String phone = user.getPhone();

        if(StringUtils.isEmpty(phone)){
            return R.error("邮件发送失败，请稍后再试！");
        }
        //生成四位随机校验码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        //发送邮件
        //MailUtils.sendMail(phone,"【井冈山外卖】您正在登录井冈山外卖，验证码为" + code + "有效期五分钟,如非本人操作，请忽略本邮件！",
        //        "井冈山外卖验证码");
        //将校验码存入session中(已弃用，改用redis缓存)
        //session.setAttribute("code",code);

        //将生成的验证码存入 redis 缓存，并设置有效期为五分钟
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
        //log.info("code={}",code);
        String codeInRedis = redisTemplate.opsForValue().get(phone).toString();
        log.info("redis 中的验证码{}",codeInRedis);

        return R.success("已向您的邮箱发送验证码，请注意查收！");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //log.info(map.toString());
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //获取session中保存的验证码(已弃用，改用 redis 缓存)
        //String codeInSession = session.getAttribute("code").toString();

        //从 redis 中获取验证码
        String codeInSession = redisTemplate.opsForValue().get(phone).toString();

        //将两个code进行对比
        if(codeInSession != null && codeInSession.equals(code)){
            //相同则登录成功
            //判断当前用户是否为新用户，
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //如果为新用户则注册为新用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            //用户登录成功，将用户信息存入session
            session.setAttribute("user",user.getId());
            //删除 redis 中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }

}
