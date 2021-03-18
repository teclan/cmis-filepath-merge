package cmis.teclan.desktop;

import cmis.teclan.constant.Constant;
import cmis.teclan.utils.*;
import com.teclan.ssh.DefaultLinehandler;
import com.teclan.ssh.SSHClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class WorkSpace {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkSpace.class);
    public static final JTextField JT_LOCAL_FILE_PATH = new JTextField();
    public static final List<String> SUFFIX = new ArrayList<String>();
    public static final JTextArea LOG = new JTextArea();

    static {
        SUFFIX.add("txt");
        SUFFIX.add("dat");
    }

    public static void load(){
        final JFrame workSpace = new JFrame();
        workSpace.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        workSpace.setUndecorated(true);
        workSpace.setPreferredSize(new Dimension(1200, 800));
        workSpace.setVisible(true);

        workSpace.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                String  logFile = "logs/操作日志_"+sdf.format(new Date())+".log";
//               FileUtils.randomWrite2File(logFile,LOG.getText());
            }
        });

        JPanel info = new JPanel();
        info.setLayout(new BorderLayout(20, 10));

        JLabel jlLocalPath = new JLabel("路径清单目录:");
        jlLocalPath.setFont(Constant.FONT_SIZE_20);
        JT_LOCAL_FILE_PATH.setBorder(Constant.BORDER);
        JT_LOCAL_FILE_PATH.setPreferredSize(new Dimension(330, 30));
        JT_LOCAL_FILE_PATH.setEditable(false);
        JT_LOCAL_FILE_PATH.setFont(Constant.FONT);
        JT_LOCAL_FILE_PATH.setScrollOffset(5);
        JButton chooser = new JButton("选择");
        chooser.setFont(Constant.FONT_SIZE_20);
        JButton merge = new JButton("合并所有文本");
        merge.setFont(Constant.FONT_SIZE_20);

        JButton extractCmisIncrement  = new JButton("提取上线CMIS增量包");
        extractCmisIncrement.setFont(Constant.FONT_SIZE_20);

        JPanel filePathInfo = new JPanel();
        filePathInfo.add(BorderLayout.WEST, jlLocalPath);
        filePathInfo.add(BorderLayout.CENTER, JT_LOCAL_FILE_PATH);
        filePathInfo.add(BorderLayout.EAST, chooser);
        filePathInfo.add(BorderLayout.EAST, merge);
        filePathInfo.add(BorderLayout.EAST, extractCmisIncrement);


        info.add(BorderLayout.WEST, filePathInfo);
        workSpace.add(BorderLayout.NORTH, info);

        final JTable localTable = FileUtils.fileInfoTableInit();
        JScrollPane localFileTable = new JScrollPane(localTable);

        LOG.setFont(Constant.FONT);
        LOG.setPreferredSize(new Dimension(600, 800));
        LOG.setForeground(Color.RED);
        localFileTable.setPreferredSize(new Dimension(600, 800));
        Box hBox01 = Box.createHorizontalBox();
        hBox01.add(localFileTable);
        hBox01.add(LOG);

        Box vBox = Box.createVerticalBox();
        vBox.setSize(1200,800);
        vBox.add(hBox01);
        workSpace.add(BorderLayout.CENTER, vBox);

        MoveLabel notice = new MoveLabel("合并时会遍历子目录下的文本文件，且仅支持的文件扩展名为： txt,log!!");
        notice.setFont(Constant.FONT);
        notice.setForeground(Color.RED);

        workSpace.add(BorderLayout.SOUTH, notice);
        workSpace.pack();
        workSpace.setLocationRelativeTo(null);//在屏幕中居中显示

        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {  //按钮点击事件
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);
                chooser.setOpaque(false);
                chooser.setDialogTitle("选择本地文件...");
                chooser.setFont(new Font("", Font.PLAIN, 20));
                int returnVal = chooser.showOpenDialog(chooser);        //是否打开文件选择框

                if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型
                    String absolutePath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
                    LOG.append("打开目录："+absolutePath+"\n");
                    LOGGER.info("{}","打开目录："+absolutePath+"\n");
                    JT_LOCAL_FILE_PATH.setText(absolutePath);
                    FileUtils.flusFileListByPath(localTable,absolutePath);
                    workSpace.setLocationRelativeTo(null);
                    workSpace.pack();
                }
            }
        });

        merge.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String loaclFilePath = JT_LOCAL_FILE_PATH.getText();
                    Set<String> filePaths = FileUtils.getFilePathList(new File(loaclFilePath));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    File merge = new File(loaclFilePath+File.separator+sdf.format(new Date())+"_合并结果.txt");
                    merge.delete();
                    LOG.append(String.format("合并前删除文件：%s\n",merge.getAbsolutePath()));
                    LOGGER.info("{}",String.format("合并前删除文件：%s\n",merge.getAbsolutePath()));

                    FileLineHandler fileLineHandler = new DefaultFileLineHandler();
                    fileLineHandler.clean();

                    for(String filePath:filePaths){
                        File file = new File(filePath);
                        String suffix = filePath.substring(filePath.lastIndexOf(".")+1);
                        if(SUFFIX.contains(suffix.toLowerCase())){
                            String content = FileUtils.getContent(file);
                            if(Assert.assertNullString(content)){
                                LOG.append(String.format("文件内容为空：%s\n",filePath));
                                LOGGER.info(String.format("文件内容为空：%s\n",filePath));
                                continue;
                            }else {
                                FileUtils.getContent(file,fileLineHandler);
                            }

                        }else {
                            LOG.append(String.format("文件扩展名不符合规则：%s\n",filePath));
                            LOGGER.info(String.format("文件扩展名不符合规则：%s\n",filePath));
                        }
                    }

                     FileUtils.randomWrite2File(merge.getAbsolutePath(),fileLineHandler.get());

                    LOG.append("刷新目录："+loaclFilePath+"\n");
                    LOGGER.info(String.format("刷新目录："+loaclFilePath+"\n"));
                    FileUtils.flusFileListByPath(localTable,loaclFilePath);
                    LOG.append(String.format("合并完成，输出文件：%s\n",merge.getAbsolutePath()));
                }catch (Exception e){
                    DialogUtils.showError(e.getMessage());
                    LOGGER.error(e.getMessage(),e);
                }
            }
        });

        extractCmisIncrement.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                int selected = JOptionPane.showConfirmDialog(null, "即将提取CMIS增量包，请确认是否继续？","警告",0);
                if (JOptionPane.OK_OPTION == selected) {
                    LOG.append("即将提取CMIS增量包，请确认是否继续？： 是 \n");
                    SSHClient sshClient = new SSHClient("200.100.154.39","root","123456");
                    sshClient.setJschLogOpen(true); // 启用Jsch日志，打印 ssh 连接信息等
                    sshClient.setLinehandler(new DefaultLinehandler()); // 设置服务返回的每一行的处理类
                    sshClient.setTimeout(3000);// 设置命令最大的等待时间，例如； top -b 命令会持续输出
                    sshClient.login();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String dst = "上线版本/"+sdf.format(new Date())+"/CMIS增量包/";
                    try {
                        sshClient.download("/home/filePicker/target/",dst);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(),e);
                    }
                    sshClient.logout();
                }else{
                    LOG.append("即将提取CMIS增量包，请确认是否继续？： 否 \n");
                }
             }
        });

    }
}
