package com.springbootpractice.demo.tx.transactional.dao.mapper;

import com.springbootpractice.demo.tx.transactional.dao.entity.UserLoginEntity;
import com.springbootpractice.demo.tx.transactional.dao.entity.UserLoginEntityExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginEntityMapper {

    /**插入用户信息
     * @param param
     * @return
     */
    Integer insertUserLogin(@Param("param") UserLoginEntity param);

    /**按照id查询用户信息
     * @param id
     * @return
     */
    UserLoginEntity getUserLoginById(@Param("id") Integer id);
}
