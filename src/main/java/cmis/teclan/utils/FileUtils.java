package cmis.teclan.utils;


import cmis.teclan.desktop.WorkSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private static final DecimalFormat DF = new DecimalFormat("######0.00");

    public static File[] getFileList(String filePath) {
        return getFileList(new File(filePath));
    }

    public static File[] getFileList(File file) {
        File[] files = null;

        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files = new File[1];
            files[0] = file;
        }
        return files;
    }


    public static JTable fileInfoTableInit() {

        Vector vData = new Vector();
        Vector vName = new Vector();
        vName.add("文件名");
        vName.add("文件类型");
        vName.add("文件大小");
        vName.add("修改时间");


        Vector vRow = new Vector();
        vRow.add("..");
        vRow.add("上级目录");
        vRow.add("");
        vRow.add("");
        vData.add(vRow);

        DefaultTableModel model = new DefaultTableModel(vData, vName);
        model.isCellEditable(-1,-1);
        final JTable table = new JTable(){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        table.setFont(new Font("",Font.PLAIN,14));
        table.setModel(model);

        table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getButton()==MouseEvent.BUTTON1 && mouseEvent.getClickCount()==2){
                    int focusedRowIndex = table.rowAtPoint(mouseEvent.getPoint());
                    if (focusedRowIndex == -1) {
                        return;
                    }
                    table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
                    int[] selectRowIdxs = table.getSelectedRows();
                    for (int index : selectRowIdxs) {
                        String fileName = (String) table.getValueAt(index, 0);
                        String local = WorkSpace.JT_LOCAL_FILE_PATH.getText();
                        if("".equals(local)){
                            return;
                        }

                        if("..".equals(fileName) && !"".equals(WorkSpace.JT_LOCAL_FILE_PATH.getText()) && new File(local).getParentFile()!= null){
                            local = new File(local).getParentFile().getAbsolutePath();
                            WorkSpace.JT_LOCAL_FILE_PATH.setText(afterFormatFilePath(local));
                            flusFileListByPath(table,local);
                            WorkSpace.LOG.append("打开目录："+afterFormatFilePath(local)+"\n");
                        }else {
                            local = local+"/"+fileName;

                            File file = new File(local);
                            if(!file.exists()){
                                return;
                            }
                            if(file.isDirectory()){
                                flusFileListByPath(table,local);
                                WorkSpace.JT_LOCAL_FILE_PATH.setText(afterFormatFilePath(local));
                                WorkSpace.LOG.append("打开目录："+afterFormatFilePath(local)+"\n");
                                LOGGER.info("{}","打开目录："+afterFormatFilePath(local)+"\n");
                            }else {
                                WorkSpace.LOG.append("浏览文件："+afterFormatFilePath(local)+"\n");
                                LOGGER.info("{}","浏览文件："+afterFormatFilePath(local)+"\n");
                                open(file);
                            }
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent mouseEvent) {

            }

            public void mouseReleased(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {

            }

            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        return table;
    }


    public static void flusFileListByPath(JTable jTable,String filePath) {

        Vector vData = new Vector();
        Vector vName = new Vector();
        vName.add("文件名");
        vName.add("文件类型");
        vName.add("文件大小");
        vName.add("修改时间");

        File[] files = getFileList(filePath);

        Vector vRow = new Vector();
        vRow.add("..");
        vRow.add("上级目录");
        vRow.add("");
        vData.add(vRow);

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            vRow = new Vector();
            vRow.add(file.getName());
            vRow.add(file.isDirectory()?"文件夹":getSuffix(file));
            vRow.add(getFileSize(file));
            vRow.add(DateUtils.getDataString(file.lastModified()));
            vData.add(vRow);
        }
        DefaultTableModel model = new DefaultTableModel(vData, vName);
        jTable.setModel(model);
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    }

    public static String getSuffix(File file) {
        String fileName = file.getName();
        String suffix = "未知";
        if (fileName.lastIndexOf(".") > 0) {
            suffix = fileName.substring(fileName.lastIndexOf("."));
        }
        return suffix;
    }




    private static String getFileSize(File file) {
        String size = "";
        double length = file.length();

        length = length * 1.0 / 1024; // KB
        size = DF.format(length) + "KB";

        if (length > 1024) {
            length = length / 1024; // M
            size = DF.format(length) + "MB";
        }

        if (length > 1024) {
            length = length / 1024; // G
            size = DF.format(length) + "GB";
        }
        return size;
    }

    public static Set<String> getFileLis(File file){
        Set<String> abps = new HashSet<String>();
        if (!file.exists()){
            return abps;
        }

        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f:files){
                abps.addAll(getFileLis(f));
            }
        }
        abps.add(file.getAbsolutePath());
        return abps;
    }


    public static Set<String> getFileLis(String prefix,File file){

        Set<String> abps = new HashSet<String>();

        if (!file.exists()){
            return abps;
        }

        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f:files){
                abps.addAll(getFileLis(prefix,f));
            }
        }
        abps.add(file.getAbsolutePath().replace(prefix,"").replace("\\","/"));

        return abps;
    }

    public static String afterFormatFilePath(String abp){

        while (abp.indexOf("\\")>=0){
            abp = abp.replace("\\","/");
        }

        while (abp.indexOf("//")>=0){
            abp = abp.replace("//","/");
        }
        return abp;
    }

    public static void open(File file){
        open(file.getAbsolutePath());
    }

    private static void open(String filePath){
        final Runtime runtime = Runtime.getRuntime();
        Process process = null;
        final String cmd = "rundll32 url.dll FileProtocolHandler file://"+filePath;//要打开的文件路径。
        try {
            process = runtime.exec(cmd);
        } catch (final Exception e) {
           e.fillInStackTrace();
        }
    }

    public static Set<String> getFilePathList(File file) throws Exception {
        Set<String> paths = new HashSet<String>();
        if(!file.exists()){
            throw new Exception("文件不存在："+file.getAbsolutePath());
        }
        if(file.isFile()){
            paths.add(file.getAbsolutePath());
        }else {

            File[] files = file.listFiles();
            for(File f:files){
                paths.addAll(getFilePathList(f));
            }
        }
        return paths;
    }

    public static String getContent(File file) {
        StringBuilder content = new StringBuilder();
        try {
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\r\n");
                }
                read.close();
            } else {
                throw new Exception("找不到指定的文件:{}"+ file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return null;
        }
        return content.toString();
    }

    public static void getContent(File file,FileLineHandler handler) {
        try {
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String line = null;
                int index=0;
                while ((line = bufferedReader.readLine()) != null) {
//                    content.append(line).append("\r\n");
                    handler.handle(++index,line);
                }
                read.close();
            } else {
                throw new Exception("找不到指定的文件:{}"+ file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return  ;
        }
    }

    public static void randomWrite2File(String fileName, String content) {
        RandomAccessFile randomFile = null;
        try {
            creatIfNeed(fileName);
            randomFile = new RandomAccessFile(fileName, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
//            randomFile.writeBytes(content);
            randomFile.write(content.getBytes("UTF-8"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        } finally {
            try {
                if (randomFile != null) {
                    randomFile.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }
    public static void creatIfNeed(String fileName) {
        try {
            File parentFile = new File(fileName).getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            new File(fileName).createNewFile();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }
}
