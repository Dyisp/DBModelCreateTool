package self.dy.tool.threads;

import org.mybatis.generator.config.JDBCConnectionConfiguration;
import self.dy.tool.generator.ConfigFactory;
import self.dy.tool.generator.ExecuteCreateModel;
import self.dy.tool.utils.ComponentUtils;
import self.dy.tool.window.CompsName;
import self.dy.tool.window.DyExecption;
import self.dy.tool.window.DyJComboBox;
import self.dy.tool.window.WindowStart;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;

public class CacheTableListThread extends Thread{
    JPanel tablePanel;
    private Map<String, Component> paramCompMap;
    private Map<String, Component> tableCompMap;
    private Set<String> tableNames = new HashSet();

    public CacheTableListThread(JPanel contentPanel) {
        this.tablePanel = contentPanel;
        Component[] components = contentPanel.getComponents();
        Map<String, Component> paramCompMap = new HashMap<>();
        ComponentUtils.listComponent(paramCompMap, components[0]);
        Map<String, Component> tableCompMap = new HashMap<>();
        ComponentUtils.listComponent(tableCompMap,components[1],DyJComboBox.class);
        this.paramCompMap=paramCompMap;
        this.tableCompMap=tableCompMap;
    }

    @Override
    public void run() {
        Map<String,String> paramValueMap = ComponentUtils.getCompsValue(paramCompMap,false,false);
        String dataBaseType = paramValueMap.get(CompsName.DataBaseType);
        String dataBaseIP = paramValueMap.get(CompsName.DataBaseIP);
        String dataBasePort = paramValueMap.get(CompsName.DataBasePort);
        String serviceName = paramValueMap.get(CompsName.ServiceName);
        String userID = paramValueMap.get(CompsName.UserID);
        String password = paramValueMap.get(CompsName.Password);

        try {
            // 测试数据库连接是否可用
            JDBCConnectionConfiguration jdbcConfiguration = ConfigFactory.createJdbcConfiguration(dataBaseType,dataBaseIP,dataBasePort,serviceName,userID,password);
            // 注册驱动
            Class.forName(jdbcConfiguration.getDriverClass());
            // 获取连接
            Connection connection = DriverManager.getConnection(jdbcConfiguration.getConnectionURL(), jdbcConfiguration.getUserId(), jdbcConfiguration.getPassword());
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, userID, null, new String[]{"TABLE"});
            this.tableNames.clear();
            while(tables.next()){
                String tableName = tables.getString(3);
                this.tableNames.add(tableName);
            }
        }catch (Exception e) {
            e.printStackTrace();
            new DialogThread(WindowStart.massage,null,"数据库连接失败").start();
            return;
        }

        // 给表名列表赋值
        Collection<Component> values = tableCompMap.values();
        Iterator<Component> iterator= values.iterator();
        while(iterator.hasNext()){
            Component comp = iterator.next();
            if(comp instanceof DyJComboBox){
                DyJComboBox tableNameBox = (DyJComboBox) comp;
                tableNameBox.removeAllItems();
                tableNameBox.addItem("");
                for(String tableName : tableNames){
                    tableNameBox.addItem(tableName);
                }
            }
        }
    }
}
