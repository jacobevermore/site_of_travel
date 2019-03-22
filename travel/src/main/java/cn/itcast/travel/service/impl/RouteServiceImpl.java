package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.impl.*;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;

import java.util.List;

public class RouteServiceImpl implements RouteService{

        private SellerDao sellerdao = new SellerDaoImpl();
    private RouteDao rd = new RouteDaoImpl();
    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize, String rname) {

        PageBean<Route> pb = new PageBean<>();
        pb.setCurrentPage(currentPage);
        pb.setPageSize(pageSize);

        //设置总记录数
        int totalCount = rd.findTotalCount(cid,rname);
        pb .setTotalCount(totalCount);

        int start = (currentPage-1)*pageSize;
            //设置当前页显示的数据集合
        List<Route> list = rd.findByPage(cid,start,pageSize,rname);
        pb.setList(list);
        //设置总页数
        int totalPage = (totalCount  % pageSize ==0) ? (totalCount  / pageSize) :(totalCount  / pageSize )+1;
        pb.setTotalPage(totalPage);

            return pb;
    }
    private RouteImgDao rid1 = new RouteImgDaoImpl();

    @Override
    public Route findOne(String rid) {
        Route route = rd.findOne(Integer.parseInt(rid));
        List<RouteImg> byRId = rid1.findByRId(route.getRid());
        route.setRouteImgList(byRId);
        Seller sellernew = sellerdao.findById(route.getSid());
        route.setSeller(sellernew);
        return route;
    }
}
