package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.common.ResultMapper;
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
    public Logger log = LoggerFactory.getLogger(PurchaseEntity.class);
    private final PurchaseService purchaseService;

    @GetMapping("/select/all/product")
    public Result<List<Map<String, Object>>> selectAllProduct() {
        List<Map<String, Object>> list = purchaseService.selectAllProduct();
        if (list.isEmpty()) log.info("商品为空");
        return Result.success(list);
    }

    @GetMapping("/select/all/product/id")
    public Result<List<Map<String, Object>>> selectProductById(@RequestParam int id) {
        List<Map<String, Object>> list = purchaseService.selectProductById(id);
        if (list.isEmpty()) log.info("商品不存在");
        return Result.success(list);
    }

    @GetMapping("/select/user")
    public Result<List<Map<String, Object>>> selectUserInformation() {
        List<Map<String, Object>> list = purchaseService.selectUserInformation();
        if (list.isEmpty()) log.info("用户不存在");
        return Result.success(list);
    }

    @PostMapping("/add/product")
    public Result<Map<String, Object>> addProduct(@RequestParam String productName, @RequestParam BigDecimal price, @RequestParam Integer quantity, @RequestParam BigDecimal total) {
        return ResultMapper.from(purchaseService.addProduct(productName, price, quantity, total));
    }

    @PostMapping("/edit/price")
    public Result<Map<String, Object>> editPrice(@RequestParam int id, @RequestParam String name, @RequestParam BigDecimal newPrice) {
        return ResultMapper.from(purchaseService.editPrice(id, name, newPrice));
    }

    @PostMapping("/edit/product/name")
    public Result<Map<String, Object>> editProductName(@RequestParam int id, @RequestParam String newName) {
        return ResultMapper.from(purchaseService.editProductName(id, newName));
    }

    @DeleteMapping("/delete/product")
    public Result<Map<String, Object>> deleteProduct(@RequestParam int id, @RequestParam String name) {
        return ResultMapper.from(purchaseService.deleteProduct(id, name));
    }

    @PostMapping("/add/spread")
    public Result<Map<String, Object>> addSpread(int id, String productName, String text) {
        return ResultMapper.from(purchaseService.addSpread(id, productName, text));
    }

    @DeleteMapping("/delete/spread")
    public Result<Map<String, Object>> deleteSpread(int id, String productName, String text) {
        return ResultMapper.from(purchaseService.deleteSpread(id, productName, text));
    }

    public Result<Map<String, Object>> editSpread(int id, String productName, String text) {
        return ResultMapper.from(purchaseService.editSpread(id, productName, text));
    }

    @GetMapping("/select/price")
    public Result<List<Map<String, Object>>> selectPrice(@RequestParam int id, @RequestParam String name) {
        List<Map<String, Object>> list = purchaseService.selectPrice(id, name);
        if (list.isEmpty()) log.info("商品不存在！");
        return Result.success(list);
    }

    @GetMapping("/select/m/price")
    public Result<List<Map<String, Object>>> selectProductByPrice(@RequestParam BigDecimal maxPrice, @RequestParam BigDecimal minPrice) {
        List<Map<String, Object>> list = purchaseService.selectProductByPrice(maxPrice, minPrice);
        if (list.isEmpty()) log.info("商品不存在");
        return Result.success(list);
    }

    @GetMapping("/select/order")
    public Result<List<Map<String, Object>>> selectOrder() {
        List<Map<String, Object>> list = purchaseService.selectOrder();
        if (list.isEmpty()) log.info("商品不存在");
        return Result.success(list);
    }

    @PostMapping("/write/evaluation")
    public Result<Map<String, Object>> writeEvaluation(@RequestParam String contents, @RequestParam int userId, @RequestParam int id) {
        return ResultMapper.from(purchaseService.writeEvaluation(contents, userId, id));
    }

    @PostMapping("/edit/comments")
    public Result<Map<String, Object>> editEvaluation(@RequestParam String contents, @RequestParam int userId, @RequestParam int id) {
        return ResultMapper.from(purchaseService.editEvaluation(contents, userId, id));
    }

    @DeleteMapping("/delete/my/comments")
    public Result<Map<String, Object>> deleteMyEvaluation(@RequestParam int userId, @RequestParam int id) {
        return ResultMapper.from(purchaseService.deleteMyEvaluation(userId, id));
    }

    @GetMapping("/select/my/comments")
    public Result<List<Map<String, Object>>> selectEvaluation(@RequestParam int id, @RequestParam int userId) {
        List<Map<String, Object>> list = purchaseService.selectEvaluation(id, userId);
        if (list.isEmpty()) log.info("评论不存在！");
        return Result.success(list);
    }

    @GetMapping("/select/id/comments")
    public Result<List<Map<String, Object>>> selectAllEvaluations(@RequestParam int id) {
        List<Map<String, Object>> list = purchaseService.selectAllEvaluations(id);
        if (list.isEmpty()) log.info("评论不存在！");
        return Result.success(list);
    }
}

