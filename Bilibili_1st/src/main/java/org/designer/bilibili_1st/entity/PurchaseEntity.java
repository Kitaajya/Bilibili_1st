package org.designer.bilibili_1st.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name="bilibili_purchase")
public class PurchaseEntity {
    @Id
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_name")
    private String productName;
    private BigDecimal price;
    private int quantity;
    @JsonProperty("total")
    private BigDecimal total;
    private String spread;  //宣传标语
}
