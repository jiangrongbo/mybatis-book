Mybatis源码深度解析
================
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2f0a0191b02448e6919aca6ce12a1584)](https://app.codacy.com/app/https://app.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=rongbo-j/mybatis-book&utm_campaign=Badge_Grade_Settings)
[![Total lines](https://tokei.rs/b1/github/rongbo-j/mybatis-book?category=lines)](https://github.com/rongbo-j/mybatis-book)
[![Build Status](https://travis-ci.org/rongbo-j/mybatis-book.svg?branch=master)](https://travis-ci.org/rongbo-j/mybatis-book)
### 书籍封面
![书籍封面](https://github.com/rongbo-j/mybatis-book/raw/master/img/mybatis-book.png)  

### 图书目录  
前言	4  
第1篇 Mybatis3源码	7  
第1章 搭建Mybatis源码环境	7  
1.1 Mybatis3简介	7  
1.2 环境准备	7  
1.3 获取Mybatis源码	8  
1.4 导入Mybatis源码到IDE	10  
1.5 HSQLDB数据库简介	13  
1.6 本章小结	16  
第2章 JDBC规范详解	17  
2.1 JDBC API简介	17  
2.1.1 建立数据源连接	18  
2.1.2 执行SQL语句	19  
2.1.3 处理SQL执行结果	19  
2.1.4 使用JDBC操作数据库	20  
2.2 JDBC API中的类与接口	21  
2.2.1 java.sql包详解	21  
2.2.2 javax.sql包详解	24  
2.3 Connection详解	28  
2.3.1 JDBC驱动类型	29  
2.3.2 java.sql.Driver接口	30  
2.3.3 Java SPI机制简介	31  
2.3.4 java.sql.DriverAction接口	33  
2.3.5 java.sql.DriverManager类	34  
2.3.5 javax.sql.DataSource接口	35  
2.3.6 使用JNDI API增强应用可移植性	36  
2.3.7 关闭Connection对象	39  
2.4 Statement详解	39  
2.4.1 java.sql.Statement接口	40  
2.4.2 java.sql.PreparedStatement接口	44  
2.4.3 java.sql.CallableStatement接口	48  
2.4.4 获取自增长的键值	49  
2.5 ResultSet详解	50  
2.5.1 ResultSet类型	50  
2.5.2 ResultSet并行性	51  
2.5.3 ResultSet可保持性	51  
2.5.4 ResultSet属性设置	52  
2.5.5 ResultSet游标移动	52  
2.5.6 修改ResultSet对象	53  
2.5.7 关闭ResultSet对象	55  
2.6 DatabaseMetaData详解	55  
2.6.1 创建DatabaseMetaData对象	56  
2.6.2 获取数据源基本信息	56  
2.6.3 获取数据源支持特性	57  
2.6.5 获取数据源限制	58  
2.6.7 获取SQL对象及属性	58  
2.6.8 获取事务支持	59  
2.7 JDBC事务	59  
2.7.1 事务边界与自动提交	59  
2.7.2 事务隔离级别	59  
2.7.3 事务中保存点	61  
2.8 本章小结	62  
第3章 Mybatis常用工具类	63  
3.1 使用SQL类生成语句	63  
3.2 使用ScriptRunner执行脚本	69  
3.3 使用SqlRunner操作数据库	72  
3.4 MetaObject详解	76  
3.5 MetaClass详解	77  
3.6 ObjectFactory详解	79  
3.7 ProxyFactory详解	80  
3.8 本章小结	81  
第4章 Mybatis核心组件介绍	82  
4.1 使用Mybatis操作数据库	82  
4.2 Mybatis核心组件	86  
4.3 Configuration详解	88  
4.4 Executor详解	94  
4.5 MappedStatement详解	96  
4.6 StatementHandler详解	99  
4.7 TypeHandler详解	100  
4.9 ParameterHandler详解	104  
4.9 ResultSetHandler详解	105  
4.10 本章小结	106  
第5章 SqlSession创建过程	108  
5.1 XPath方式解析XML文件	108  
5.2 Configuration实例创建过程	111  
5.3 SqlSession实例创建过程	115  
5.4 本章小结	116  
第6章 SqlSession执行Mapper过程	118  
6.1 Mapper接口注册过程	118  
6.2 MappedStatement注册过程	122  
6.3 Mapper方法调用过程详解	128  
6.4 SqlSession执行Mapper过程	134  
6.5 本章小结	139  
第7章 Mybatis缓存	139  
7.1 Mybatis缓存的使用	139  
7.2 Mybatis缓存实现类	140  
7.3 Mybatis一级缓存实现原理	144  
7.4 Mybatis二级缓存实现原理	147  
7.5 Mybatis使用Redis缓存	152  
7.6 本章小结	154  
第8章 Mybatis日志实现	155  
8.1 Java日志体系	155  
8.2 Mybatis日志实现	159  
8.3 本章小结	165  
第9章 动态SQL实现原理	166  
9.1 动态SQL的使用	166  
9.2 SqlSource与BoundSql详解	168  
9.3 LanguageDriver详解	171  
9.4 SqlNode详解	174  
9.5 动态SQL解析过程	179  
9.6 从源码角度分析#{}和${}区别	189  
9.7 本章小结	193  
第10章 Mybatis插件实现原理	194  
10.1 Mybatis插件实现原理	194  
10.2 自定义一个分页插件	203  
10.3 自定义慢SQL统计插件	211  
10.4 本章小结	212  
第11章 Mybatis级联映射与懒加载	214  
11.1 Mybatis级联映射详解	214  
11.1.1 准备工作	214  
11.1.2 一对多关联映射	217  
11.1.3 一对一关联映射	219  
11.1.4 Discriminator详解	221  
11.2 Mybatis懒加载机制	223  
11.3 Mybatis级联映射实现原理	224  
11.3.1 ResultMap详解	224  
11.3.2 ResultMap解析过程	225  
11.3.3 级联映射实现原理	231  
11.4 懒加载实现原理	238  
11.5 本章小结	243  
第2篇 Mybatis Spring源码	245  
第12章 Mybatis与Spring整合案例	245  
12.1 准备工作	245  
12.2 Mybatis与Spring整合	246  
12.3 用户注册案例	248  
12.4 本章小结	251  
第13章 Mybatis Spring实现原理	252  
13.1 Spring中的一些概念	252  
13.2 Spring容器启动过程	255  
13.3 Mapper动态代理对象注册过程	256  
13.4 Mybatis整合Spring事务管理	260  
13.5 本章小结	264

### 内容概要  
本书主要分为两个篇章，第一篇章为Mybatis3源码篇，从第1章至11章，主要介绍Mybatis框架各个特性的实现原理。第二篇章为Mybatis Spring源码篇，主要介绍Mybatis框架与Spring框架整合原理。  
<b><details><summary>📚 第一章 搭建Mybatis源码环境</summary></b>
> 主要介绍如何搭建Mybatis源码调试环境，包括Mybatis框架源码获取途径，如何导入集成开发工具，如何运行Mybatis源码中自带的测试用例。

</details>

<b><details><summary>📚 第二章 JDBC规范详解</summary></b>
> Mybatis框架是对JDBC轻量级的封装，熟练掌握JDBC规范有助于理解Mybatis框架实现原理，本章详细介绍JDBC规范相关细节，已经全面掌握JDBC规范的读者可以跳过该章节。

</details>

<b><details><summary>📚 第三章 Mybatis常用工具类</summary></b>
> 介绍Mybatis框架中常用的工具类，避免读者对这些工具类的使用不熟悉，而导致对框架主流程理解的干扰，这些工具类包括MetaObject、ObjectFactory、ProxyFactory等。

</details>

<b><details><summary>📚 第四章 Mybatis核心组件介绍</summary></b>
> 介绍Mybatis的核心组件，包括Configuration、SqlSession、Executor、MappedStatement等，本章详细介绍了这些组件的作用及Mybatis执行SQL语句的核心流程。

</details>

<b><details><summary>📚 第五章 SqlSession创建过程</summary></b>
> 主要介绍SqlSession组件的创建过程，包括Mybatis框架对XPath方式解析XML封装的工具类，Mybatis主配置文件解析生成Configuration对象的过程。

</details>

<b><details><summary>📚 第六章 SqlSession执行Mapper过程</summary></b>
> 本章介绍Mapper接口注册过程，SQL配置转换为MappedStatement对象并注册到Configuration对象的过程。除此之外，本章还介绍了通过SqlSession执行Mapper的过程。

</details>

<b><details><summary>📚 第七章 Mybatis缓存</summary></b>
> 本章首先介绍了Mybatis一级缓存和二级缓存的使用细节，接着介绍了一级缓存和二级缓存的实现原理，最后介绍了Mybatis如何整合Redis作为二级缓存。

</details>

<b><details><summary>📚 第八章 Mybatis日志实现</summary></b>
> 基于Java语言的日志框架比较多，比较常用的有Logback、Log4j等，本章介绍了Java的日志框架发展史，并介绍了这些日志框架之间的关系。最后，本章介绍了Mybatis自动查找日志框架的实现原理。

</details>

<b><details><summary>📚 第九章 动态SQL实现原理</summary></b>
> 本章主要介绍Mybatis动态SQL的使用，动态SQL配置转换为SqlSource对象的过程，以及动态SQL的解析原理，最后从源码的角度分析动态SQL配置中#{}和${}参数占位符的区别。

</details>

<b><details><summary>📚 第十章 Mybatis插件实现原理</summary></b>
> 本章介绍了Mybatis插件的实现原理，并以实际的案例介绍了如何自定义Mybatis插件。本章中实现了两个Mybatis插件，分别为分页查询插件和慢SQL统计插件。

</details>

<b><details><summary>📚 第十一章 Mybatis级联映射与懒加载</summary></b>
> 本章介绍了Mybatis中的一对一、一对多级联映射和懒加载机制的使用细节，并介绍了级联映射和懒加载的实现原理。

</details>

<b><details><summary>📚 第十二章 Mybatis与Spring整合案例</summary></b>
> 本章中以一个用户注册RESTful接口案例，介绍了Mybatis框架与Spring框架整合的最佳实践。

</details>

<b><details><summary>📚 第十三章 Mybatis Spring实现原理</summary></b>
> 本章介绍了Spring框架中的一些核心概念，并介绍了Spring IoC容器的使用过程。接着介绍了Mybatis和Spring整合后，动态代理产生的Mapper对象是如何与Spring Ioc容器进行关联的，最后介绍了Mybatis整合Spring事务管理的实现原理。

</details>

### 读者交流群  
企鹅群：1055227297
