package org.designer.bilibili_1st.entity;

import lombok.Data;

@Data
//发帖
public class DynamicEntity {
    private Long id;
    //发帖人用户ID
    private Long userId;
    //标题
    private String title;
    //内容
    private String contents;
}
