新朋友业务逻辑
搜索后，添加到newfriend，修改状态。
别人审核通过后，update后，加入到contact。
别人加我，增加一条，修改状态。
我审核通过，update，加入到contact。


通信录。
RUDI
update 


聊天历史
RUDI
发送成功update status


最近聊天。
RUDI


　　　群聊一共新加2张表
　　　
　　　一，群表： GROUP表
　　　字段如下：
　　　GROUP_ID              群id
　　　GROUP_AVATAR         群头像
　　　GROUP_NICK_NAME          群昵称
　　　SELF_ID                用户id（当前用户id）
　　　IS_NICK_SET           是否设置了群名称
　　　IS_GROUP_SAVED      是否保存到了通讯录，默认不保存
　　　IS_NEWS_NOTIFY       是否屏蔽群消息提醒，默认不屏蔽
　　　BY1                    扩展字段1
　　　BY2                    扩展字段2
　　　
　　　
　　　二，群成员表：GROUP_MEMBERS表
　　　字段如下：
　　　GROUP_ID              群id
　　　MEMBER_ID            群成员id
　　　NICK_NAME          群成员自己的名字
　　　MEMBER_AVATAR       群成员头像
　　　MEMBER_NAME         群成员在群里的名字
　　　SELF_ID                用户id（当前用户id）
　　　IS_OWNER              是否是群主
　　　BY1                     扩展字段1
　　　BY2                     扩展字段2





zip文件 Android开源：数据库ORM框架GreenDao学习心得及使用总结

tonytianshu2014-04-29上传
一、greenDAO相关

1.greenDAO官网：http://greendao-orm.com/

2.项目下载地址：https://github.com/greenrobot/greenDAO（或者官网）

greenDAO是一个可以帮助Android开发者快速将Java对象映射到SQLite数据库的表单中的ORM解决方案，通过使用一个简单的面向对象API，开发者可以对Java对象进行存储、更新、删除和查询。

greenDAO的主要设计目标：

*最大性能（最快的Android ORM）

*易于使用API

*高度优化

*最小内存消耗

二、使用步骤

官方Demo里共有六个工程目录，分别为：

(1).DaoCore：库目录，即jar文件greendao-1.3.0-beta-1.jar的代码；

(2).DaoExample：android范例工程；

(3).DaoExampleGenerator：DaoExample工程的DAO类构造器，java工程；

(4).DaoGenerator：DAO类构造器，java工程；

(5).DaoTest、PerformanceTestOrmLite：其他测试相关的工程。



（一）DAO类构造

首先需要新建一个java工程来生成DAO类文件，该工程需要导入greendao-generator.jar和freemarker.jar文件到项目中。



01.
package de.greenrobot.daogenerator.gentest;
02.
import de.greenrobot.daogenerator.DaoGenerator;
03.
import de.greenrobot.daogenerator.Entity;
04.
import de.greenrobot.daogenerator.Property;
05.
import de.greenrobot.daogenerator.Schema;
06.
import de.greenrobot.daogenerator.ToMany;
07.
/**
08.
* Generates entities and DAOs for the example project DaoExample.
09.
*
10.
* Run it as a Java application (not Android).
11.
*
12.
* @author Markus
13.
*/
14.
public class ExampleDaoGenerator
15.
{
16.

17.
public static void main(String[] args) throws Exception
18.
{
19.
Schema schema = new Schema(3, "de.greenrobot.daoexample");
20.

21.
addNote(schema);
22.
addCustomerOrder(schema);
23.

24.
new DaoGenerator().generateAll(schema, "../DaoExample/src-gen");
25.
}
26.

27.
private static void addNote(Schema schema)
28.
{
29.
Entity note = schema.addEntity("Note");
30.
note.addIdProperty();
31.
note.addStringProperty("text").notNull();
32.
note.addStringProperty("comment");
33.
note.addDateProperty("date");
34.
}
35.

36.
private static void addCustomerOrder(Schema schema)
37.
{
38.
Entity customer = schema.addEntity("Customer");
39.
customer.addIdProperty();
40.
customer.addStringProperty("name").notNull();
41.

42.
Entity order = schema.addEntity("Order");
43.
order.setTableName("ORDERS"); // "ORDER" is a reserved key<a href="http://www.it165.net/edu/ebg/" target="_blank" class="keylink">word</a>
44.
order.addIdProperty();
45.
Property orderDate = order.addDateProperty("date").getProperty();
46.
Property customerId = order.addLongProperty("customerId").notNull().getProperty();
47.
order.addToOne(customer, customerId);
48.

49.
ToMany customerToOrders = customer.addToMany(order, customerId);
50.
customerToOrders.setName("orders");
51.
customerToOrders.orderAsc(orderDate);
52.
}
53.

54.
}


在main方法中，



1.
Schema schema = new Schema(3, "de.greenrobot.daoexample");
该方法第一个参数用来更新数据库版本号，第二个参数为要生成的DAO类所在包路径。



然后进行建表和设置要生成DAO文件的目标工程的项目路径。





addNote(schema);
addCustomerOrder(schema);
new DaoGenerator().generateAll(schema, "../DaoExample/src-gen");
其中src-gen这个目录名需要在运行前手动创建，否则会报错。



如果运行后出现以下错误，则导入DaoGenerator项目的dao.ftl文件即可(或者直接使用DaoGenerator来生成DAO文件)。



1.
Exception in thread "main" java.io.FileNotFoundException: Template "dao.ftl" not found.
2.
at freemarker.template.Configuration.getTemplate(Configuration.java:742)
3.
at freemarker.template.Configuration.getTemplate(Configuration.java:665)
4.
at de.greenrobot.daogenerator.DaoGenerator.<init>(DaoGenerator.java:68)
5.
at de.greenrobot.daogenerator.gentest.ExampleDaoGenerator.main(ExampleDaoGenerator.java:41)


运行后出现以下的提示说明DAO文件自动生成成功了，刷新一下DaoExample项目即可看到。



01.
greenDAO Generator
02.
Copyright 2011-2013 Markus Junginger, greenrobot.de. Licensed under GPL V3.
03.
This program comes with ABSOLUTELY NO WARRANTY
04.
Processing schema version 3...
05.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\NoteDao.java
06.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\Note.java
07.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\CustomerDao.java
08.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\Customer.java
09.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\OrderDao.java
10.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\Order.java
11.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\DaoMaster.java
12.
Written F:\Android_Ex\work_10\DaoExample\src-gen\de\greenrobot\daoexample\DaoSession.java
13.
Processed 3 entities in 204ms


运行后可以看到，DaoExample项目src-gen下面自动生成了8个文件，3个实体对象，3个dao，1个DaoMaster,1个DaoSession.



(二)创建表

1.创建一个实体类



Entity note = schema.addEntity("Note");
默认表名就是类名，也可以自定义表名



1.
dao.setTableName("NoteList");
greenDAO会自动根据实体类属性创建表字段，并赋予默认值。例如在数据库方面的表名和列名都来源于实体类名和属性名。默认的数据库名称是大写使用下划线分隔单词，而不是在Java中使用的驼峰式大小写风格。例如，一个名为“CREATIONDATE”属性将成为一个数据库列“CREATION_DATE”。



设置一个自增长ID列为主键：



1.
dao.addIdProperty().primaryKey().autoincrement();


设置其他各种类型的属性：



dao.addIntProperty("cityId");
dao.addStringProperty("infoType").notNull();//非null字段
dao.addDoubleProperty("Id");
在生成的实体类中,int类型为自动转为long类型。

如果在编译过程中出现以下错误，那么有可能是主键的类型错误所致：

1.
java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String


在使用greenDAO时，一个实体类只能对应一个表，目前没法做到一个表对应多个实体类，或者多个表共用一种对象类型。后续的升级也不会针对这一点进行扩展。



(二)表的增删改查

增删改查相当方便，完全的面向对象，不需要涉及到任何的sql语言。

1.查询

范例1：查询某个表是否包含某个id:



public boolean isSaved(int ID)
{
QueryBuilder<SaveList> qb = saveListDao.queryBuilder();
qb.where(Properties.Id.eq(ID));
qb.buildCount().count();
return qb.buildCount().count() > 0 ? true : false;
}


范例2：获取整个表的数据集合,一句代码就搞定！



public List<PhotoGalleryDB> getPhotoGallery()
{
    return photoGalleryDao.loadAll();// 获取图片相册
}


范例3：通过一个字段值查找对应的另一个字段值(为简便直接使用下面方法，也许有更简单的方法，尚未尝试)

/** 通过图片id查找其目录id */
public int getTypeId(int picId)
{
QueryBuilder<PhotoGalleryDB> qb = photoGalleryDao.queryBuilder();
qb.where(Properties.Id.eq(picId));
if (qb.list().size() > 0)
{
return qb.list().get(0).getTypeId();
}
else
{
return -1;
}
}


范例4：查找所有第一姓名是“Joe”并且以lastname排序。



List joes = userDao.queryBuilder()
.where(Properties.FirstName.eq("Joe"))
.orderAsc(Properties.LastName)
.list();


范例5：多重条件查询

(1)获取id为cityId并且infotype为HBContant.CITYINFO_SL的数据集合：

public List<CityInfoDB> getSupportingList(int cityId)
{
QueryBuilder<CityInfoDB> qb = cityInfoDao.queryBuilder();
qb.where(qb.and(Properties.CityId.eq(cityId),Properties.InfoType.eq(HBContant.CITYINFO_SL)));
qb.orderAsc(Properties.Id);// 排序依据
return qb.list();
}


(2)获取firstname为“Joe”并且出生于1970年10月以后的所有user集合:

QueryBuilder qb = userDao.queryBuilder();
qb.where(Properties.FirstName.eq("Joe"),
qb.or(Properties.YearOfBirth.gt(1970),
qb.and(Properties.YearOfBirth.eq(1970), Properties.MonthOfBirth.ge(10))));
List youngJoes = qb.list();


范例6：获取某列对象

1.
picJsonDao.loadByRowId(picId);




2.增添/插入、修改

插入数据更加简单，也是只要一句代码便能搞定!



1.
public void addToPhotoTable(Photo p)
2.
{
3.
photoDao.insert(p);
4.
}


插入时需要new一个新的对象，范例如下:



1.
DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
2.
db = helper.getWritableDatabase();
3.
daoMaster = new DaoMaster(db);
4.
daoSession = daoMaster.newSession();
5.
noteDao = daoSession.getNoteDao();
6.
Note note = new Note(null, noteText, comment, new Date());
7.
noteDao.insert(note);


修改更新：

1.
photoDao.insertOrReplace(photo);
2.
photoDao.insertInTx(photo);


3.删除:

(1)清空表格数据



1.
/** 清空相册图片列表的数据 */
2.
public void clearPhoto()
3.
{
4.
photoDao.deleteAll();
5.
}


(2)删除某个对象

public void deleteCityInfo(int cityId)
{
QueryBuilder<DBCityInfo> qb = cityInfoDao.queryBuilder();
DeleteQuery<DBCityInfo> bd = qb.where(Properties.CityId.eq(cityId)).buildDelete();
bd.executeDeleteWithoutDetachingEntities();
}


参考：https://github.com/greenrobot/greenDAO/issues/34



由上可见，使用greenDAO进行数据库的增删改查时及其方便，而且性能极佳。



(三)常用方法笔记

1.在Application实现得到DaoMaster和DaoSession的方法：



01.
private static DaoMaster daoMaster;
02.
private static DaoSession daoSession;
03.
/**
04.
* 取得DaoMaster
05.
*
06.
* @param context
07.
* @return
08.
*/
09.
public static DaoMaster getDaoMaster(Context context)
10.
{
11.
if (daoMaster == null)
12.
{
13.
OpenHelper helper = new DaoMaster.DevOpenHelper(context, HBContant.DATABASE_NAME, null);
14.
daoMaster = new DaoMaster(helper.getWritableDatabase());
15.
}
16.
return daoMaster;
17.
}
18.
/**
19.
* 取得DaoSession
20.
*
21.
* @param context
22.
* @return
23.
*/
24.
public static DaoSession getDaoSession(Context context)
25.
{
26.
if (daoSession == null)
27.
{
28.
if (daoMaster == null)
29.
{
30.
daoMaster = getDaoMaster(context);
31.
}
32.
daoSession = daoMaster.newSession();
33.
}
34.
return daoSession;
35.
}


2.增删改查工具类:

01.
public class DBHelper
02.
{
03.
private static Context mContext;
04.
private static DBHelper instance;
05.

06.
private CityInfoDBDao cityInfoDao;
07.

08.
private DBHelper()
09.
{
10.
}
11.

12.
public static DBHelper getInstance(Context context)
13.
{
14.
if (instance == null)
15.
{
16.
instance = new DBHelper();
17.
if (mContext == null)
18.
{
19.
mContext = context;
20.
}
21.

22.
// 数据库对象
23.
DaoSession daoSession = HBApplication.getDaoSession(mContext);
24.
instance.cityInfoDao = daoSession.getCityInfoDBDao();
25.
}
26.
return instance;
27.
}
28.

29.
/** 添加数据 */
30.
public void addToCityInfoTable(CityInfo item)
31.
{
32.
cityInfoDao.insert(item);
33.
}
34.

35.
/** 查询 */
36.
public List<EstateLoveListJson> getCityInfoList()
37.
{
38.
QueryBuilder<CityInfo> qb = cityInfoDao.queryBuilder();
39.
return qb.list();
40.
}
41.

42.
/** 查询 */
43.
public List<CityInfo> getCityInfo()
44.
{
45.
return cityInfoDao.loadAll();// 查找图片相册
46.
}
47.

48.
/** 查询 */
49.
public boolean isSaved(int Id)
50.
{
51.
QueryBuilder<CityInfo> qb = cityInfoDao.queryBuilder();
52.
qb.where(Properties.Id.eq(Id));
53.
qb.buildCount().count();
54.
return qb.buildCount().count() > 0 ? true : false;// 查找收藏表
55.
}
56.

57.
/** 删除 */
58.
public void deleteCityInfoList(int Id)
59.
{
60.
QueryBuilder<CityInfo> qb = cityInfoDao.queryBuilder();
61.
DeleteQuery<CityInfo> bd = qb.where(Properties.Id.eq(Id)).buildDelete();
62.
bd.executeDeleteWithoutDetachingEntities();
63.
}
64.

65.
/** 删除 */
66.
public void clearCityInfo()
67.
{
68.
cityInfoDao.deleteAll();
69.
}
70.

71.
/** 通过城市id查找其类型id */
72.
public int getTypeId(int cityId)
73.
{
74.
QueryBuilder<CityInfo> qb = cityInfoDao.queryBuilder();
75.
qb.where(Properties.Id.eq(cityId));
76.
if (qb.list().size() > 0)
77.
{
78.
return qb.list().get(0).getTypeId();
79.
}
80.
else
81.
{
82.
return 0;
83.
}
84.
}
85.

86.
/** 多重查询 */
87.
public List<CityInfo> getIphRegionList(int cityId)
88.
{
89.
QueryBuilder<CityInfoDB> qb = cityInfoDao.queryBuilder();
90.
qb.where(qb.and(Properties.CityId.eq(cityId), Properties.InfoType.eq(HBContant.CITYINFO_IR)));
91.
qb.orderAsc(Properties.Id);// 排序依据
92.
return qb.list();
93.
}
94.
}



DaoMaster：一看名字就知道它是Dao中的最大的官了。它保存了sqlitedatebase对象以及操作DAO classes（注意：不是对象）。其提供了一些创建和删除table的静态方法，其内部类OpenHelper和DevOpenHelper实现了SQLiteOpenHelper并创建数据库的框架。

DaoSession：会话层。操作具体的DAO对象（注意：是对象），比如各种getter方法。

XXXDao：实际生成的某某DAO类，通常对应具体的java类，比如NoteDao等。其有更多的权限和方法来操作数据库元素。

XXXEntity：持久的实体对象。通常代表了一个数据库row的标准java properties。

