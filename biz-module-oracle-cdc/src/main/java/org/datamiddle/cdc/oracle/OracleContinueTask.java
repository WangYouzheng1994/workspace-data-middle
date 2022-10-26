package org.datamiddle.cdc.oracle;

import cn.hutool.setting.dialect.Props;
import org.apache.commons.collections.CollectionUtils;
import org.datamiddle.cdc.oracle.bean.MemcacheConfEntity;
import org.datamiddle.cdc.util.DbUtil;
import org.datamiddle.cdc.util.GetterUtil;
import org.datamiddle.cdc.util.MysqlTools;
import org.datamiddle.cdc.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/***
 *     断点重启 运行demo
 */
public class OracleContinueTask {
    public static void main(String[] args) {
        MemcacheConfEntity memcacheConfEntity = null;
        try {
            memcacheConfEntity = MysqlTools.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> sourceTableList = null;
        try {
            //sourceTableList = new ArrayList<>();
            sourceTableList = getSourceTableList();
            //sourceTableList.add("TDS_LJ.MDAC11");
        }catch (Exception e){
            e.printStackTrace();
        }
        Props props = PropertiesUtil.getProps();
        String host = props.getStr("cdc.oracle.hostname");
        String port = props.getStr("cdc.oracle.port");
        String tableArray = props.getStr("cdc.oracle.table.list");
        String user = props.getStr("cdc.oracle.username");
        String pwd = props.getStr("cdc.oracle.password");
        String oracleServer = props.getStr("cdc.oracle.database");
        //111018319    94407775    83743364   97454694
        if (null != memcacheConfEntity) {
            String scnStr = memcacheConfEntity.getScn();
            String[] scnArr = scnStr.split(" ");
            String scn = scnArr[0];
            String rs_id = scnArr[1];
            OracleCDCConfig build = OracleCDCConfig.builder()
                    .startSCN(scn)
                    .endSCN(scn)
                    .identification(4)
                    .rs_id(" " + rs_id + " ") //空格不能删
                    .readPosition("ALL")
                    .driverClass("oracle.jdbc.OracleDriver")
                   // .table(Arrays.asList(tableArray))
                    .table(sourceTableList)
                    .username(user)
                    .password(pwd).jdbcUrl("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=OFF)(ADDRESS=(PROTOCOL=tcp)(HOST=" +
                            host + ")(PORT=" +
                            port + ")))(CONNECT_DATA=(SID=" +
                            oracleServer + ")))").build();
            new OracleSource().reStart(build);
        }
    }
    /**
     * 获取当前上下文环境下 数仓中的分流到clickhouse的表名，用以MySqlCDC抽取。
     * @return
     * @throws Exception
     */
    public static List<String> getSourceTableList() throws Exception {
        Props props = PropertiesUtil.getProps();
        List<Map<String, Object>> sourceTableList = DbUtil.executeQuery("select distinct(source_table) as source_table from table_process where  level_name='ods'" );
        List<String> sourceTable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceTableList)) {
            // sourceTableList
            for (Map<String, Object> sourceTableKV : sourceTableList) {
                String sourceTableName = GetterUtil.getString(sourceTableKV.get("source_table"));
                sourceTable.add(props.getStr("cdc.oracle.schema.list") + "." + sourceTableName.toUpperCase());
            }
        }
        return sourceTable;
    }
}
