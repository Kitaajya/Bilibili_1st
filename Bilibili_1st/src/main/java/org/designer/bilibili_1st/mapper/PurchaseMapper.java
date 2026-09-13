package org.designer.bilibili_1st.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Repository
@RequiredArgsConstructor
public class PurchaseMapper {
    private final JdbcTemplate jdbcTemplate;
    //==============================最高/商家权限======================================
    //查看所有商品
    public List<Map<String,Object>> selectAllProduct(){
        return jdbcTemplate.queryForList("SELECT * FROM bilibili_purchase");
    }
    //根据商品id查询商品
    public List<Map<String,Object>> selectProductById(int id){
        return jdbcTemplate.queryForList("SELECT * FROM bilibili_purchase WHERE id=?",id);
    }
    //查看购物者的信息
    public List<Map<String,Object>> selectUserInformationThatPurchased(){
        return jdbcTemplate.queryForList("SELECT user_id,product_name,quantity,total FROM bilibili_purchase");
    }
    //添加商品
    public int addProduct(String productName, BigDecimal price, Integer quantity, BigDecimal total){
        String sql = "INSERT INTO bilibili_purchase(product_name, price, quantity, total) VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, productName, price, quantity, total);
    }
    //修改商品价格
    public int editPrice(int id,String name,BigDecimal newPrice){
        return jdbcTemplate.update("UPDATE bilibili_purchase SET price=? WHERE id=? AND product_name=?",newPrice,id,name);
    }
    //修改商品名称
    public int editProductName(int id,String newName){
        return jdbcTemplate.update("UPDATE bilibili_purchase SET product_name=? WHERE id=?", newName,id);
    }
    //删除商品
    public int deleteProduct(int id,String name){
        return jdbcTemplate.update("DELETE FROM bilibili_purchase WHERE id=? AND product_name=?",id,name);
    }

    //=========================================买家权限================================================
    //通过商品id或商品名得到商品价格
    public List<Map<String,Object>> selectPrice(int id,String name){
        return jdbcTemplate.queryForList("SELECT price FROM bilibili_purchase WHERE id=? OR product_name=?",id,name);
    }
    //通过价格筛选商品，最高价格和最低价格由用户指定搜索商品
    public List<Map<String,Object>> selectProductByPrice(BigDecimal maxPrice, BigDecimal minPrice){
        return jdbcTemplate.queryForList("SELECT * FROM bilibili_purchase WHERE price>= ? AND price<= ?",minPrice,maxPrice);
    }
    //买家查看订单
    public List<Map<String,Object>> selectOrder(){
        return jdbcTemplate.queryForList("SELECT * FROM bilibili_purchase");
    }
    /**
     * 买家写评价，在商品信息号为id、用户id为userId的情况下编写contents内容
     * @param contents 评价内容
     * @param userId 用户ID
     * @param id 商品订单ID
     * @return 影响行数
     */
    //写评论
    public int writeEvaluation(String contents, int userId, int id) {
        return jdbcTemplate.update(
                "UPDATE bilibili_purchase SET contents= ? WHERE user_id= ? AND id = ?",
                contents, userId, id);
    }
    public int editEvaluation(String contents, int userId, int id){
        return jdbcTemplate.update("UPDATE bilibili_purchase SET contents=? WHERE user_id=? AND id=?",
                contents,userId,id);
    }
    //删自己的评论
    public int deleteMyEvaluation(int userId, int id) {
        return jdbcTemplate.update(
                "UPDATE bilibili_purchase SET contents = NULL WHERE user_id = ? AND id = ?",
                userId, id);
    }
    //查看自己的评论
    public List<Map<String,Object>> selectEvaluation(int id,int userId){
        return jdbcTemplate.queryForList("SELECT contents FROM bilibili_purchase WHERE id=? AND user_id=?",id,userId);
    }
    //查看商品id为id的所有评论
    public List<Map<String,Object>> selectAllEvaluations(int id){
        return jdbcTemplate.queryForList("SELECT contents FROM bilibili_purchase WHERE id=?", id);
    }
}