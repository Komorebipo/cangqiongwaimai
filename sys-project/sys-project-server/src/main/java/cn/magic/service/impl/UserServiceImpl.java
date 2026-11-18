package cn.magic.service.impl;

import cn.magic.entity.User;
import cn.magic.mapper.UserMapper;
import cn.magic.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//用户服务实现类
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}