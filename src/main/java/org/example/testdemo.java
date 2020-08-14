package org.example;

//-javaagent:E:\javaProject\probe\target\SQLpooltest-1.0-SNAPSHOT-jar-with-dependencies.jar
import java.sql.*;
import java.util.logging.Logger;

public class testdemo extends Thread {
    @Override
    public synchronized void run(){
        String url="jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
        String username="root";
        String password="123456";
        String string = url+"  "+username+" "+password+" " ;
        String sql="select id from tbl ";
        Connection conn = null;
        try{
            conn= DriverManager.getConnection(url, username, password);
            Statement sta=conn.createStatement();
            Statement sta1=conn.createStatement();
            Statement sta2=conn.createStatement();

            System.out.println(sta);
            ResultSet rs=sta.executeQuery(sql);
            rs.toString();
            while(rs.next()){
                System.out.println(rs.getInt("id")+"..." );
            }
            rs.close();
            sta.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Logger logger = Logger.getLogger(" ") ;
        logger.info("fff");
        System.out.println("please enter number 1 continue");
        testdemo testdemo1 = new testdemo();
        testdemo1.run();
        testdemo testdemo2 = new testdemo();
        testdemo2.run();
        testdemo testdemo4 = new testdemo();
        testdemo4.run();

//        String url="jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
//        String username="root";
//        String password="123456";
//        String string = url+"  "+username+" "+password+" " ;
//        String sql="select id from tbl ";
//        Connection conn = null;
//        try{
//            conn= DriverManager.getConnection(url, username, password);
//            Statement sta=conn.createStatement();
//            ResultSet rs=sta.executeQuery(sql);
//            rs.toString();
//            while(rs.next()){
//                System.out.println(rs.getInt("id")+"..." );
//            }
//            rs.close();
//            sta.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

//-javaagent:E:\javaProject\probe-master\probe-master\target\SQLpooltest-1.0-SNAPSHOT-jar-with-dependencies.jar
}
