package com.springbootpractice.demo.data.dict.param.bo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carter
 * create_date  2020/5/9 16:55
 * description     TODO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("字段的类型和长度")
public class ColumnTypeBo implements Serializable {

    private static final long serialVersionUID = 5761868821705193679L;

    @ApiModelProperty("字段类型名称")
    private String columnTypeName;

    @ApiModelProperty("字段类型的长度，null表示无长度")
    private Integer columnTypeLength;

    public static ColumnTypeBo getColumnTypeBo(@NonNull String columnNameString) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(columnNameString), "字段字符串为空");

        String trimString = columnNameString.trim();
        int indexOfStart = trimString.indexOf("(");

        if (indexOfStart < 0) {
            return ColumnTypeBo.builder().columnTypeName(trimString).build();
        }

        String columnTypeString = trimString.substring(0, indexOfStart);
        String columnLengthString = trimString.substring(indexOfStart+1, trimString.indexOf(")"));

        return ColumnTypeBo.builder().columnTypeName(columnTypeString)
                .columnTypeLength(Integer.parseInt(columnLengthString))
                .build();

    }

    public static void main(String[] args) {


        ColumnTypeBo columnTypeBo = ColumnTypeBo.getColumnTypeBo("bit(1)");
        System.out.println(columnTypeBo);


        ColumnTypeBo columnTypeBo2 = ColumnTypeBo.getColumnTypeBo("datetime");
        System.out.println(columnTypeBo2);

    }

    private static Map<String,String> mysqlTypeMapOracleType = new HashMap<>();
    static {
        mysqlTypeMapOracleType.put("varchar", "varchar2");

        /**
         * TINYINT(-128-127)
         *
         * SMALLINT(-32768-32767)
         *
         * MEDIUMINT(-8388608-8388607)
         *
         * INT(-2147483648-2147483647)
         * BIGINT(-9223372036854775808-9223372036854775807)
         */
        mysqlTypeMapOracleType.put("tinyint", "number");
        mysqlTypeMapOracleType.put("SMALLINT", "number");
        mysqlTypeMapOracleType.put("MEDIUMINT", "number");
        mysqlTypeMapOracleType.put("INT", "number");
        mysqlTypeMapOracleType.put("BIGINT", "number");
        mysqlTypeMapOracleType.put("DECIMAL", "number");
        mysqlTypeMapOracleType.put("float", "number");
        mysqlTypeMapOracleType.put("double", "number");
        mysqlTypeMapOracleType.put("bit", "number");

        mysqlTypeMapOracleType.put("date", "date");
        mysqlTypeMapOracleType.put("datetime", "date");
        mysqlTypeMapOracleType.put("timestamp", "date");
        mysqlTypeMapOracleType.put("time", "date");

        mysqlTypeMapOracleType.put("char", "char");

        mysqlTypeMapOracleType.put("tinytext", "clob");
        mysqlTypeMapOracleType.put("text", "clob");
        mysqlTypeMapOracleType.put("mediumtext", "clob");
        mysqlTypeMapOracleType.put("longtext", "clob");

        mysqlTypeMapOracleType.put("tinyblob", "blob");
        mysqlTypeMapOracleType.put("blob", "blob");
        mysqlTypeMapOracleType.put("mediumblob", "blob");
        mysqlTypeMapOracleType.put("longblob", "blob");




    }

    public static String getOracleType(@NonNull ColumnTypeBo columnTypeBo) {

        String columnTypeName = columnTypeBo.getColumnTypeName();
        boolean containsKey = mysqlTypeMapOracleType.containsKey(columnTypeName);
        Preconditions.checkArgument(containsKey, "没有对mysql的类型【%s】配置对应oracleType",columnTypeName);

        String oracleTypeName  = mysqlTypeMapOracleType.get(columnTypeName);
        Integer columnTypeLength = columnTypeBo.getColumnTypeLength();

        if (Objects.isNull(columnTypeLength)){
            return oracleTypeName;
        }

        return new StringBuilder(oracleTypeName).append("(").append(columnTypeLength).append(")").toString();
    }
}
