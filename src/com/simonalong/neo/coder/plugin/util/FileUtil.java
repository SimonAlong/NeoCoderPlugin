package com.simonalong.neo.coder.plugin.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shizi
 * @since 2019/12/3 11:48 上午
 */
public class FileUtil {

    private static Charset CRYPTO_CHARSET = StandardCharsets.UTF_8;

    /**
     * 获取文件
     */
    public static File getFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setReadable(true);
        file.setWritable(true);
        return file;
    }

    /**
     * 获取文件夹类型的文件
     */
    public static File getDirectFile(String fileName) {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.mkdir();
        }
        file.setReadable(true);
        file.setWritable(true);
        return file;
    }

    @SuppressWarnings("all")
    public static Boolean createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists() && file.createNewFile()) {
            return file.setReadable(true) && file.setWritable(true);
        }
        return false;
    }

    public static Boolean exist(String fileFullPath){
        File file = new File(fileFullPath);
        return file.exists();
    }

    /**
     * 向文件中写入对应的文件
     */
    public static void writeFile(File file, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes(CRYPTO_CHARSET));
            outputStream.flush();
        }
    }

    public static void writeFile(String file, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(new File(file))) {
            outputStream.write(content.getBytes(CRYPTO_CHARSET));
            outputStream.flush();
        }
    }

    public static BufferedReader readFile(File file) throws IOException {
        return Files.newBufferedReader(file.toPath(), CRYPTO_CHARSET);
    }

    public static String readFromFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = readFile(file)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 读取资源文件中的内容
     * @param cls 类所在的位置
     * @param resourceFileName 资源文件中的位置比如：/script/base.groovy，其中前面一定要有"/"
     * @return 文件的字符数据
     */
    public static String readFromResource(Class cls, String resourceFileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = cls.getResourceAsStream(resourceFileName);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } finally {
            inputStream.close();
            assert bufferedReader != null;
            bufferedReader.close();
        }
        return stringBuilder.toString();
    }

    /**
     * 通过文件的绝对路径读取文件信息
     */
    public static String read(String filePath) throws IOException {
        FileReader fileReader;
        StringBuilder stringBuilder = new StringBuilder();
        fileReader = new FileReader(filePath);
        char[] cbuf = new char[32];
        int hasRead = 0;
        while ((hasRead = fileReader.read(cbuf)) > 0) {
            stringBuilder.append(cbuf, 0, hasRead);
        }
        return stringBuilder.toString();
    }

    public static FileInputStream readToStream(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }

    /**
     * 向绝对路径中的文件写入对应的数据信息
     */
    public static void write(String filePath, String content) throws IOException {
        writeFile(getFile(filePath), content);
    }

    public static  String readUrl(String FileName) throws IOException {
        String read;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(FileName);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setReadTimeout(5000);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            while ((read = br.readLine()) != null) {
                stringBuilder.append(read);
            }
            br.close();
        } catch (IOException e) {

        }
        return stringBuilder.toString();
    }


    public static void appendFile(String filePath, List<String> content) {
        FileWriter fw = null;
        if (content == null || content.isEmpty()) {
            return;
        }
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File file = getFile(filePath);
            if ((!file.exists()) &&
                    (!file.createNewFile())) {
                throw new IOException("create file '" + filePath +
                        "' failure.");
            }
            fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            for (String str : content) {
                if(str != null && !"".equals(str)) {
                    pw.println(str);
                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendFile(String filePath, String content) {
        List<String> contents = new ArrayList<>();
        contents.add(content);
        appendFile(filePath, contents);
    }

    public static boolean delete(String filePath) {
        return delete(new File(filePath));
    }

    public static boolean delete(File file) {
        if (null == file || !file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            File[] childFile = file.listFiles();
            if (null == childFile) {
                return false;
            }
            for (File fileMeta : childFile) {
                delete(fileMeta);
            }
        }

        return file.delete();
    }
}
