package self.dy.tool.generator;


import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import self.dy.tool.window.CompsName;
import self.dy.tool.window.DyExecption;

import java.io.File;
import java.util.*;

public class ExecuteCreateModel {
    public static Properties properties = new Properties();
    public static Set<String> tableNames = new HashSet();
    public static boolean complite = false;
    public static void createModel(Map<String, String> paramValueMap, Map<String, String> tableValueMap) throws Exception {

        //加载自定义配置文件
        /*InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("dy.properties");
        properties.load(systemResourceAsStream);*/
        List<String> warnings = new ArrayList<>();
        //加载mybatis-generator配置文件（本地正确，部署读不到）
        /*File configFileStream = new File(ExecuteCreateModel.class.getResource("/generatorConfig.xml").toURI());*/
        //加载mybatis-generator配置文件（本地正确，部署正确）
        /*InputStream configFileStream = ExecuteCreateModel.class.getResourceAsStream("/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFileStream);*/
        Configuration config = createConfig();
        refreshConfig(config,paramValueMap,tableValueMap);
        DefaultShellCallback shellCallback = new DefaultShellCallback(true); //文件存在是否覆盖
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
        myBatisGenerator.generate(null);
        System.out.println(warnings);
    }

    private static Configuration createConfig() {
        Configuration configuration = new Configuration();
        Context context = new Context(null);
        context.setId("testTables");
        context.setTargetRuntime("MyBatis3");
        configuration.addContext(context);
        return configuration;
    }

    private static void refreshConfig(Configuration config, Map<String, String> paramValueMap, Map<String, String> tableValueMap) throws Exception {
        Context testTables = config.getContext("testTables");
        // 组装JDBC配置模型
        String dataBaseType = paramValueMap.get(CompsName.DataBaseType);
        String dataBaseIP = paramValueMap.get(CompsName.DataBaseIP);
        String dataBasePort = paramValueMap.get(CompsName.DataBasePort);
        String serviceName = paramValueMap.get(CompsName.ServiceName);
        String userID = paramValueMap.get(CompsName.UserID);
        String password = paramValueMap.get(CompsName.Password);
        JDBCConnectionConfiguration jdbcConfiguration = ConfigFactory.createJdbcConfiguration(dataBaseType, dataBaseIP, dataBasePort, serviceName, userID, password);
        testTables.setJdbcConnectionConfiguration(jdbcConfiguration);

        // 组织类型转换模型
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = ConfigFactory.createJavaTypeResolverConfiguration();
        testTables.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        // 组织是否生成注释模型
        String suppressAllComments = paramValueMap.get(CompsName.SuppressAllComments);
        CommentGeneratorConfiguration commentGeneratorConfiguration = ConfigFactory.createCommentGeneratorConfiguration(suppressAllComments);
        testTables.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        String targetPath = paramValueMap.get(CompsName.TargetProject);
        if(targetPath == null || targetPath.length() == 0){
            throw new DyExecption("TargetProject不能为空!");
        }
        File file = new File(targetPath);
        file.mkdirs();
        // JavaModel配置
        String modelPackagePath = paramValueMap.get(CompsName.DBModelPackagePath);
        if(modelPackagePath == null || modelPackagePath.length() == 0){
            throw new DyExecption("DBModelRootClassName不能为空");
        }
        String modelRootClass = paramValueMap.get(CompsName.DBModelRootClassName);
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = ConfigFactory.createJavaModelGeneratorConfiguration(targetPath, modelPackagePath, modelRootClass);
        testTables.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        // xml配置
        String sqlMapPath = paramValueMap.get(CompsName.SqlMapPath);
        if(sqlMapPath == null || sqlMapPath.length() == 0){
            throw new DyExecption("SqlMapPath不能为空");
        }
        SqlMapGeneratorConfiguration sqlMapGenerationConfiguration = ConfigFactory.createSqlMapGenerationConfiguration(targetPath, sqlMapPath);
        testTables.setSqlMapGeneratorConfiguration(sqlMapGenerationConfiguration);
        // Dao配置
        String daoPackagePath = paramValueMap.get(CompsName.DBDaoPackagePath);
        if(daoPackagePath == null || daoPackagePath.length() == 0){
            throw new DyExecption("DBDaoPackagePath不能为空");
        }
        String daoRootClass = paramValueMap.get(CompsName.DBDaoRootClassName);
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = ConfigFactory.createJavaClientGeneratorConfiguration(targetPath, daoPackagePath, daoRootClass);
        testTables.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        // 表配置
        Collection<String> values = tableValueMap.values();
        List<String> tableNames = new ArrayList<>(values);
        ConfigFactory.createTableConfiguration(testTables, tableNames);
    }
}