package org.example;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;


public class Test implements ClassFileTransformer {

//    public  static  String postToKafa = MethodStrings.postToKafa.getMethod();
    public  static String readlog = MethodStrings.readlog.getMethod();
    public  static String getProcessId=MethodStrings.getProcessId.getMethod();
    public  static  String Stringlog = MethodStrings.Stringlog.getMethod();
    public  static  String logExceptionAndCache=MethodStrings.logExceptionAndCache.getMethod();
//    public  static String strTojson = MethodStrings.strTojson.getMethod();

    //<.............>
    public  static String[] agentargs = jdbcPremain.getAgentargs().split("&");
    public  static boolean  flag= agentargs.length>4?("1".equals(agentargs[4])):true;

    public static void logException(CtMethod[] arr,CtClass cl) throws CannotCompileException, NotFoundException {
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for (CtMethod method:arr
        ) {
            method.addCatch("{java.lang.String strings= $e.toString();" +
                    "java.lang.String Id=getProcessIdlog();" +
                    "long start = System.currentTimeMillis();" +
                    "java.lang.String[] arr = {Id,strings, java.lang.String.valueOf(start)};" +
                    "java.lang.String log_res = logExceptionAndCache(\"logException.txt\",start,Id,strings);" +
                    "java.lang.String log_res1 = logExceptionAndCache(\"logExceptionCache.txt\",start,Id,strings);" +
                    " throw $e; }", etype);
        }
    }

    public static void logExceptionDriver(CtMethod[] arr,CtClass cl) throws CannotCompileException, NotFoundException {

        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for (CtMethod method:arr
        ) {
            method.addCatch("{java.lang.String strings= $e.toString();" +
                    "java.lang.String Id=getProcessIdlog();" +
                    "long start = System.currentTimeMillis();" +
                    "java.lang.String log_res = logExceptionAndCache(\"logException.txt\",start,Id,strings);" +
                    //            "java.lang.String log_res1 = logExceptionAndCache(\"logExceptionCache.txt\",start,Id,strings);" +
                    " throw $e; }", etype);
        }
    }



    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)  {

        ClassPool pool = ClassPool.getDefault();
//        String newName = className.replace("/",".");
        /*
        * 1. preparedStatements通过java.sql.Connnection的(prepareCall|Statement)方法进行SQL传递,然后通过PreparedStatement.executeQuery()执行SQL语句；
        * 2. statement通过java.sql.Statement的execute($|Update|Query|Batch)方法传递SQL并执行SQL语句；
        * 3. mysql-connector 驱动是通过类名：com.mysql.cj.jdbc.StatementImpl和类名：com.mysql.cj.jdbc.ConnectionImpl实现的,是通过Statement实现的
        *
        * */



        try {
            CtClass cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            cl.addMethod(CtMethod.make(getProcessId,cl));
            cl.addMethod(CtMethod.make(logExceptionAndCache, cl));
            cl.addMethod(CtMethod.make(Stringlog, cl));
            cl.addMethod(CtMethod.make(readlog,cl));

            if (( Arrays.toString(cl.getInterfaces()).contains("java.sql.Statement")||Arrays.toString(cl.getInterfaces()).contains("java.sql.Connection")
                    ||Arrays.toString(cl.getInterfaces()).contains("java.sql.PrepareStatement"))&& !cl.isInterface()){
                /*
                * mysql-connector执行SQL主要通过com.mysql.cj.jdbc.ConnectionImpl和com.mysql.cj.jdbc.StatementImpl实现
                * mysql-connector在执行Statement对象的
                * */
                CtMethod[] ctMethods = cl.getDeclaredMethods();
                logException(ctMethods,cl);
                String clazzName = cl.getName();
//                判断是通过那个方法执行SQL的
                String curStatement = currentStatement(ctMethods);
//                System.out.println(curStatement);
                if (curStatement.equals("PreparedStatement")){
//                    通过PreparedStatement方式执行SQL的
                    captureSqlFromPreparedStatement(pool,cl);
//                    CtClass esqlclazz = SqlExecuteTime(pool,cl);
//                    byte[] epbytes = esqlclazz.toBytecode();

                }else if(curStatement.equals("Statement")){
//                    通过Statement方式执行SQL的
                    captureSqlFromStatement(pool,cl);
                    byte[] ddbytes =cl.toBytecode();
                    return ddbytes;

                }else {
                    System.out.println("不能判断是通过PreparedStatement对象还是Statement对象实现SQL语句的。");
                }
            } else if(className.equals("com/mysql/cj/jdbc/ClientPreparedStatement")) {
                CtMethod ad = cl.getDeclaredMethod("getInstance");
                ad.addLocalVariable("sql", pool.get("java.lang.String"));
                ad.insertBefore("sql = $2;");
                ad.insertAfter("org.example.MonitorLog.setSql(sql);");

                ad.addLocalVariable("Database",pool.get("java.lang.String"));
                ad.addLocalVariable("User",pool.get("java.lang.String"));
                ad.addLocalVariable("HostPort",pool.get("java.lang.String"));
                ad.insertBefore("Database=$1.getDatabase();");
                ad.insertBefore("User=$1.getUser();");
                ad.insertBefore("HostPort=$1.getHostPortPair();");
                ad.insertAfter("org.example.MonitorLog.setUser(User);");
                ad.insertAfter("org.example.MonitorLog.setPort(HostPort);");
                ad.insertAfter("org.example.MonitorLog.setDatabase(Database);");


//                ad.insertAfter("System.out.println(\"<------A PreparedStatement object. This step is for SQL. Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
//                ad.insertAfter("System.out.println(\"sql:\"+sql);");

                CtMethod ct = cl.getDeclaredMethod("executeQuery");
                ct.addLocalVariable("id", pool.get("java.lang.String"));
                ct.insertBefore("id = org.example.ProcessId.getProcessId();");

                ct.addLocalVariable("start", CtClass.longType);
                ct.insertBefore("start = System.currentTimeMillis();");

                ct.addLocalVariable("cost", CtClass.longType);
                ct.insertAfter("cost = System.currentTimeMillis()-start;");


//                ct.insertAfter("System.out.println(\"<------A PreparedStatement executeQuery(). Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
//                ct.insertAfter("System.out.println(\"start:\"+start);");
//                ct.insertAfter("System.out.println(\"processID:\"+id);");
//                ct.insertAfter("System.out.println(\"cost:\"+cost);");

                ct.insertAfter("org.example.MonitorLog.setStart(start);");
                ct.insertAfter("org.example.MonitorLog.setCost(cost);");
                ct.insertAfter("org.example.MonitorLog.setProcessID(id);");

                ct.addLocalVariable("log_res", pool.get("java.lang.String"));
//                ct.insertAfter("log_res = org.example.MonitorLog.writeLog(o_start,o_processID,o_sql,o_cost);");

                ct.addLocalVariable("Database",pool.get("java.lang.String"));
                ct.addLocalVariable("User",pool.get("java.lang.String"));
                ct.addLocalVariable("HostPort",pool.get("java.lang.String"));
                ct.insertBefore("Database=this.connection.getDatabase();");
                ct.insertBefore("User=this.connection.getUser();");
                ct.insertBefore("HostPort=this.connection.getHostPortPair();");
                ct.insertAfter("org.example.MonitorLog.setUser(User);");
                ct.insertAfter("org.example.MonitorLog.setPort(HostPort);");
                ct.insertAfter("org.example.MonitorLog.setDatabase(Database);");

                ct.insertAfter("log_res = org.example.MonitorLog.tests();");

                //增删操作：
                CtMethod inserDelete = cl.getDeclaredMethod("executeUpdate");
                inserDelete.addLocalVariable("id", pool.get("java.lang.String"));
                inserDelete.insertBefore("id = org.example.ProcessId.getProcessId();");

                inserDelete.addLocalVariable("start", CtClass.longType);
                inserDelete.insertBefore("start = System.currentTimeMillis();");


                inserDelete.addLocalVariable("cost", CtClass.longType);
                inserDelete.insertAfter("cost = System.currentTimeMillis()-start;");

//                inserDelete.insertAfter("System.out.println(\"<------A PreparedStatement executeUpdate(). Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
//                inserDelete.insertAfter("System.out.println(\"start:\"+start);");
//                inserDelete.insertAfter("System.out.println(\"processID:\"+id);");
//                inserDelete.insertAfter("System.out.println(\"cost:\"+cost);");

                inserDelete.insertAfter("org.example.MonitorLog.setStart(start);");
                inserDelete.insertAfter("org.example.MonitorLog.setCost(cost);");
                inserDelete.insertAfter("org.example.MonitorLog.setProcessID(id);");


                inserDelete.addLocalVariable("log_res", pool.get("java.lang.String"));
//                ct.insertAfter("log_res = org.example.MonitorLog.writeLog(o_start,o_processID,o_sql,o_cost);");

                inserDelete.insertAfter("log_res = org.example.MonitorLog.tests();");
                byte[] ddbytes =cl.toBytecode();
                return ddbytes;
            }

        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }

        return classfileBuffer;
    }
/*
* 获取DriverManager参数
* */
public static CtClass JudgeDriver(ClassPool pool,CtClass ctClass) throws CannotCompileException, NotFoundException {
    CtMethod ct = ctClass.getDeclaredMethod("getConnection",
            new CtClass[]{pool.get("java.lang.String"),pool.get("java.util.Properties"),pool.get("java.lang.Class")});
    ct.addLocalVariable("info",pool.get("java.util.Properties"));
    ct.addLocalVariable("url",pool.get("java.lang.String"));
    ct.addLocalVariable("strings",pool.get("java.lang.String"));
    ct.insertBefore("url=$1;");
    ct.insertBefore("info=$2;");
    ct.insertAfter("if($3!=null){" +
            "strings=url+\" \"+info.toString()+\" \"+$3.toString();}else{" +
            "strings=url+\" \"+info.toString();}");
    ct.addLocalVariable("id",pool.get("java.lang.String"));
    ct.addLocalVariable("start",CtClass.longType);

    ct.insertAfter("id = getProcessIdlog();");
    ct.insertBefore("start = System.currentTimeMillis();");
    ct.addLocalVariable("cost",CtClass.longType);
    ct.insertAfter("cost = System.currentTimeMillis()-start;");
//    ct.insertAfter("System.out.println(\"<------It is a DriverManager:------>\");");
//    ct.insertAfter("System.out.println(\"processID:\"+id);");
//    ct.insertAfter("System.out.println(\"cost:\"+cost);");

    ct.addLocalVariable("log_res",pool.get("java.lang.String"));
    ct.addLocalVariable("judgeflag",CtClass.booleanType);
    ct.insertAfter("judgeflag="+flag+";");

    ct.insertAfter("if(judgeflag)log_res = writeLoglog(start,id,strings,cost);");
    ct.addLocalVariable("stringlog",pool.get("java.lang.String"));
    ct.addLocalVariable("log_res1",pool.get("java.lang.String"));
    ct.insertAfter("stringlog = readLoglog();");
    ct.insertAfter("if (stringlog.length()>1?(id.equals(stringlog.split(\",\"))):true){log_res1 = logExceptionAndCache(\"logCache.txt\",start,id,strings);}");

    return ctClass;
}

    /*
    * 如果 SQL语句 是通过 PrepareStatement实例化对象，需要通过 connection.prepareStatement（）或 connection.prepareCall() 获取SQL参数
    * */
    public static void captureSqlFromPreparedStatement(ClassPool pool, CtClass clazz) throws NotFoundException, CannotCompileException {



//        captureSqlFromPreparCall(pool,clazz);
        CtMethod pm = clazz.getDeclaredMethod("prepareStatement",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        pm.addLocalVariable("sql",pool.get("java.lang.String"));
        pm.insertBefore("sql = $1;");

//        获取进程ID
        pm.addLocalVariable("id",pool.get("java.lang.String"));
        pm.insertBefore("id = org.example.ProcessId.getProcessId();");

//        获取SQL执行的开始时间
        pm.addLocalVariable("start",CtClass.longType);
        pm.insertBefore("start = System.currentTimeMillis();");

//        获取SQL的运行时间
        pm.addLocalVariable("cost",CtClass.longType);
        pm.insertAfter("cost = System.currentTimeMillis()-start;");

//        pm.insertAfter("System.out.println(\"<------A PreparedStatement Object. This is prepareStatement() for SQL:------>\");");
//        pm.insertAfter("System.out.println(\"start:\"+start);");
//        pm.insertAfter("System.out.println(\"processID:\"+id);");
//        pm.insertAfter("System.out.println(\"sql:\"+sql);");
//        pm.insertAfter("System.out.println(\"cost:\"+cost);");
        pm.insertAfter("org.example.MonitorLog.setSql(sql);");
        pm.insertAfter("org.example.MonitorLog.setProcessID(id);");

        //<..数据库名，用户面，端口>


//        SqlExecuteTime(pool,clazz);

//        输出至日志文档
        pm.addLocalVariable("log_res",pool.get("java.lang.String"));
//        pm.insertAfter("log_res = org.example.MonitorLog.tests();");
      //  pm.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

//        return clazz;
    }
    public static CtClass captureSqlFromPreparCall(ClassPool pool, CtClass clazz) throws NotFoundException, CannotCompileException {
//        System.out.println("这是个PreparedStatement对象。");
        CtMethod pm = clazz.getDeclaredMethod("prepareCall",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        pm.addLocalVariable("sql",pool.get("java.lang.String"));
        pm.insertBefore("sql = $1;");


//        获取进程ID
        pm.addLocalVariable("id",pool.get("java.lang.String"));
        pm.insertBefore("id = org.example.ProcessId.getProcessId();");
        pm.insertBefore("org.example.MonitorLog.setProcessID(id);");

//        获取SQL执行的开始时间
        pm.addLocalVariable("start",CtClass.longType);
        pm.insertBefore("start = System.currentTimeMillis();");
//        获取SQL的运行时间
        pm.addLocalVariable("cost",CtClass.longType);
        pm.insertAfter("cost = System.currentTimeMillis()-start;");

//        pm.insertAfter("System.out.println(\"<------A PreparedStatement Object. This is prepareCall() for SQL:------>\");");
//        pm.insertAfter("System.out.println(\"start:\"+start);");
//        pm.insertAfter("System.out.println(\"processID:\"+id);");
//        pm.insertAfter("System.out.println(\"sql:\"+sql);");
//        pm.insertAfter("System.out.println(\"cost:\"+cost);");
        pm.insertAfter("org.example.MonitorLog.setSql(sql);");

//        SqlExecuteTime(pool,clazz);
//        输出至日志文档
        
        pm.addLocalVariable("log_res",pool.get("java.lang.String"));
        pm.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

        return clazz;
    }

    /*
    * 如果 SQL语句是通过Statement实例化对象执行的，需要通过statement.executeQuery()获取SQL参数
    * */
    public static void captureSqlFromStatement(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
//        System.out.println("这是个Statement对象。");
        CtMethod em = clazz.getDeclaredMethod("executeQuery",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        em.addLocalVariable("sql",pool.get("java.lang.String"));
        em.insertBefore("sql = $1;");


//        获取进程ID
        em.addLocalVariable("id",pool.get("java.lang.String"));
        em.insertBefore("id = org.example.ProcessId.getProcessId();");


//        获取SQL执行的开始时间
        em.addLocalVariable("start",CtClass.longType);
        em.insertBefore("start = System.currentTimeMillis();");


//        获取SQL的运行时间
        em.addLocalVariable("cost",CtClass.longType);
        em.insertAfter("cost = System.currentTimeMillis()-start;");


//        em.insertAfter("System.out.println(\"<------A Statement Object. This is execute() for SQL. Achieved by com/mysql/cj/jdbc/StatementImpl:------>\");");
//        em.insertAfter("System.out.println(\"start:\"+start);");
//        em.insertAfter("System.out.println(\"processID:\"+id);");
//        em.insertAfter("System.out.println(\"sql:\"+sql);");
//        em.insertAfter("System.out.println(\"cost:\"+cost);");

//        输出至日志文档
        em.addLocalVariable("Database",pool.get("java.lang.String"));
        em.addLocalVariable("User",pool.get("java.lang.String"));
        em.addLocalVariable("HostPort",pool.get("java.lang.String"));
        em.insertBefore("Database=this.connection.getDatabase();");
        em.insertBefore("User=this.connection.getUser();");
        em.insertBefore("HostPort=this.connection.getHostPortPair();");
        em.insertAfter("org.example.MonitorLog.setUser(User);");
        em.insertAfter("org.example.MonitorLog.setPort(HostPort);");
        em.insertAfter("org.example.MonitorLog.setDatabase(Database);");


        em.insertAfter("org.example.MonitorLog.setSql(sql);");
        em.insertAfter("org.example.MonitorLog.setCost(cost);");
        em.insertAfter("org.example.MonitorLog.setStart(start);");
        em.insertAfter("org.example.MonitorLog.setProcessID(id);");
        em.addLocalVariable("log_res",pool.get("java.lang.String"));
//        em.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

        em.insertAfter("log_res = org.example.MonitorLog.tests();");
//        return clazz;
    }
    public static void SqlExecuteTime(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {

        CtMethod closeStatementm = clazz.getDeclaredMethod("com.mysql.cj.jdbc.ClientPreparedStatement.executeQuery");
//        获取SQL的运行时间
        closeStatementm.addLocalVariable("end",CtClass.longType);
        closeStatementm.insertBefore("end = System.currentTimeMillis();");
//        closeStatementm.insertBefore("System.out.println(\"<------This is execute() for end:------>\");");
//        closeStatementm.insertBefore("System.out.println(\"end:\"+end);");

//        return cpsclazz;
    }


    /*
    * 测试 hellw类 中的 add()方法
    * */
    public static CtClass hellwTest(ClassPool pool) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get("org.example.hellw");
//        System.out.println(clazz);
        CtMethod method = clazz.getDeclaredMethod("add",new CtClass[]{pool.get("java.lang.String"),pool.get("java.lang.String")});
        method.addLocalVariable("start",CtClass.longType);
        method.insertBefore("start = System.currentTimeMillis();");
        System.out.println(method.getName());
        method.insertAfter("org.example.MonitorLog.info3($1,\"./Log.txt\");");

        return clazz;
    }
    /*
    * 判断当前语句执行实例是 Statement 还是 PreparedStatement
    * */

    public static String currentStatement(CtMethod[] ctMethods){
        boolean isStatement=false;
        boolean isPreparedStatement=false;
        String res = "";
        for(CtMethod cm:ctMethods){
            if(cm.getName().contains("execute")){
                isStatement = true;
            }
            if(cm.getName().contains("prepare")){
                isPreparedStatement = true;
            }
        }
        if((isStatement^isPreparedStatement)){
            if(isPreparedStatement){
                res = "PreparedStatement";
            }else if(isStatement){
                res ="Statement";
            }
        }else {
            System.out.println("currentStatement()方法失效，无法判断是PrepareStateMent还是Statement！");
            res = "noStatement";
        }
        return res;
    }

}
