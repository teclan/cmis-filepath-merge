package cmis.teclan.desktop;

import cmis.teclan.constant.Constant;
import cmis.teclan.utils.Assert;
import cmis.teclan.utils.DialogUtils;
import cmis.teclan.utils.FileUtils;
import cmis.teclan.utils.MoveLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class WorkSpace {
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

        JPanel filePathInfo = new JPanel();
        filePathInfo.add(BorderLayout.WEST, jlLocalPath);
        filePathInfo.add(BorderLayout.CENTER, JT_LOCAL_FILE_PATH);
        filePathInfo.add(BorderLayout.EAST, chooser);
        filePathInfo.add(BorderLayout.EAST, merge);

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
                    String fileName = chooser.getSelectedFile().getName();
                    LOG.append("打开目录："+absolutePath+"\n");
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

                    for(String filePath:filePaths){
                        File file = new File(filePath);
                        String suffix = filePath.substring(filePath.lastIndexOf(".")+1);
                        if(SUFFIX.contains(suffix.toLowerCase())){
                            String content = FileUtils.getContent(file);

                            if(Assert.assertNullString(content)){
                                LOG.append(String.format("文件内容为空：%s\n",filePath));
                            }else {
                                FileUtils.randomWrite2File(merge.getAbsolutePath(),content);
                            }

                        }else {
                            LOG.append(String.format("文件扩展名不符合规则：%s\n",filePath));
                        }
                    }
                    LOG.append(String.format("合并完成，输出文件：%s\n",merge.getAbsolutePath()));
                }catch (Exception e){
                    DialogUtils.showError(e.getMessage());
                }
            }
        });
    }
}
