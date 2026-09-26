package org.designer.bilibili_1st.mapper;

import org.designer.bilibili_1st.entity.LogInEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogInMapper extends JpaRepository<LogInEntity, Long> {

    Optional<LogInEntity> findByQqEmail(String qqEmail);
    boolean existsByQqEmail(String qqEmail);
    boolean existsByPhoneNumber(String phoneNumber);

    //软删除：只查未注销的用户
    Optional<LogInEntity> findByQqEmailAndIsDeleted(String qqEmail, Integer isDeleted);
    boolean existsByQqEmailAndIsDeleted(String qqEmail, Integer isDeleted);
    boolean existsByPhoneNumberAndIsDeleted(String phoneNumber, Integer isDeleted);
}