package org.designer.bilibili_1st;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication


//项目开始时间2026年9月初
public class Bilibili1stApplication {
    /**
     模板：
     com.example.myproject          # 根包（通常使用反向域名）
     ├── Application.java           # 主启动类，放在根包下
     ├── controller/                # 表现层，处理 HTTP 请求 [citation:3][citation:7]
     │   └── UserController.java
     ├── service/                   # 业务逻辑层 [citation:3][citation:7]
     │   ├── UserService.java       # 业务接口
     │   └── impl/
     │       └── UserServiceImpl.java # 业务实现
     ├── mapper/或dao/              # 数据访问层 [citation:3][citation:7]
     │   └── UserMapper.java
     └── entity/或domain/           # 实体类，通常与数据库表对应 [citation:3]
         └── User.java
     * **/
    static Logger log= LoggerFactory.getLogger(Bilibili1stApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(Bilibili1stApplication.class, args);
        log.info("测试代码");
    }

}


//我的一点小巧思
/**
 * 滂霈驾马
 * 作者：贾奕嘉
 * 时间：2026年9月13日
 * 滂霈骇人世人落，天边未觉民疾苦。蛊雷不倦霎吼声，仰首天晴低头煮。瀑布倾盆渔踊跃，未到耄耋身先腐。驾人享乐言马困，马乏粮草力全无。
 * 灾引自身本相报，嫁祸布衣语境逆。应是亏福差相反，应是削人寻伪辞。一纸名状谓佳策，未尝按与行为骑。骐骥不识千里马，何以烈马三人驱？
 * 名商谓其东道主，啃马蚕骨以换利。马棚列草刀割腹，漏雨盆倾缝中隙。商人重利尽蚕马，终前不忘可戮力。头晕眼花乏水食，谓其不力应刑辞。
 * 寻个牝马图孳生，财骒诈婚卷资行。一朝遁去如升天，人间蒸发杳无声。究之吸血何处有？远嫁易得配外种（找一个好点的母马结婚吧，想要生个
 * 小马来组建家庭寻求幸福，而这个母马贪财，在与这头公马结婚后，骗走了公马所有的财产，逃之夭夭，就像跑到了天上，人间蒸发。公马探索她
 * 到底去哪里诈骗了，原来，母马已经远嫁到了国外，轻易地和外国马结婚，并早已经配下了外国的种）。付诸东流涛涛水，劣迹斑斑走崖横。不忘
 * 商人血与恨，安记财马爱与情？群群坐骑笑此庸，再觅牝种以后平。若不婚种似饥寒，犹如骡马裸当街。咒其断香佛不保，辱马无能首该切。
 * 摇曳空中片海花，踏馅白堤冷刺骨。北面海啸呼不停，近闻远闻都谓虎。苍穹渐青日已落，商人寒店破窗铺。归宿冷落村异待，孤残牡马踏寒芦。
 * **/
