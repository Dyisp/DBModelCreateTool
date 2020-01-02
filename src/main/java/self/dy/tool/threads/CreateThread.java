package self.dy.tool.threads;

import org.mybatis.generator.config.JDBCConnectionConfiguration;
import self.dy.tool.generator.ConfigFactory;
import self.dy.tool.generator.ExecuteCreateModel;
import self.dy.tool.utils.ComponentUtils;
import self.dy.tool.window.CompsName;
import self.dy.tool.window.DyExecption;
import self.dy.tool.window.WindowStart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;

public class CreateThread extends Thread{
    JPanel contentPanel;
    public static Map<String, Component> paramCompMap;
    Map<String, Component> tableCompMap;
    public CreateThread(Map<String, Component> paramCompMap,Map<String, Component> tableCompMap){
        this.paramCompMap=paramCompMap;
        this.tableCompMap=tableCompMap;
    }

    public CreateThread(JPanel contentPanel) {
        this.contentPanel = contentPanel;
        Component[] components = contentPanel.getComponents();
        Map<String, Component> paramCompMap = new HashMap<>();
        ComponentUtils.listComponent(paramCompMap, components[0]);
        Map<String, Component> tableCompMap = new HashMap<>();
        ComponentUtils.listComponent(tableCompMap,components[1]);
        this.paramCompMap=paramCompMap;
        this.tableCompMap=tableCompMap;
    }

    @Override
    public void run() {
        Map<String,String> paramValueMap = ComponentUtils.getCompsValue(paramCompMap,false, false);
        String dataBaseType = paramValueMap.get(CompsName.DataBaseType);
        String dataBaseIP = paramValueMap.get(CompsName.DataBaseIP);
        String dataBasePort = paramValueMap.get(CompsName.DataBasePort);
        String serviceName = paramValueMap.get(CompsName.ServiceName);
        String userID = paramValueMap.get(CompsName.UserID);
        String password = paramValueMap.get(CompsName.Password);

        try {
            // 测试数据库连接是否可用
            JDBCConnectionConfiguration jdbcConfiguration = ConfigFactory.createJdbcConfiguration(dataBaseType, dataBaseIP, dataBasePort, serviceName, userID, password);
            // 注册驱动
            Class.forName(jdbcConfiguration.getDriverClass());
            // 获取连接
            Connection connection = DriverManager.getConnection(jdbcConfiguration.getConnectionURL(), jdbcConfiguration.getUserId(), jdbcConfiguration.getPassword());
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, userID, null, new String[]{"TABLE"});
            ExecuteCreateModel.tableNames.clear();
            while (tables.next()) {
                String tableName = tables.getString(3);
                ExecuteCreateModel.tableNames.add(tableName);
            }
        }catch (Exception e) {
            e.printStackTrace();
            new DialogThread(WindowStart.massage,null,"数据库连接失败").start();
            return;
        }
        Map<String,String> tableValueMap = ComponentUtils.getCompsValue(tableCompMap,true,true);
        Collection<String> values = tableValueMap.values();
        if(values.isEmpty()){
            new DialogThread(WindowStart.massage,null,"请至少输入一个表名！").start();
            return;
        }
        List<String> tableNameList = new ArrayList<>(values);
        Iterator<String> iterator = tableNameList.iterator();
        while (iterator.hasNext()){
            String tableName = iterator.next();
            if(!ExecuteCreateModel.tableNames.contains(tableName)){
                new DialogThread(WindowStart.massage,null,"数据库中不存在["+tableName+"]表！").start();
                return;
            }
        }
        System.out.println("Start to Create DBModels!StartTime:"+(new Date().toString()));
        String basePath = paramValueMap.get(CompsName.TargetProject);
        // 获取盘符，判断盘符是否存在,不存在结束并提示
        if(basePath!=null){
            String[] split = basePath.split(":");
            if(split.length>0){
                String diskName = split[0];
                String diskPath = diskName + ":\\";
                File file = new File(diskPath);
                if(file.exists()){
                    File baseDir = new File(basePath);
                    if(!baseDir.exists()){
                        baseDir.mkdirs();
                    }
                }else{
                    new DialogThread(WindowStart.massage,null,"路径配置错误(磁盘不存在)!").start();
                }
            }
        }
        try {
            setProperties(paramValueMap,tableValueMap);
            ExecuteCreateModel.createModel(paramValueMap,tableValueMap);
            ExecuteCreateModel.complite = true;
            new DialogThread(WindowStart.massage,null,"Model生成结束!").start();
        } catch (DyExecption e) {
            e.printStackTrace();
            new DialogThread(WindowStart.massage,null,e.getDesc()).start();
        }catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
            new DialogThread(WindowStart.massage,null,"生成DBModel异常").start();
        }
        System.out.println("Create DBModels End!EndTime:"+(new Date().toString()));
    }

    private void setProperties(Map<String, String> paramValueMap, Map<String, String> tableValueMap) {
        ExecuteCreateModel.properties.setProperty(CompsName.DaoNameAppend,paramValueMap.get(CompsName.DaoNameAppend));
        ExecuteCreateModel.properties.setProperty(CompsName.SqlXmlAppend,paramValueMap.get(CompsName.SqlXmlAppend));
        ExecuteCreateModel.properties.setProperty(CompsName.DaoClassType,paramValueMap.get(CompsName.DaoClassType));
        ExecuteCreateModel.properties.setProperty(CompsName.MakeGetSet,paramValueMap.get(CompsName.MakeGetSet));
        ExecuteCreateModel.properties.setProperty(CompsName.CreatorName,paramValueMap.get(CompsName.CreatorName));
        ExecuteCreateModel.properties.setProperty(CompsName.DBModelRootClassFullPath,paramValueMap.get(CompsName.DBModelRootClassFullPath));
        ExecuteCreateModel.properties.setProperty(CompsName.DBDaoRootClassFullPath,paramValueMap.get(CompsName.DBDaoRootClassFullPath));
    }
}
