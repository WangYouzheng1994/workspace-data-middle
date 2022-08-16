package org.jeecg.yqwl.datamiddle.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.yqwl.datamiddle.util.custom.StringPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/9/6 9:22
 * @Version: V1.0
 */
@Slf4j
public class FileUtils {
    
    public static String dirSplit = "\\";// linux windows
    
    /**
     * save file accroding to physical directory infomation
     *
     * @param physicalPath physical directory
     * @param istream      input stream of destination file
     * @return
     */
    public static boolean SaveFileByPhysicalDir(String physicalPath, InputStream istream) {
        
        boolean flag = false;
        try {
            OutputStream os = new FileOutputStream(physicalPath);
            int readBytes = 0;
            byte buffer[] = new byte[8192];
            while ((readBytes = istream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, readBytes);
            }
            os.close();
            flag = true;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return flag;
    }
    
    public static boolean CreateDirectory(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return true;
    }
    
    public static void saveAsFileOutputStream(String physicalPath, String content) {
        File file = new File(physicalPath);
        boolean b = file.getParentFile().isDirectory();
        if (!b) {
            File tem = new File(file.getParent());
            // tem.getParentFile().setWritable(true);
            tem.mkdirs();// 创建目录
        }
        // Log.info(file.getParent()+";"+file.getParentFile().isDirectory());
        FileOutputStream foutput = null;
        try {
            foutput = new FileOutputStream(physicalPath);
            
            foutput.write(content.getBytes("UTF-8"));
            // foutput.write(content.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                foutput.flush();
                foutput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        // Log.info("文件保存成功:"+ physicalPath);
    }
    
    /**
     * COPY文件
     *
     * @param srcFile String
     * @param desFile String
     * @return boolean
     */
    public static boolean copyToFile(String srcFile, String desFile) {
        File scrfile = new File(srcFile);
        if (scrfile.isFile() == true) {
            int length;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(scrfile);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            File desfile = new File(desFile);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(desfile, false);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            desfile = null;
            length = (int) scrfile.length();
            byte[] b = new byte[length];
            try {
                fis.read(b);
                fis.close();
                fos.write(b);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            scrfile = null;
            return false;
        }
        scrfile = null;
        return true;
    }

    /**
     * 拷贝文件到ftp服务器
     *
     * @return
     */
    //public static boolean copyToFTPFile(String sourceFilePath, String targetFilePath, String targetFileName, FtpCli ftpCli) {
    //    try {
    //        if (ftpCli != null) {
    //            // 读取源文件
    //            if (FileUtils.checkExist(sourceFilePath)) {
    //                try (FileInputStream fis = new FileInputStream(sourceFilePath)) {
    //                    if (!ftpCli.isConnected()) {
    //                        ftpCli.connect();
    //                    }
    //                    String result = ftpCli.uploadFileToFilePath(targetFilePath, targetFileName, fis);
    //                    if (StringUtils.isNotBlank(result)) {
    //                        return true;
    //                    }
    //
    //                } catch (FileNotFoundException ex) {
    //                }
    //            }
    //        }
    //    } catch (Exception e) {
    //    }
    //    return false;
    //}

    /**
     * 传入路径 分割出带后缀的文件名
     * @param path
     * @param separator
     * @return
     */
    public static String getFileNameFromPath(String path, String separator) {
        return StringUtils.substringAfterLast(path, separator);
    }

    public static void main(String[] args) {
        // FileUtils.copyToFTPFile("C://1.mp3", "/wyz/", "1.mp3", FtpCli.createFtpCli("172.16.110.2", "lianxinftp", "qingdao1234"));

        // System.out.println(FileUtils.getFileNameFromPath("/cs_record/lianxin/recordings/100001/20201124/20201124154751_Q_100001_1005_15275429358.mp3", "/"));

        System.out.println(FileUtils.getSavePath("uploadPath", "dto.getModuleName()", "nowStr", " uuid" + "." + "insert.getExtension()"));
    }
    
    /**
     * COPY文件夹
     *
     * @param sourceDir String
     * @param destDir   String
     * @return boolean
     */
    public static boolean copyDir(String sourceDir, String destDir) {
        File sourceFile = new File(sourceDir);
        String tempSource;
        String tempDest;
        String fileName;
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            fileName = files[i].getName();
            tempSource = sourceDir + "/" + fileName;
            tempDest = destDir + "/" + fileName;
            if (files[i].isFile()) {
                copyToFile(tempSource, tempDest);
            } else {
                copyDir(tempSource, tempDest);
            }
        }
        sourceFile = null;
        return true;
    }
    
    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dir 要删除的目录
     * @return 删除成功时返回true，否则返回false。
     */
    public static boolean deleteDirectory(File dir) {
        File[] entries = dir.listFiles();
        if (entries == null) {
            return true;
        }
        int sz = entries.length;
        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                if (!deleteDirectory(entries[i])) {
                    return false;
                }
            } else {
                if (!entries[i].delete()) {
                    return false;
                }
            }
        }
        if (!dir.delete()) {
            return false;
        }
        return true;
    }
    
    /**
     * File exist check
     *
     * @param sFileName File Name
     * @return boolean true - exist<br>
     * false - not exist
     */
    public static boolean checkExist(String sFileName) {
        
        boolean result = false;
        
        try {
            File f = new File(sFileName);
            
            // if (f.exists() && f.isFile() && f.canRead()) {
            if (f.exists() && f.isFile()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        
        /* return */
        return result;
    }
    
    /**
     * Get File Size
     *
     * @param sFileName File Name
     * @return long File's size(byte) when File not exist return -1
     */
    public static long getSize(String sFileName) {
        
        long lSize = 0;
        
        try {
            File f = new File(sFileName);
            
            // exist
            if (f.exists()) {
                if (f.isFile() && f.canRead()) {
                    lSize = f.length();
                } else {
                    lSize = -1;
                }
                // not exist
            } else {
                lSize = 0;
            }
        } catch (Exception e) {
            lSize = -1;
        }
        /* return */
        return lSize;
    }
    
    /**
     * File Delete
     *
     * @param sFileName File Nmae
     * @return boolean true - Delete Success<br>
     * false - Delete Fail
     */
    public static boolean deleteFromName(String sFileName) {
        
        boolean bReturn = true;
        
        try {
            File oFile = new File(sFileName);
            
            // exist
            if (oFile.exists() && oFile.isFile()) {
                // Delete File
                boolean bResult = oFile.delete();
                // Delete Fail
                if (bResult == false) {
                    bReturn = false;
                }
                
                // not exist
            } else {
            
            }
            
        } catch (Exception e) {
            bReturn = false;
        }
        
        // return
        return bReturn;
    }

    /**
     * getRealFileName
     *
     * @param baseDir     Root Directory
     * @param absFileName absolute Directory File Name
     * @return java.io.File Return file
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static File getRealFileName(String baseDir, String absFileName) throws Exception {
        
        File ret = null;
        List dirs = new ArrayList();
        StringTokenizer st = new StringTokenizer(absFileName, System.getProperty("file.separator"));
        while (st.hasMoreTokens()) {
            dirs.add(st.nextToken());
        }
        ret = new File(baseDir);
        if (dirs.size() > 1) {
            for (int i = 0; i < dirs.size() - 1; i++) {
                ret = new File(ret, (String) dirs.get(i));
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, (String) dirs.get(dirs.size() - 1));
        return ret;
    }
    
    /**
     * copyFile
     *
     * @param srcFile    Source File
     * @param targetFile Target file
     */
    @SuppressWarnings("resource")
    static public void copyFile(String srcFile, String targetFile) throws IOException {
        
        FileInputStream reader = null;// new FileInputStream(srcFile);
        FileOutputStream writer = null;// new FileOutputStream(targetFile);
        byte[] buffer = new byte[4096];
        int len;
        try {
            reader = new FileInputStream(srcFile);
            writer = new FileOutputStream(targetFile);
            
            while ((len = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (writer != null)
                writer.close();
            if (reader != null)
                reader.close();
        }
    }
    
    /**
     * renameFile
     *
     * @param srcFile    Source File
     * @param targetFile Target file
     */
    static public void renameFile(String srcFile, String targetFile) throws IOException {
        try {
            copyFile(srcFile, targetFile);
            deleteFromName(srcFile);
        } catch (IOException e) {
            throw e;
        }
    }
    
    public static void write(String tivoliMsg, String logFileName, boolean append) {
        try (FileOutputStream fOut = new FileOutputStream(logFileName, append);) {
            byte[] bMsg = tivoliMsg.getBytes();
            //fOut.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
            fOut.write(bMsg);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is used to log the messages with timestamp,error code and the
     * method details
     *
     * @param logFile       String
     * @param batchId       String
     * @param exceptionInfo String
     */
    public static void writeLog(String logFile, String batchId, String exceptionInfo) {
        
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.JAPANESE);
        
        Object args[] = { df.format(new Date()), batchId, exceptionInfo };
        
        String fmtMsg = MessageFormat.format("{0} : {1} : {2}", args);
        
        try {
            
            File logfile = new File(logFile);
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(fmtMsg);
            fw.write(System.getProperty("line.separator"));
            fw.flush();
            fw.close();
        } catch (Exception e) {
        }
    }
    
    public static String readTextFile(String realPath) throws Exception {
        File file = new File(realPath);
        if (!file.exists()) {
            System.out.println("File not exist!");
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(realPath), "UTF-8"));
        String temp = "";
        String txt = "";
        while ((temp = br.readLine()) != null) {
            txt += temp;
        }
        br.close();
        return txt;
    }
    
    /**
     * <b>function:</b> 传入一个文件名，得到这个文件名称的后缀
     *
     * @param fileName 文件名
     * @return 后缀名
     * @author hoojo
     * @createDate Oct 9, 2010 11:30:46 PM
     */
    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index + 1);// 后缀
            return suffix;
        } else {
            return null;
        }
    }
    
    /**
     * <b>function:</b> 传入一个文件名，得到这个文件名称的后缀
     *
     * @param fileName 文件名
     * @return 后缀名
     * @author hoojo
     * @createDate Oct 9, 2010 11:30:46 PM
     */
    public static String getDotSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index);// 后缀
            return suffix;
        } else {
            return null;
        }
    }
    
    /**
     * 获取文件名  不带后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileNameWithOutSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(0, index);
            return suffix;
        } else {
            return null;
        }
    }
    
    /**
     * 得到Excel表中的值
     *
     * @param xssfCell Excel中的每一个格子
     * @return xcel中每一个格子中的值
     */
    public static String getExcelCellValue(Cell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {
            String cellValue = "";
            if (HSSFDateUtil.isCellDateFormatted(xssfCell)) { // 判断是日期类型
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                Date dt = HSSFDateUtil.getJavaDate(xssfCell.getNumericCellValue());// 获取成DATE类型
                cellValue = dateformat.format(dt);
            } else {
                HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
                String cellFormatted = dataFormatter.formatCellValue(xssfCell);
                return cellFormatted;
                // xssfCell.setCellType(CellType.STRING);
                // return String.valueOf(xssfCell.getStringCellValue());
            }
            return cellValue;
        } else if (xssfCell.getCellTypeEnum() == CellType.STRING) {
            // 返回字符串类型的值
            return String.valueOf(xssfCell.getStringCellValue());
        } else {
            // 返回字符串类型的值
            xssfCell.setCellType(CellType.STRING);
            return String.valueOf(xssfCell.getStringCellValue());
        }
    }
    
    /**
     * 下载文件
     *
     * @param file
     * @param response
     * @param isDelete
     */
    public static void downloadFile(HttpServletRequest request, File file, HttpServletResponse response, boolean isDelete) {
        downloadFile(request, file, file.getName(), response, isDelete);
    }
    
    /**
     * 下载文件
     *
     * @param file
     * @param fileName
     * @param response
     * @param isDelete
     */
    public static void downloadFile(HttpServletRequest request, File file, String fileName, HttpServletResponse response, boolean isDelete) {
        // 清空response
        response.reset();
        try (InputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream();) {
            
            // String fileName = file.getName();
            response.reset();
            // response.setHeader("content-disposition", "attachment;filename="
            // + new String((fileName).getBytes("UTF-8"), "ISO8859-1"));
            String userAgent = request.getHeader("user-agent");
            if (userAgent != null && userAgent.indexOf("Edge") >= 0) {
                fileName = URLEncoder.encode(fileName, "UTF8");
            } else if (userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0 || userAgent.indexOf("Safari") >= 0) {
                fileName = new String((fileName).getBytes("UTF-8"), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "UTF8"); // 其他浏览器
            }
            
            // 以流的形式下载文件。
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            
            // 写文件
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            
            if (isDelete) {
                file.delete(); // 是否将生成的服务器端文件删除
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 断点续传文件
     *
     * @param response
     * @param request
     * @param location
     * @return
     */
    public static boolean downFile(HttpServletResponse response, HttpServletRequest request, String location) {
        BufferedInputStream bis = null;
        try {
            File file = new File(location);
            if (file.exists()) {
                long p = 0L;
                long toLength = 0L;
                long contentLength = 0L;
                int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
                long fileLength;
                String rangBytes = "";
                fileLength = file.length();
                // get file content
                InputStream ins = new FileInputStream(file);
                bis = new BufferedInputStream(ins);
                
                // tell the client to allow accept-ranges
                response.reset();
                response.setHeader("Accept-Ranges", "bytes");
                
                // client requests a file block download start byte
                String range = request.getHeader("Range");
                if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    rangBytes = range.replaceAll("bytes=", "");
                    if (rangBytes.endsWith("-")) { // bytes=270000-
                        rangeSwitch = 1;
                        p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                        contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    } else { // bytes=270000-320000
                        rangeSwitch = 2;
                        String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
                        String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
                        p = Long.parseLong(temp1);
                        toLength = Long.parseLong(temp2);
                        contentLength = toLength - p + 1; // 客户端请求的是
                        // 270000-320000
                        // 之间的字节
                    }
                } else {
                    contentLength = fileLength;
                }
                
                // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
                // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
                response.setHeader("Content-Length", new Long(contentLength).toString());
                
                // 断点开始
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                if (rangeSwitch == 1) {
                    String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-").append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else if (rangeSwitch == 2) {
                    String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else {
                    String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/").append(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                }
                
                String fileName = file.getName();
                response.reset();
                // response.setHeader("content-disposition",
                // "attachment;filename=" + new
                // String((fileName).getBytes("UTF-8"), "ISO8859-1"));
                String userAgent = request.getHeader("user-agent");
                if (userAgent != null && userAgent.indexOf("Edge") >= 0) {
                    fileName = URLEncoder.encode(fileName, "UTF8");
                } else if (userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0 || userAgent.indexOf("Safari") >= 0) {
                    fileName = new String((fileName).getBytes("UTF-8"), "ISO8859-1");
                } else {
                    fileName = URLEncoder.encode(fileName, "UTF8"); // 其他浏览器
                }
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                
                OutputStream out = response.getOutputStream();
                int n = 0;
                long readLength = 0;
                int bsize = 1024;
                byte[] bytes = new byte[bsize];
                if (rangeSwitch == 2) {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    while (readLength <= contentLength - bsize) {
                        n = bis.read(bytes);
                        readLength += n;
                        out.write(bytes, 0, n);
                    }
                    if (readLength <= contentLength) {
                        n = bis.read(bytes, 0, (int) (contentLength - readLength));
                        out.write(bytes, 0, n);
                    }
                } else {
                    while ((n = bis.read(bytes)) != -1) {
                        out.write(bytes, 0, n);
                    }
                }
                out.flush();
                out.close();
                bis.close();
            } else {
                throw new Exception("file not found");
            }
        } catch (IOException ie) {
            // 忽略 ClientAbortException 之类的异常
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * 获取文件配置
     *
     * @param filePath
     * @return
     */
    
    public static Configuration getDirectoryConfiguation(String filePath) {
        Configuration config = new Configuration();
        try {
            config.setDirectoryForTemplateLoading(new File(filePath));
            config.setEncoding(Locale.CHINA, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
    
    /**
     * 生成文件
     *
     * @param ftlPath
     * @param ftlName
     * @param variables
     * @return
     * @throws Exception
     */
    public static String generate(String ftlPath, String ftlName, Map<String, Object> variables) throws Exception {
        Configuration config = getDirectoryConfiguation(ftlPath);
        Template tp = config.getTemplate(ftlName);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);
        tp.setEncoding("UTF-8");
        tp.process(variables, writer);
        String htmlStr = stringWriter.toString();
        writer.flush();
        writer.close();
        return htmlStr;
    }

    public static void doCompressDir(File dirFile, ZipOutputStream out, String basePath) throws IOException {
        
        // 如果文件是文件夹
        if (dirFile.isDirectory()) {
            ZipEntry entry = new ZipEntry(basePath + File.separator);
            out.putNextEntry(entry);
            basePath = basePath.length() == 0 ? "" : basePath + File.separator;
            File[] files = dirFile.listFiles();
            for (File file : files) {
                doCompressDir(file, out, basePath + file.getName());
            }
        } else {
            doCompress(dirFile, out, basePath);
        }
    }
    
    /**
     * 压缩单个文件
     *
     * @param file
     * @param out
     * @throws IOException
     */
    public static void doCompress(File file, ZipOutputStream out, String basePath) throws IOException {
        
        // 压缩文件
        if (basePath.length() > 0) {
            out.putNextEntry(new ZipEntry(basePath));
        } else {
            out.putNextEntry(new ZipEntry(file.getName()));
        }
        int BUFFER_SIZE = 1024;
        byte buff[] = new byte[BUFFER_SIZE];
        FileInputStream fis = new FileInputStream(file);
        int len = 0;
        // 读取文件的内容, 打包到zip文件
        while ((len = fis.read(buff)) > 0) {
            out.write(buff, 0, len);
            out.flush();
        }
        fis.close();
    }
    
    public static byte[] toByteArray(String filename) throws IOException {
        
        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }
    
    //统计目录大小的方法
    public static long getSize(File file) {
        if (file == null)
            return 0;
        long size = 0;
        if (file.isFile()) {
            //如果是文件，获取文件大小累加
            size += file.length();
        } else if (file.isDirectory()) {
            //获取目录中的文件及子目录信息
            File[] f1 = file.listFiles();
            for (int i = 0; i < f1.length; i++) {
                //调用递归遍历f1数组中的每一个对象
                size += getSize(f1[i]);
            }
        }
        return size;
    }
    
    /**
     * 生成数据
     *
     * @param sheet
     * @param keyList
     * @return
     */
    public static List<Map<String, Object>> setEntity(Sheet sheet, List<String> keyList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            Map<String, Object> tmpMap = new HashMap<>();
            String cellVal;
            for (int i = 0; i < keyList.size(); i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cellVal = getExcelCellValue(cell);
                    if (StringUtils.isNotBlank(cellVal)) {
                        tmpMap.put(keyList.get(i), cellVal);
                    }
                }
            }
            tmpMap.put("rowNum", rowNum + 1);
            mapList.add(tmpMap);
        }
        return mapList;
    }


    /**
     * 下载文件
     *
     * @param file
     * @param response
     */
    public static void downloadFile2(HttpServletRequest request, File file, HttpServletResponse response) {
        // 清空response
        response.reset();
        try (InputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream();) {
            String fileName = file.getName();
            String userAgent = request.getHeader("user-agent");
            if (userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0
                    || userAgent.indexOf("Safari") >= 0) {
                fileName = new String((fileName).getBytes("UTF-8"), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "UTF8"); // 其他浏览器
            }

            // 以流的形式下载文件。
            response.setContentType(fileName.endsWith("wav") ? "audio/wav" : "audio/mp3");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            // 没有这个header，audio没有总时长
            response.setHeader("Content-Length", file.length() + "");
            // range 值
            // audio / video 标签拖动时发送的请求, 第一次：bytes=0-， 后续：bytes=0-xxx
//			String rangeString = request.getHeader("Range");
//			long rangeStart = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
//			long rangeEnd = file.length();
//			if (rangeString.indexOf("-") < rangeString.length() - 1) {
//				rangeEnd = Long.parseLong(rangeString.substring(rangeString.indexOf("-") + 1));
//			}
//			// range范围  start-end/total
//			response.setHeader("Content-Range", rangeStart + "-" + rangeEnd + "/" + file.length());
            // 告知video / audio标签可以接受范围单位
            response.setHeader("Accept-Ranges", "bytes");
            // 写文件
            int b;
//			in.skip(rangeStart);
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 根据带后缀文件名获取文件名
     * @param originalFilename
     * @return
     */
    public static String getFileNameFromOriginalFilename(String originalFilename) {

        return StringUtils.isBlank(originalFilename) ? StringUtils.EMPTY : StringUtils.substringBeforeLast(originalFilename, StringPool.PERIOD);
    }

    /**
     * 获取文件名后缀
     *
     * @param originalFilename
     * @return
     */
    public static String getFileExtensionFromOriginalName(String originalFilename) {
        return StringUtils.isBlank(originalFilename) ? StringUtils.EMPTY : StringUtils.substringAfterLast(originalFilename, StringPool.PERIOD);
    }

    /**
     * 获取完整的保存路径，并去掉最后一个斜杠
     * 文件路径+完整文件名
     *
     * @param savePath
     * @return
     */
    public static String getSavePath(String... savePath) {
        if (ArrayUtils.isEmpty(savePath)) {
            throw new IllegalArgumentException("参数异常");
        }
        StringBuilder sb = new StringBuilder();
        for (String partPath : savePath) {
            if (StringUtils.isNotBlank(partPath)) {
                sb.append(File.separator).append(partPath);
            }
        }
        return StringUtils.removeEnd(sb.toString(), File.separator);
    }
}
