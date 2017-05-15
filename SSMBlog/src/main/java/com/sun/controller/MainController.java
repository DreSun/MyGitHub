package com.sun.controller;

/**
 * Created by DreSun on 2017/3/31.
 */

import com.sun.model.UserEntity;
import com.sun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *  MVC框架有model、view、controller三部分组成。
 *  model一般为一些基本的Java Bean，view用于进行相应的页面显示，controller用于处理网站的请求。
 * */
/*
 *
 *（1）@Controller注解：采用注解的方式，可以明确地定义该类为处理请求的Controller类；

 *（2）@RequestMapping()注解：用于定义一个请求映射，value为请求的url，值为 /
      说明，该请求首页请求，method用以指定该请求类型，一般为get和post；

 *（3）return "index"：处理完该请求后返回的页面，此请求返回 index.jsp页面。
 * */
@Controller
public class MainController {
    /**
     * 自动装配：相当于数据库操作的极简化，只要定义了就可以直接进行数据库操作，不用再去管开启连接、关闭连接等问题

     找到所有记录：使用JpaRepository的默认方法findAll()。

     modelMap：用于将controller方法里面的参数传递给所需的jsp页面，以进行相关显示。
     * */
    // 自动装配数据库接口，不需要再写原始的Connection来操作数据库
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public String getUsers(ModelMap modelMap) {
        // 查询user表中所有记录
        List<UserEntity> userList = userRepository.findAll();

        // 将所有记录传递给要返回的jsp页面，放在userList当中
        modelMap.addAttribute("userList", userList);

        // 返回pages目录下的admin/users.jsp页面
        return "admin/users";
    }
    // get请求，访问添加用户 页面
    @RequestMapping(value = "/admin/users/add", method = RequestMethod.GET)
    public String addUser() {
        // 转到 admin/addUser.jsp页面
        return "admin/addUser";
    }

    // post请求，处理添加用户请求，并重定向到用户管理页面
    /**
     * /admin/users/add请求：get请求转到添加用户页面

     /admin/users/addP请求：post请求收集数据并存库

     @ModelAttribute注解：收集post过来的数据（在此，相当于post过来了一整个userEntity，不用一个一个地取）

     save()和saveAndFlush()：save()方法处理完毕后，数据依然在缓冲区未写入数据库，使用saveAndFlush()可以立即刷新缓冲区，写入数据库

     redirect:/admin/users：这里使用重定向，可以让该方法重定向访问一个请求，ruturn之后，将跳转到 :/admin/users 所访问的页面。
     * */
    /**
     * <form:form>标签：使用Spring的form标签，可以方便的收集整块数据，commondName=“user”说明form内的内容都保存在这个user实例中，
     * 然后将整个user实例传递给controller处理。在所有的input标签中，name一定要与UserEntity中的成员相同，不然无法找到。
     在提交之后，后台将会处理 /admin/users/addP 请求。
     * */
    @RequestMapping(value = "/admin/users/addP", method = RequestMethod.POST)
    public String addUserPost(@ModelAttribute("user") UserEntity userEntity) {
        // 注意此处，post请求传递过来的是一个UserEntity对象，里面包含了该用户的信息
        // 通过@ModelAttribute()注解可以获取传递过来的'user'，并创建这个对象

        // 数据库中添加一个用户，该步暂时不会刷新缓存
        //userRepository.save(userEntity);

        // 数据库中添加一个用户，并立即刷新缓存
        userRepository.saveAndFlush(userEntity);

        // 重定向到用户管理页面，方法为 redirect:url
        return "redirect:/admin/users";
    }
    // 查看用户详情
// @PathVariable可以收集url中的变量，需匹配的变量用{}括起来
// 例如：访问 localhost:8080/admin/users/show/1 ，将匹配 id = 1
    @RequestMapping(value = "/admin/users/show/{id}", method = RequestMethod.GET)
    public String showUser(@PathVariable("id") Integer userId, ModelMap modelMap) {

        // 找到userId所表示的用户
        UserEntity userEntity = userRepository.findOne(userId);

        // 传递给请求页面
        modelMap.addAttribute("user", userEntity);
        return "admin/userDetail";
    }
    // 更新用户信息 页面
    @RequestMapping(value = "/admin/users/update/{id}", method = RequestMethod.GET)
    public String updateUser(@PathVariable("id") Integer userId, ModelMap modelMap) {

        // 找到userId所表示的用户
        UserEntity userEntity = userRepository.findOne(userId);

        // 传递给请求页面
        modelMap.addAttribute("user", userEntity);
        return "admin/updateUser";
    }

    // 更新用户信息 操作
    @RequestMapping(value = "/admin/users/updateP", method = RequestMethod.POST)
    public String updateUserPost(@ModelAttribute("userP") UserEntity user) {

        // 更新用户信息
        userRepository.updateUser(user.getNickname(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getId());
        userRepository.flush(); // 刷新缓冲区
        return "redirect:/admin/users";
    }
    // 删除用户
    @RequestMapping(value = "/admin/users/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Integer userId) {

        // 删除id为userId的用户
        userRepository.delete(userId);
        // 立即刷新
        userRepository.flush();
        return "redirect:/admin/users";
    }
}