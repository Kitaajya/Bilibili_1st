package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.entity.PurchaseEntity;
import org.designer.bilibili_1st.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
@RequestMapping("/api/purchase/controller")
@RestController
public class PurchaseController {
    public Logger log= LoggerFactory.getLogger(PurchaseEntity.class);
    private final PurchaseService purchaseService;
    //==============================最高/商家权限======================================
    //查看所有商品
    @GetMapping("/select/all/product")
    public List<Map<String,Object>> selectAllProduct(){
        if(purchaseService.selectAllProduct().isEmpty()) log.info("商品为空");
        return purchaseService.selectAllProduct();
    }
    @GetMapping("/select/all/product/id")
    //根据id查询商品
    public List<Map<String,Object>> selectProductById(@RequestParam int id){
        if(purchaseService.selectProductById(id).isEmpty())
            log.info("商品不存在");
        return purchaseService.selectProductById(id);
    }
    @GetMapping("/select/user")
    //查看购物者的信息
    public List<Map<String,Object>> selectUserInformation() {
        if(purchaseService.selectUserInformation().isEmpty())
            log.info("用户不存在");
        return purchaseService.selectUserInformation();
    }
    @PostMapping("/add/product")
    //添加商品
    public Map<String,Object> addProduct(@RequestParam String productName,@RequestParam BigDecimal price,@RequestParam Integer quantity,@RequestParam BigDecimal total) {
        return purchaseService.addProduct(productName,price,quantity,total);
    }
    @PostMapping("/edit/price")
    //修改商品价格
    public Map<String,Object> editPrice(@RequestParam int id,@RequestParam String name,@RequestParam BigDecimal newPrice) {
        return purchaseService.editPrice(id,name,newPrice);
    }
    @PostMapping("/edit/product/name")
    //修改商品名称
    public Map<String,Object> editProductName(@RequestParam int id,@RequestParam String newName) {
        return purchaseService.editProductName(id,newName);
    }
    @DeleteMapping("/delete/product")
    //删除商品
    public Map<String,Object> deleteProduct(@RequestParam int id,@RequestParam String name) {
        return purchaseService.deleteProduct(id,name);
    }
    //=========================================买家权限================================================
    @GetMapping("/select/price")
    //通过商品id和商品名得到商品价格
    public List<Map<String,Object>> selectPrice(@RequestParam int id,@RequestParam String name) {
        if(purchaseService.selectPrice(id,name).isEmpty())
            log.info("商品不存在！");
        return purchaseService.selectPrice(id,name);
    }
    @GetMapping("/select/m/price")
    //通过价格筛选商品，最高价格和最低价格由用户指定搜索商品
    public List<Map<String,Object>> selectProductByPrice(@RequestParam BigDecimal maxPrice,@RequestParam BigDecimal minPrice) {
        if (purchaseService.selectProductByPrice(maxPrice, minPrice).isEmpty())
            log.info("商品不存在");
        return purchaseService.selectProductByPrice(maxPrice, minPrice);
    }
    @GetMapping("/select/order")
    //买家查看订单
    public List<Map<String,Object>> selectOrder() {
        if(purchaseService.selectOrder().isEmpty())
            log.info("商品不存在");
        return purchaseService.selectOrder();
    }
    /**
     * 买家写评价，在商品信息号为id、用户id为userId的情况下编写contents内容
     * @param contents 评价内容
     * @param userId 用户ID
     * @param id 商品订单ID
     * @return 影响行数
     */
    @PostMapping("/write/evaluation")
    //写评论
    public Map<String,Object> writeEvaluation(@RequestParam String contents, @RequestParam int userId,@RequestParam int id) {
        return purchaseService.writeEvaluation(contents, userId, id);
    }
    @PostMapping("/edit/comments")
    //改自己的评论
    public Map<String,Object> editEvaluation(@RequestParam String contents,@RequestParam int userId,@RequestParam int id) {
        return purchaseService.editEvaluation(contents,userId,id);
    }
    @DeleteMapping("/delete/my/comments")
    //删自己的评论
    public Map<String,Object> deleteMyEvaluation(@RequestParam int userId, @RequestParam int id) {
        return purchaseService.deleteMyEvaluation(userId,id);
    }
    @GetMapping("/select/my/comments")
    //查看自己的评论
    public List<Map<String,Object>> selectEvaluation(@RequestParam int id,@RequestParam int userId) {
        if (purchaseService.selectEvaluation(id,userId).isEmpty())
            log.info("评论不存在！");
        return purchaseService.selectEvaluation(id,userId);
    }
    @GetMapping("/select/id/comments")
    //查看商品id为id的所有评论
    public List<Map<String,Object>> selectAllEvaluations(@RequestParam int id){
        if(purchaseService.selectAllEvaluations(id).isEmpty())
            log.info("评论不存在！");
        return purchaseService.selectAllEvaluations(id);
    }
}
