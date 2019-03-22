package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.impl.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService{


    private UserDao userDao = new UserDaoImpl();

    @Override
    public boolean regist(User user) {
        //根据用户名查询用户对象
        User u = userDao.findByUsername(user.getUsername());

        if(u !=null){

            return false;
        }

        user.setStatus("N");
        user.setCode(UuidUtil.getUuid());


            userDao.save(user);

        String content="<a href='http://localhost:8081/travel/user/active?code="+user.getCode()+"'>点击激活【黑马旅游网】</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");


        return true;

    }

    @Override
    public boolean active(String code) {
        User user = userDao.findByCode(code);
            if(user!=null){

                userDao.updateStatus(user);
                return true;
            }else{

                return false;
            }

    }

    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
