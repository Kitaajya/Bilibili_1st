package org.designer.bilibili_1st.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.bilibili_1st.mapper.PurchaseMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    //==============================最高/商家权限======================================
    //查看所有商品
    public List<Map<String,Object>> selectAllProduct(){
        if(purchaseMapper.selectAllProduct().isEmpty()) log.info("商品列表为空");
        return purchaseMapper.selectAllProduct();
    }
    //根据id查询商品
    public List<Map<String,Object>> selectProductById(int id){
        if(purchaseMapper.selectProductById(id).isEmpty()) log.info("不存在此商品");
        return purchaseMapper.selectProductById(id);
    }
    //查看购物者的信息
    public List<Map<String,Object>> selectUserInformation(){
        if(purchaseMapper.selectUserInformationThatPurchased().isEmpty()) log.info("此人不存在");
        return purchaseMapper.selectUserInformationThatPurchased();
    }
    //添加商品
    public Map<String,Object> addProduct(String productName, BigDecimal price, Integer quantity, BigDecimal total){
        int k= purchaseMapper.addProduct(productName,price,quantity,total);
        if(k==0) return Map.of("success",false,"message","添加失败");
        return Map.of("success",true,"message","添加成功");
    }
    //修改商品价格
    public Map<String,Object> editPrice(int id,String name,BigDecimal newPrice){
        int k=purchaseMapper.editPrice(id,name,newPrice);
        if(k==0) return Map.of("success",false,"message","修改失败");
        return Map.of("success",true,"message","修改成功");
    }
    //修改商品名称
    public Map<String,Object> editProductName(int id,String newName){
        int k=purchaseMapper.editProductName(id,newName);
        if(k==0) return Map.of("success",false,"message","修改失败");
        return Map.of("success",true,"message","修改成功");
    }
    //删除商品
    public Map<String,Object> deleteProduct(int id,String name){
        int k=purchaseMapper.deleteProduct(id,name);
        if(k==0) return Map.of("success",false,"message","删除失败");
        return Map.of("success",true,"message","删除成功");
    }
    //添加宣传标语
    public Map<String,Object> addSpread(int id,String productName,String text){
        int k=purchaseMapper.addSpread(id,productName,text);
        if(k<=0) return Map.of("success",false,"message","添加失败");
        return Map.of("success",true,"message","添加成功");
    }
    //删除宣传标语
    public Map<String,Object> deleteSpread(int id,String productName,String text) {
        int k=purchaseMapper.deleteSpread(id, productName, text);
        if(k<=0) return Map.of("success",false,"message","删除失败");
        return Map.of("success",true,"message","删除成功");
    }
    //修改宣传标语
    public Map<String,Object> editSpread(int id,String productName,String text){
        int k=purchaseMapper.editSpread(id, productName, text);
        if(k<=0) return Map.of("success",false,"message","修改失败");
        return Map.of("success",true,"message","修改成功");
    }
    //=========================================买家权限================================================
    //通过商品id和商品名得到商品价格
    public List<Map<String,Object>> selectPrice(int id,String name){
        if(purchaseMapper.selectPrice(id,name).isEmpty()) log.info("此商品不存在");
        return purchaseMapper.selectPrice(id,name);
    }
    //通过价格筛选商品，最高价格和最低价格由用户指定搜索商品
    public List<Map<String,Object>> selectProductByPrice(BigDecimal maxPrice, BigDecimal minPrice){
        return purchaseMapper.selectProductByPrice(maxPrice,minPrice);
    }
    //买家查看订单
    public List<Map<String,Object>> selectOrder(){
        if(purchaseMapper.selectOrder().isEmpty()) log.info("买家已退单或买家不存在");
        return purchaseMapper.selectOrder();
    }
    /**
     * 买家写评价，在商品信息号为id、用户id为userId的情况下编写contents内容
     * @param contents 评价内容
     * @param userId 用户ID
     * @param id 商品订单ID
     * @return 影响行数
     */
    //写评论
    public Map<String,Object> writeEvaluation(String contents, int userId, int id) {
        int k=purchaseMapper.writeEvaluation(contents,userId,id);
        if(k==0) return Map.of("success",false,"message","评论失败");
        return Map.of("success",true,"message","评论成功");
    }
    //改自己的评论
    public Map<String,Object> editEvaluation(String contents, int userId, int id){
        int k= purchaseMapper.editEvaluation(contents,userId,id);
        if(k==0) return Map.of("success",false,"message","修改失败");
        return Map.of("success",true,"message","修改成功");
    }
    //删自己的评论
    public Map<String,Object> deleteMyEvaluation(int userId, int id) {
        int k= purchaseMapper.deleteMyEvaluation(userId,id);
        if(k==0) return Map.of("success",false,"message","删除失败");
        return Map.of("success",true,"message","删除成功");
    }
    //查看自己的评论
    public List<Map<String,Object>> selectEvaluation(int id,int userId){
        if(purchaseMapper.selectEvaluation(id,userId).isEmpty()) log.info("评论不存在或已被删除");
        return purchaseMapper.selectEvaluation(id,userId);
    }
    //查看商品id为id的所有评论
    public List<Map<String,Object>> selectAllEvaluations(int id){
        return purchaseMapper.selectAllEvaluations(id);
    }
}
