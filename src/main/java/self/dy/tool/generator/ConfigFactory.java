package self.dy.tool.generator;

import org.mybatis.generator.config.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigFactory {
    // 自定义配置
    public static Properties properties = new Properties();

    public void loadProperties(){
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("/conf/dy.properties");
        try {
            properties.load(systemResourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库链接
     * @param DBType
     * @param IP
     * @param port
     * @param serverName
     * @param password
     * @throws Exception
     */
    public static JDBCConnectionConfiguration createJdbcConfiguration(String DBType, String IP, String port, String serverName, String userId, String password) throws Exception {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        String driverClass;
        String url;
        if("ORACLE".compareToIgnoreCase(DBType)==0){
            driverClass = "oracle.jdbc.driver.OracleDriver";
            StringBuilder oracleUrl = new StringBuilder("jdbc:oracle:thin:@");
            oracleUrl.append(IP).append(":").append(port).append(":").append(serverName);
            url = oracleUrl.toString();
        }else if("MYSQL".compareToIgnoreCase(DBType)==0){
            driverClass = "com.mysql.jdbc.Driver";
            StringBuilder mySqlUrl = new StringBuilder("jdbc:mysql://");
            mySqlUrl.append(IP).append(":").append(port).append("/").append(serverName).append("?allowMultiQueries=true");
            url = mySqlUrl.toString();
        }else{
            throw new Exception("不支持的数据库类型");
        }
        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(url);
        jdbcConnectionConfiguration.setUserId(userId);
        jdbcConnectionConfiguration.setPassword(password);
        return jdbcConnectionConfiguration;
    }

    /**
     * 生成XML文件配置模型 <sqlMapGenerator>
     * @param targetProject XML文件存放根目录
     * @param targetPackage XML文件包路径
     * @return
     */
    public static SqlMapGeneratorConfiguration createSqlMapGenerationConfiguration(String targetProject, String targetPackage) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);
        Properties properties = sqlMapGeneratorConfiguration.getProperties();
        properties.setProperty("enableSubPackages","false");
        return sqlMapGeneratorConfiguration;
    }

    /**
     * 生成DBModel定义模型 <javaModelGenerator>
     * @param targetProject
     * @param targetPackage
     * @param rootClass
     * @return
     */
    public static JavaModelGeneratorConfiguration createJavaModelGeneratorConfiguration(String targetProject, String targetPackage, String rootClass) {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);
        Properties properties = javaModelGeneratorConfiguration.getProperties();
        properties.setProperty("trimStrings","true");
        if(null!=rootClass){
            properties.setProperty("rootClass",rootClass);
        }
        properties.setProperty("enableSubPackages","false");
        return javaModelGeneratorConfiguration;
    }

    /**
     * 生成Dao类定 <javaClientGenerator>
     * @param targetProject
     * @param targetPackage
     * @param rootClass
     * @return
     */
    public static JavaClientGeneratorConfiguration createJavaClientGeneratorConfiguration(String targetProject, String targetPackage, String rootClass) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        Properties properties = javaClientGeneratorConfiguration.getProperties();
        if(null!=rootClass){
            properties.setProperty("rootClass",rootClass);
        }
        properties.setProperty("enableSubPackages","false");
        return javaClientGeneratorConfiguration;
    }

    /**
     * 生成注释配置模型<commentGenerator>
     * @return
     */
    public static CommentGeneratorConfiguration createCommentGeneratorConfiguration(String suppressAllComments) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        Properties properties = commentGeneratorConfiguration.getProperties();
        properties.setProperty("suppressAllComments",suppressAllComments);
        return commentGeneratorConfiguration;
    }

    /**
     * 生成java类型解析配置模型<javaTypeResolver>
     * @return
     */
    public static JavaTypeResolverConfiguration createJavaTypeResolverConfiguration() {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        Properties properties = javaTypeResolverConfiguration.getProperties();
        properties.setProperty("forceBigDecimals","true");
        return javaTypeResolverConfiguration;
    }

    /**
     * 数据库表配置模型<table>
     * @return
     */
    public static void createTableConfiguration (Context context, List<String> tableNames) {
        ArrayList<TableConfiguration> tableConfigurationsTemp = new ArrayList<>();
        List<TableConfiguration> tableConfigurationsSource = context.getTableConfigurations();
        tableConfigurationsSource.clear();
        for(String tableName:tableNames){
            if(tableName.compareToIgnoreCase("")!=0){
                TableConfiguration tableConfiguration = new TableConfiguration(context);
                tableConfiguration.setTableName(tableName);
                boolean createExample = false;
                tableConfiguration.setSelectByExampleStatementEnabled(createExample);
                tableConfiguration.setDeleteByExampleStatementEnabled(createExample);
                tableConfiguration.setCountByExampleStatementEnabled(createExample);
                tableConfiguration.setUpdateByExampleStatementEnabled(createExample);
                tableConfiguration.setSelectByExampleQueryId("false");
                tableConfigurationsTemp.add(tableConfiguration);
            }
        }
        tableConfigurationsSource.addAll(tableConfigurationsTemp);
    }

}
