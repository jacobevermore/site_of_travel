package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.impl.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao dao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        Jedis jedis = JedisUtil.getJedis();
        List<Category> cs ;
//        Set<String> category = jedis.zrange("category", 0, -1);
        Set<Tuple> category = jedis.zrangeWithScores("category", 0, -1);


        if (category == null || category.size() == 0) {
            //空，从数据库查询
            System.out.println("从数据库查询");
            cs = dao.findAll();
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category", cs.get(i).getCid(), cs.get(i).getCname());

            }


        } else {
            System.out.println("从缓存查询");

            cs = new ArrayList<Category>();
            for (Tuple tuple : category) {
                Category c = new Category();
                c.setCname(tuple.getElement());
                c.setCid((int) tuple.getScore());
                cs.add(c);
            }


        }

        //从redis中查询
        return cs;
    }
}
