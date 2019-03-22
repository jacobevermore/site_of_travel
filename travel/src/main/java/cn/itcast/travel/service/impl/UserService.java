package cn.itcast.travel.service.impl;

import cn.itcast.travel.domain.User;

public interface UserService {
    boolean regist(User user);

    boolean active(String code);

    User login(User user);
}
