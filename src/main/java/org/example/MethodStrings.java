package org.example;

public enum MethodStrings {

     readlog("public static java.lang.String readLoglog()throws Exception{\n" +
            "            java.io.File file = new java.io.File(\"logCache.txt\");//Text文件bai\n" +
            "            java.io.BufferedReader br = new java.io.BufferedReader(new  java.io.FileReader(file));//构造一个duBufferedReader类来读取zhi文件\n" +
            "            java.lang.String s = null;\n" +
            "            java.lang.StringBuffer sb=new java.lang.StringBuffer();\n" +
            "            while((s = br.readLine())!=null){\n" +
             "               sb.append(s);" +
            "            }\n" +
            "            br.close();;\n" +
            "            return sb.toString();\n" +
            "        }"),

    getProcessId("public static String getProcessIdlog()" +
            "{java.lang.management.RuntimeMXBean runtimeMXBean " +
            "= java.lang.management.ManagementFactory.getRuntimeMXBean(); " +
            "return (runtimeMXBean.getName().split(\"@\")[0]);}"),
    Stringlog("public static String writeLoglog(long startTime,java.lang.String processID,java.lang.String sql,long costTime)" +
            "{\n"+
            "        java.lang.String filePath = \"Log.txt\";\n"+
            "        java.lang.String message = java.lang.Long.toString(startTime)+\",\"+processID+\",\"+sql+\",\"+java.lang.Long.toString(costTime);" +
            "        java.lang.String res = \"\";\n" +
            "        java.io.FileWriter fw = null;\n" +
            "        java.io.PrintWriter pw = null;\n" +
            "        java.io.File f = new java.io.File(filePath);\n" +
            "        try {\n" +
            "            fw = new java.io.FileWriter(f,true);\n" +
            "            pw = new java.io.PrintWriter(fw);\n" +
            "            pw.println(message);\n" +
            "        } catch (java.io.IOException e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.out.println(\"写入txt文件出现异常！\");\n" +
            "        }finally {\n" +
            "            try {\n" +
            "                pw.flush();\n" +
            "                fw.flush();\n" +
            "                pw.close();\n" +
            "                fw.close();\n" +
            "                res = \"log out successed\";\n" +
            "            } catch (java.io.IOException e) {\n" +
            "                e.printStackTrace();\n" +
            "                System.out.println(\"在刷新/关闭txt文件出现异常！\");\n" +
            "                res = \"log out failed\";\n" +
            "            }\n" +
            "        }\n" +
            "        return res;\n" +
            "    }"),
    logExceptionAndCache("public static String logExceptionAndCache(java.lang.String file,long startTime,java.lang.String processID,java.lang.String Exception)" +
            "{\n"+
//            " if (\"logCache.txt\".equals(file)){\n" +
//            "                        if (readLoglog().length()>1){\n" +
//            "                            if (processID.equals(readLoglog().split(\",\")[1]))\n" +
//            "                                return null;\n" +
//            "                        } \n" +
//            "                    }\n"+
            "        java.lang.String filePath =file;\n"+
            "        java.lang.String message = java.lang.Long.toString(startTime)+\",\"+processID+\",\"+Exception;" +
            "        java.lang.String res = \"\";\n" +
            "        java.io.FileWriter fw = null;\n" +
            "        java.io.PrintWriter pw = null;\n" +
            "        java.io.File f = new java.io.File(filePath);\n" +
            "        try {\n" +
            "            fw = new java.io.FileWriter(f,true);\n" +
            "            pw = new java.io.PrintWriter(fw);\n" +
            "            pw.println(message);\n" +
            "        } catch (java.io.IOException e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.out.println(\"写入txt文件出现异常！\");\n" +
            "        }finally {\n" +
            "            try {\n" +
            "                pw.flush();\n" +
            "                fw.flush();\n" +
            "                pw.close();\n" +
            "                fw.close();\n" +
            "                res = \"log out successed\";\n" +
            "            } catch (java.io.IOException e) {\n" +
            "                e.printStackTrace();\n" +
            "                System.out.println(\"在刷新/关闭txt文件出现异常！\");\n" +
            "                res = \"log out failed\";\n" +
            "            }\n" +
            "        }\n" +
            "        return res;\n" +
            "    }"),
     strTojson (" public static com.alibaba.fastjson.JSONObject strTojsonlog(java.lang.String[] str) {\n" +
            "\n" +
            "        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();\n" +
            "        com.alibaba.fastjson.JSONObject in_jsonObject = new com.alibaba.fastjson.JSONObject();\n" +
            "        jsonObject.put(\"topic\", \"test\");\n" +
            "        \n" +
            "        java.lang.String[] key = {\"processID\", \"strings\", \"start_time\"};\n" +
            "        for (int index = 0; index < key.length; index++) {\n" +
            "            in_jsonObject.put(key[index], str[index]);\n" +
            "        }\n" +
            "        jsonObject.put(\"message\", in_jsonObject.toJSONString());\n" +
            "\n" +
            "        return jsonObject;\n" +
            "    }"),
    postToKafa("public static java.lang.String postToKafalog(com.alibaba.fastjson.JSONObject jsonObject) {\n" +
            "        java.net.HttpURLConnection connection = null;\n" +
            "        java.lang.String httpUrl = \"http://113.106.111.75:5040/demo/kafka/produce\";" +
            "        java.io.InputStream is = null;\n" +
            "        java.io.OutputStream os = null;\n" +
            "        java.io.BufferedReader br = null;\n" +
            "        java.lang.StringBuffer buffer = new StringBuffer();\n" +
            "        try {\n" +
            "            java.net.URL url = new URL(httpUrl);// 创建连接\n" +
            "            connection = (HttpURLConnection) url.openConnection();\n" +
            "            connection.setDoOutput(true);\n" +
            "            connection.setDoInput(true);\n" +
            "            connection.setUseCaches(false);\n" +
            "            connection.setInstanceFollowRedirects(true);\n" +
            "            connection.setRequestMethod(\"POST\"); // 设置请求方式\n" +
            "            connection.setRequestProperty(\"Accept\", \"application/json\"); // 设置接收数据的格式\n" +
            "            connection.setRequestProperty(\"Content-Type\", \"application/json\"); // 设置发送数据的格式\n" +
            "            connection.connect();\n" +
            "            java.io.OutputStreamWriter out = new java.io.OutputStreamWriter(connection.getOutputStream(), \"UTF-8\"); // utf-8编码\n" +
            "            //out.write(jsonObject.toJSONString());\n" +
            "//            out.write(jsonObject.toString());\n" +
            "            out.write(String.valueOf(jsonObject));\n" +
            "\n" +

            "\n" +
            "            out.flush();\n" +
            "            out.close();\n" +
            "\n" +
            "            int code = connection.getResponseCode();\n" +
            "\n" +
            "            if (code == 200) {\n" +
            "                is = connection.getInputStream();\n" +
            "            } else {\n" +
            "                is = connection.getErrorStream();\n" +
            "            }\n" +
            "\n" +
            "            // 读取响应\n" +
            "            int length = (int) connection.getContentLength();// 获取长度\n" +
            "            if (length != -1) {\n" +
            "                byte[] data = new byte[length];\n" +
            "                byte[] temp = new byte[512];\n" +
            "                int readLen = 0;\n" +
            "                int destPos = 0;\n" +
            "                while ((readLen = is.read(temp)) > 0) {\n" +
            "                    System.arraycopy(temp, 0, data, destPos, readLen);\n" +
            "                    destPos += readLen;\n" +
            "                }\n" +
            "                String result = new String(data, \"UTF-8\"); // utf-8编码\n" +
            "                return result;\n" +
            "            }\n" +


            "            // 请求返回的状态\n" +
            "            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {\n" +
            "                System.out.println(\"连接成功\");\n" +
            "                // 请求返回的数据\n" +
            "               java.io.InputStream in1 = connection.getInputStream();\n" +
            "                try {\n" +
            "                    String readLine = new String();\n" +
            "                    java.io.BufferedReader responseReader = new java.io.BufferedReader(new java.io.InputStreamReader(in1, \"UTF-8\"));\n" +
            "                    while ((readLine = responseReader.readLine()) != null) {\n" +
            "                        buffer.append(readLine).append(\"\\n\");\n" +
            "                    }\n" +
            "                    responseReader.close();\n" +
            "                    System.out.println(buffer.toString());\n" +
            "\n" +
            "                } catch (Exception e1) {\n" +
            "                    e1.printStackTrace();\n" +
            "                }\n" +
            "            } else {\n" +
            "                System.out.println(\"error++\");\n" +
            "            }\n" +
            "        } catch (java.io.IOException e) {\n" +
            "            System.out.println(\"Exception occur when send http post request!\");\n" +
            "        }\n" +
            "        return buffer.toString();\n" +
            "    }");

    private String Method;
    private MethodStrings(String Method) {
        this.Method = Method;
    }

    public String getMethod() {
        return Method.toString();
    }

    public void setMethod(String method) {
       this.Method = method;
    }

    public String getName() {
        return Method;
    }
}
