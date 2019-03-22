package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.impl.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

  private  UserService service = new UserServiceImpl();

    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String check = request.getParameter("check");
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        if( checkcode_server==null||!checkcode_server.equalsIgnoreCase(check) ){
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误！");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            //将json数据写回客户端
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;

        }


        Map<String, String[]> map = request.getParameterMap();

        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        boolean flag = service.regist(user);

        ResultInfo info = new ResultInfo();
        if(flag){
            info.setFlag(true);

        }else{
            info.setFlag(false);
            info.setErrorMsg("注册失败");

        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        //将json数据写回客户端
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);



    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //调用service查询
        User u =service.login(user);
        ResultInfo info = new ResultInfo();
        //判断用户对象是否是null
        if(u==null){
            //用户名密码错误
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误");
        }
        if (u!=null&& !"Y".equals(u.getStatus())){
            //判断用户是否激活
            //用户尚未激活
            info.setFlag(false);
            info.setErrorMsg("您尚未激活，请激活");
        }
        if (u!=null&&"Y".equals(u.getStatus())){
            request.getSession().setAttribute("user",u);
            //在session中存储user对象
            info.setFlag(true);
        }

        //响应数据
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),info);



    }
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object user = request.getSession().getAttribute("user");
        writeValue(user,response);
    }
    protected void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //销毁session
        request.getSession().invalidate();
        response.setContentType("text/html;charset=utf-8");
        response.sendRedirect(request.getContextPath()+"/login.html");
    }
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code != null) {

            boolean flag = service.active(code);

            String msg ;

            if (flag) {

                msg = "激活成功,请<a href= 'login.html'>登录</a>";
            } else {
                msg = "激活失败，请联系管理员";

            }

            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

}
