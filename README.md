# **EasySql**
一个轻便的Android Sqlite orm库，可以方便的利用注解来操作和生成数据库，注解方面的实现，部分参考了XUtils框架的实现，不要见怪。

**使用说明**
--------


----------


**初始化**

EasySql的初始化不需要太多复杂的操作，只需要根据需要，初始化SqliteDbConfig就行了，一个SqliteDbConfig对应着本地的一个db文件，有version，db文件名字，映射等设置，具体可以查看代码。默认的db文件目录是在
**data/data/包名/databases/**，

初始化的代码如下:

    EasySql.hatch(new SqliteDBConfig.Builder().addTableMapping(Test.class,new TestMapping).setVersion().setLogger().setDataBaseName().setOnDbVersionChangeListener().build(this));


----------


**表结构**

   EasySql提供了两种方式去描述一个表结构，一个是注解，一个是自定义继承的方式，如果你只是简单的想要做一些数据的持久化工作并且追求便利性的话，可以直接利用注解方式，如果你想要更大的灵活性，和做更多的自定义的工作，可以用自定义继承的方式。
    
**1.注解方式**

   EasySql提供了两个注解描述，一个是描述表的，一个是描述列的，分别为**Column**和**Table**，用法如下：
   

      @Table(name = "persion", onCreated = "CREATE UNIQUE INDEX age_index ON download(age)")
      
      @Column(name = "key",isId = true,autoGen = true, property = "UNIQUE")
      
其中，**Table**和**Column**的**name**属性分别代表着数据库中的**表名**和**列名**，
而Table中的onCreated 属性，则表示Sqlite在回调onCreate之后，执行的Sql语句，你可以根据需要自定义的添加一些Sql语句，例如像事例说的那样，添加一个索引。
**Column**的**isId**属性，则表示是否是主键，**autogen**则表示是否自增，**property**则提供能力给你添加更多的列描述。

**2.自定义继承方式**

   自定义继承的方式，其实就是自己写代码实现了，只需要继承TableMapping这个类，并实现里面的abstract方法就可以了，需要复写以下几个方法：

    /**
     * 获取表名，表名字直接定义在这里
     *
     * @return
     */
    public abstract String getTableName();
    
    /**
     * 生成列，列的定义在这里
     *
     * @return
     */
    public abstract List<ColumnMapping> generateCloumn();
    
    /**
     * 这里设置目标类和contentvalue的转换和映射
     *
     * @param bean
     * @return ContentValues
     */
    public abstract ContentValues beanToContentValues(T bean);
    
    /**
     * 这里设置目标类和Cursor的转换和映射T
     *
     * @param cursor
     * @return T
     */
    public abstract T cursorToBean(Cursor cursor);

同时也提供了**onUpgrade** 和 **onDowngrade**的方法，可供复写，用来兼容数据库升级和降级的情况。
继承了之后，还需要应用，需要在创建**SqliteDBConfig**的时候通过**addTableMapping**方法添加进去这个类和描述的映射就可以了：事例代码如下：

    EasySql.hatch(new SqliteDBConfig.Builder().addTableMapping(Test.class,new TestMapping).build(this));
   


----------

**查询操作**

查询操作就是一个链式调用的过程，可以很方便直观的组合各种查询条件，例如where，or，limit,offset，orderBy等

       EasySql.getDefault().query(Persion.class)
       .where("age","BETWEEN",new int[]{10,24})
       .or("sex","=","man")
       .limit(100)
       .offset(10)
       .orderBy("age")
       .all();
    


----------
**增删改操作**

  增删改差操作跟查询操作一样，使用非常方便。有多个重构方法，可供选择，事例代码如下：
   

        EasySql.getDefault().curd(Persion.class).insert()
        EasySql.getDefault().curd(Persion.class).update()
        EasySql.getDefault().curd(Persion.class).delete()
        EasySql.getDefault().curd(Persion.class).deleteAll()
        EasySql.getDefault().curd(Persion.class).deleteBy()
        EasySql.getDefault().curd(Persion.class).deleteById()
