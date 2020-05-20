package com.authine.web.cola.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper{

  public CustomerDO getById(String customerId);
}
