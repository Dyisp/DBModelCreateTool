package self.dy.tool.window;


import self.dy.tool.cache.CacheService;
import self.dy.tool.cache.WindowLisenerForCache;
import self.dy.tool.threads.CacheTableListThread;
import self.dy.tool.threads.CreateThread;
import self.dy.tool.threads.DialogThread;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class WindowStart {

    public static JFrame jFrame = new JFrame();// 窗口
    public static JPanel menu = new JPanel(); // 菜单面板
    public static CardLayout contentLayout = new CardLayout(); // 容器面板布局
    public static JPanel contentPanel = new JPanel(contentLayout);// 容器面板 包含表参数和全局参数
        public static JPanel paramPanel = new JPanel();// 全局参数设置面板
    public static JPanel tablesPanel = new JPanel();// 表参数设置面板
    public static DialogThread massage;// 表参数设置面板

    private static DyActionListener actionListener = new DyActionListener(); // 自定义监听
    private static final Dimension dimensionParam = new Dimension(150,23); // 参数文本组件大小
    private static final Dimension dimensionTable = new Dimension(125,23); // 表文本组件大小

    public static void main(String[] args) throws Exception {
        String logPath;
        if(args.length==0||args[0]==""){
            logPath = ".\\CreateModel.log";
        }else{
            logPath = args[0];
        }
        // 输出重定向
        File file = new File(logPath);
        PrintStream printStream = new PrintStream(new FileOutputStream(file));
        System.setOut(printStream);
        System.setErr(printStream);
        // 创建窗口
        createWindow();
    }

    private static void createWindow() {
        jFrame.setLayout(new BorderLayout());
        jFrame.setTitle("Dcits DB Model Create Util");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口固定大小
        jFrame.setResizable(false);
        jFrame.setSize(600,600);
        jFrame.setBackground(Color.WHITE);
        // 设置窗口居中
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane().add(menu,BorderLayout.NORTH);
        jFrame.getContentPane().add(contentPanel,BorderLayout.CENTER);
        jFrame.addWindowListener(new WindowLisenerForCache(paramPanel));
        contentPanel.add("paramPanel", paramPanel);
        contentPanel.add("tablesPanel", tablesPanel);
        // 生成菜单
        menuCreate(menu);
        // 生成公共参数配置页
        paramCreate(paramPanel);
        // 生成表参数配置页
        tableCreate(tablesPanel);

        jFrame.setVisible(true);
    }

    private static void tableCreate(JPanel tablesPanel) {
        tablesPanel.setBackground(Color.WHITE);
        int rowNum = 15;
        int colnum = 4;
        tablesPanel.setLayout(new GridLayout(rowNum,colnum));
        for(int i = 1;i<=rowNum*colnum/2;i++) {
            StringBuilder tableName = new StringBuilder("Table-");
            tableName.append(i);
            DyJComboBox dyJComboBox = new DyJComboBox(tableName.toString(),dimensionTable);
            createParamGroup(tablesPanel,dimensionTable,tableName.toString(),null,dyJComboBox,false);
        }
    }

    private static void paramCreate(JPanel paramPanel) {
        paramPanel.setBackground(Color.WHITE);
        paramPanel.setLayout(new GridLayout(18,3));
        JComboBox<String> stringJComboBox = new JComboBox<>(new String[]{"Oracle", "Mysql"});
        createParamGroup(paramPanel, dimensionParam,CompsName.DataBaseType,"请输入数据库类型",stringJComboBox,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DataBaseIP,"请输入数据库IP",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DataBasePort,"请输入数据库端口",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.ServiceName,"请输入数据库服务名",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.UserID,"请输入用户ID  ",null,true);
        JPasswordField jPasswordField = new JPasswordField();
        jPasswordField.setPreferredSize(dimensionParam);
        createParamGroup(paramPanel, dimensionParam,CompsName.Password,"请输入用户密码",jPasswordField,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.TargetProject,"请输入生成为文件存储路径",null,true);
        JCheckBox jCheckBox = new JCheckBox("生成注释");
        jCheckBox.setSelected(true);
        createParamGroup(paramPanel, dimensionParam,CompsName.SuppressAllComments,"",jCheckBox,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.CreatorName,"请输入创建者名称",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DBModelPackagePath,"请输入DBModel包路径",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DBModelRootClassFullPath,"请输入DBModel继承类",null,true);
        JCheckBox lombok = new JCheckBox("使用Lombok注解");
        lombok.setSelected(true);
        createParamGroup(paramPanel, dimensionParam,CompsName.MakeGetSet,"",lombok,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.SqlMapPath,"请输入XML文件路径",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.SqlXmlAppend,"请输入XML文件补充字段",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DBDaoPackagePath,"请输入Dao类包路径",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DBDaoRootClassFullPath,"请输入Dao类继承类",null,true);
        createParamGroup(paramPanel, dimensionParam,CompsName.DaoNameAppend,"请输入Dao类名补充字段",null,true);
        createParamGroup(paramPanel,CompsName.DaoClassType,new String[]{"Class","Interface"});
    }

    /**
     * 生成一组参数配置组件(单组件)
     * @param paramPanel
     * @param labelText
     * @param fieldText
     */
    private static void createParamGroup(JPanel paramPanel,Dimension dimension, String labelText, String fieldText,Component component,boolean addCache) {
        String space = "  :";
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        right.add(new JLabel(labelText+space));
        paramPanel.add(right);
        if(addCache){
            JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
            if(null != component){
                component.setName(labelText);
                center.add(component);
            }else{
                DyJTextField dyJTextField = new DyJTextField(fieldText, labelText, dimension);
                center.add(dyJTextField);
                left.add(new DyJComboBox(dyJTextField,labelText+CompsName.CacheBox,dimension));
            }
            paramPanel.add(center);
            paramPanel.add(left);
        }else{
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
            if(null != component){
                component.setName(labelText);
                left.add(component);
            }else{
                left.add(new DyJTextField(fieldText,labelText,dimension));
            }
            paramPanel.add(left);
        }
    }

    /**
     * 生成一组参数配置组件(单选按钮)
     * @param paramPanel
     * @param labelText
     * @param fieldTexts
     */
    private static void createParamGroup(JPanel paramPanel, String labelText, String ... fieldTexts) {
        String space = "  :";
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        right.add(new JLabel(labelText+space));
        paramPanel.add(right);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        List<String> fieldList = Arrays.asList(fieldTexts);
        ButtonGroup buttonGroup = new ButtonGroup();
        for(String field : fieldList){
            JRadioButton jRadioButton = new JRadioButton(field);
            jRadioButton.setName(field);
            if(field.compareToIgnoreCase("class")==0){
                jRadioButton.setSelected(true);
            }
            buttonGroup.add(jRadioButton);
            left.add(jRadioButton);
        }
        paramPanel.add(left);
    }

    private static void menuCreate(JPanel menu) {
        menu.setBackground(Color.white);
        menu.setLayout(new FlowLayout(FlowLayout.CENTER,20,5));
        Button paramButton = new Button(CompsName.PARAM_CONFIG);
        paramButton.addActionListener(actionListener);
        Button tableButton = new Button(CompsName.TABLE_CONFIG);
        tableButton.addActionListener(actionListener);
        Button dbTable = new Button(CompsName.CONNECT_DATABASE);
        dbTable.addActionListener(actionListener);
        Button createModel = new Button(CompsName.CREATE_MODEL);
        createModel.addActionListener(actionListener);
        Button resetButton = new Button(CompsName.RESET);
        resetButton.addActionListener(actionListener);
        menu.add(paramButton);
        menu.add(tableButton);
        menu.add(dbTable);
        menu.add(createModel);
        menu.add(resetButton);
    }

    /**
     * 自定义点击事件，处理菜单四个按钮的点击事件
     */
    static class DyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand() == CompsName.PARAM_CONFIG){
                contentLayout.first(contentPanel);
            }else if(e.getActionCommand() == CompsName.TABLE_CONFIG){
                contentLayout.last(contentPanel);
            }else if(e.getActionCommand() == CompsName.CONNECT_DATABASE){
                Thread thread = new CacheTableListThread(contentPanel);
                thread.start();
            }else if(e.getActionCommand() == CompsName.CREATE_MODEL) {
                // 启动新线程生成数据模型
                massage = new DialogThread(null, null, "DBModel生成中...");
                massage.start();
                Thread thread = new CreateThread(contentPanel);
                thread.start();
                // 缓存最近输入数据
                CacheService.addCache(CreateThread.paramCompMap);
            }else if(e.getActionCommand() == CompsName.RESET){
                clearText(contentPanel);
            }
        }

        /**
         * 递归处理：清除文本域内容
         * @param component
         */
        private void clearText(Component component) {
            System.out.println("参数重置");
            if(component instanceof JPanel){
                Component[] components = ((JPanel) component).getComponents();
                List<Component> componentList = Arrays.asList(components);
                for(Component componentTemp:componentList){
                    clearText(componentTemp);
                }
            }else if(component instanceof DyJTextField){
                ((DyJTextField) component).setText(((DyJTextField) component).noticeText);
                component.setForeground(Color.GRAY);
            }else if (component instanceof JTextComponent){
                ((JTextComponent) component).setText("");
            }
        }
    }
}