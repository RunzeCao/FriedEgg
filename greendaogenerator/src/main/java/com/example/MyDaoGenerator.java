package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    //辅助文件生成的相对路径
//    public static final String DAO_PATH = "GreenDaoGenerator\\src\\main\\java\\com\\example";
    public static final String DAO_PATH = "app\\src\\main\\java-gen";
    //辅助文件的包名
    public static final String PACKAGE_NAME = "greendao";
    //数据库的版本号
    public static final int DATA_VERSION_CODE = 1;

    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(DATA_VERSION_CODE,PACKAGE_NAME);
        addTable(schema, "JokeCache");
        addTable(schema, "FreshNewsCache");
        addTable(schema, "PictureCache");
        addTable(schema, "SisterCache");
        addTable(schema, "VideoCache");
        //生成Dao文件路径
        new DaoGenerator().generateAll(schema, DAO_PATH);
    }

    private static void addTable(Schema schema,String tableName){
        Entity table = schema.addEntity(tableName);
        //主键id自增长
        table.addIdProperty().primaryKey().autoincrement();
        //请求结果
        table.addStringProperty("result");
        //页数
        table.addIntProperty("page");
        //插入时间，暂时无用
        table.addLongProperty("time");
    }
}
