package ${package}.dal.mapper;

import ${package}.dal.entity.SaasMemberEntity;
import ${package}.dal.example.SaasMemberEntityExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaasMemberEntityMapper {
    long countByExample(SaasMemberEntityExample example);

    int insert(SaasMemberEntity record);

    int insertSelective(SaasMemberEntity record);

    List<SaasMemberEntity> selectByExample(SaasMemberEntityExample example);

    SaasMemberEntity selectByPrimaryKey(Integer id);

    SaasMemberEntity selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    int updateByExampleSelective(@Param("record") SaasMemberEntity record, @Param("example") SaasMemberEntityExample example);

    int updateByExample(@Param("record") SaasMemberEntity record, @Param("example") SaasMemberEntityExample example);

    int updateByPrimaryKeySelective(SaasMemberEntity record);

    int updateByPrimaryKey(SaasMemberEntity record);

    int logicalDeleteByExample(@Param("example") SaasMemberEntityExample example);

    int logicalDeleteByPrimaryKey(Integer id);
}
