************************************************
修改dao层文件元素及内容：JavaMapperGenerator.java
pojo层文件元素及内容：BaseRecordGenerator.java
注释模板：DefaultCommentGenerator.java
pojo属性元素：AbstractJavaGenerator.java
pojo属性元素注释获取：DatabaseIntrospector.java（629）
设置生成文件名称：IntrospectedTable.java
************************************************


目前存在问题：
    1、日志打印不详细
    2、未对Mysql数据库进行验证

版本1.0功能：
    1、基本mybatis逆向工程添加对lombok插件的支持；
    2、添加创建者注释信息；
    3、添加自定义父类的导入信息;
    4、舍弃原工程xml配置文件的使用;
    5、参数配置界面化;
    6、改造为maven项目，并修改打包逻辑。
版本2.0功能：
    1、加入数据库连接有效性测试，测试过程中拉取数据库表名列表放入表明下拉框备选；
    2、本地缓存之前输入过的参数，目前固定缓存十个，最新的输入在最上一个显示；

************************************************
如果有兴趣的同学，可以一起交流改进
mail:673253952@qq.com
