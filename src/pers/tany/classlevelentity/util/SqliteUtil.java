package pers.tany.classlevelentity.util;


import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteUtil {
    static String name;
    static String dataBaseName;

    //    创建并且链接数据库
    public static Connection getConnection(String name, String dataBaseName) throws SQLException, IOException {
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.setSharedCache(true);
        sqLiteConfig.enableRecursiveTriggers(true);
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(sqLiteConfig);

        File dir = new File(new File("plugins").getCanonicalPath() + "\\" + name);
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        sqLiteDataSource.setUrl("jdbc:sqlite:" + new File("plugins").getCanonicalPath() + "\\" + name + "\\" + dataBaseName + ".db");
        SqliteUtil.name = name;
        SqliteUtil.dataBaseName = dataBaseName;
        return sqLiteDataSource.getConnection();
    }

    //    删除数据库
    public static void delDatabase() throws IOException {
        File file = new File(new File("plugins").getCanonicalPath() + "\\" + name + "\\" + dataBaseName + ".db");
        file.delete();
    }

    //    执行sql语句
    public static void executeSqlStatement(Connection connection, String statement) throws SQLException {
        connection.createStatement().executeUpdate(statement);
    }

    //    查询指令
    public static ResultSet selectTable(Connection connection, String statement) throws SQLException {
        return connection.createStatement().executeQuery(statement);
    }
}
