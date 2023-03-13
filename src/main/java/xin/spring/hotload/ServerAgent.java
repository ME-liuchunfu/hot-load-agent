package xin.spring.hotload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器代理
 *
 * @author spring
 * @date 2023/03/09
 */
public class ServerAgent {

    public static void print(String str) {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(time);
        System.out.println(date + "----" + str);
    }

    public static byte[] fileToBytes(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();
        return bytes;
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        long startTime = System.currentTimeMillis();
        print(Thread.currentThread().getName() + ":agent 启动成功,开始重定义对象....");
        print("args:" + args);
        String[] classPathArr = args.split(",");
        Class[] allClass = inst.getAllLoadedClasses();
//        print("allClass:" + Arrays.toString(allClass));
        Map<String, String> classMap = new HashMap<String, String>(16);

        String className;
        String filePath;
        for (int i = 0; i < classPathArr.length; i++) {
            String classPath = classPathArr[i];
            print("classpath:" + classPath);
            String[] arr = classPath.split("/");
            className = arr[arr.length - 1];
            filePath = classPath.replaceAll("\\.", "/") + ".class";
            classMap.put(className, filePath);
            print("targetPath:" + filePath);
        }

        print(classMap.keySet().toString());

        try {
            boolean isSuccess = false;

            for (int i = 0; i < allClass.length; i++) {
                Class c = allClass[i];
                className = c.getName();
                print("className:" + className);
                filePath = classMap.get(className);
                if (filePath != null) {
                    print("正在热更新class:" + className);
                    File file = new File(filePath);
                    try {
                        byte[] bytes = fileToBytes(file);
                        print("文件大小：" + bytes.length);
                        ClassDefinition classDefinition = new ClassDefinition(c, bytes);
                        inst.redefineClasses(new ClassDefinition[]{classDefinition});
                        isSuccess = true;
                    } catch (IOException var18) {
                        isSuccess = false;
                        var18.printStackTrace();
                        break;
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            if (isSuccess) {
                print(args + "热更新成功,runtime(" + (endTime - startTime) + ")....finish");
            } else {
                print(args + "热更新失败,runtime(" + (endTime - startTime) + ")....failed");
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        }
    }

}
