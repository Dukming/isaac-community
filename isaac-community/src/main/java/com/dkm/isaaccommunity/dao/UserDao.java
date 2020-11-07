package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    User getUser(Integer id);
    User getUserByPhone(@Param("phone") String phone);
    void increaseUserScore(@Param("userID") Integer userID, @Param("score") Integer score);
    void decreaseUserScore(@Param("userID") Integer userID, @Param("score") Integer score);
    int updateHeadImg(@Param("imgName") String imgName, @Param("userID") Integer userID);
    int insertUser(@Param("user") User user, @Param("newPassword") String newPasswoed);
}
