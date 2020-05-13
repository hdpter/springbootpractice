package com.springbootpractice.demo.data.dict;

import com.springbootpractice.demo.data.dict.dao.OracleDao;
import com.springbootpractice.demo.data.dict.param.bo.ColumnBo;
import com.springbootpractice.demo.data.dict.param.bo.IndexBo;
import com.springbootpractice.demo.data.dict.param.bo.TableBo;
import com.springbootpractice.demo.data.dict.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class OracleDaoTests {


	@Autowired
	private OracleDao oracleDao;
	private String databaseName = "cloudpivot";
	String tableName = "base_security_client";

	@BeforeEach
	void setUp() {
		oracleDao.rebuildDataSource("jdbc:oracle:thin:@47.107.241.221:1521:orcl", "DEV3","H3password");
	}

	@Test
	void testTableCount() {

		Map<String, TableBo> tableBoMap = oracleDao.getTableBoMap(databaseName);
		Assert.notEmpty(tableBoMap, "数据库一定有表");
		log.info("数据库:{}的表数量是：{}",databaseName,tableBoMap.keySet().size());

	}

	@Test
	void testTableDataCount() {

		Long tableDataCount = oracleDao.getTableDataCount(databaseName, tableName);

		Assert.isTrue(tableDataCount>=0, "表的数据量应该大于等于0");
		log.info("表:{}的数据量是：{}", tableName, tableDataCount);
	}

	@Test
	void testFirstRow() {
		Map<String, Object> firstRow = oracleDao.getRowData(databaseName, tableName,true);
		Assert.notNull(firstRow, "第一行数据");
		log.info("表:{} 的第一行数据是：{}", tableName, JsonUtil.toJson(firstRow));
	}

	@Test
	void testColumn() {
		List<ColumnBo> columnBoList = oracleDao.getTableNameDataDictBoListMap(databaseName,tableName).get(tableName);

		Assert.notEmpty(columnBoList, "列不可能为空");
		log.info("表:{} 的字段信息是：{}",tableName,JsonUtil.toJson(columnBoList));
	}

	@Test
	void testIndexs() {
		Map<String, List<IndexBo>> nameIndexBoListMap = oracleDao.getIndexNameIndexBoListMap(databaseName, tableName);

		Assert.notNull(nameIndexBoListMap,"索引可能没有");
		log.info("表:{} 的索引是：{}",tableName,JsonUtil.toJson(nameIndexBoListMap));
	}

	@AfterEach
	void tearDown() {
		oracleDao.autoCloseDataSource();
	}
}
