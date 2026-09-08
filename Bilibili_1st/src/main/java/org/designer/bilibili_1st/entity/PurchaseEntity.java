package org.designer.bilibili_1st.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="bilibili_purchase")
public class PurchaseEntity {
    @Id
    private Long id1;
    /**
     * CREATE TABLE IF NOT EXISTS bilibili_purchase(
     *     id INT PRIMARY KEY AUTO_INCREMENT,
     *     user_id INT NOT NULL,
     *     product_name VARCHAR(200) NOT NULL,
     *     price DECIMAL(10,2) NOT NULL,
     *     quantity INT DEFAULT 1,
     *     total DECIMAL(10,2) NOT NULL,
     *     create_time DATETIME DEFAULT CURRENT_TIMESTAMP
     * );
     * **/
    private Long id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("product_name")
    private String productName;
    private double price;
    private int quantity;
    @JsonProperty("total")
    private double totalPrice;
}
