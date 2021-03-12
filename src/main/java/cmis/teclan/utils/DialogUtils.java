package cmis.teclan.utils;


import cmis.teclan.constant.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

public class DialogUtils {

    public static void showError(String message){
        show(message,JRootPane.ERROR_DIALOG,new WindowAdapter(){

        });
    }

    public static void showError(String message, WindowAdapter windowAdapter){
        show(message,JRootPane.ERROR_DIALOG,windowAdapter);
    }

    public static void showInfo(String message, WindowAdapter windowAdapter){
        show(message,JRootPane.INFORMATION_DIALOG ,windowAdapter);
    }

    public static void showWarn(String message, WindowAdapter windowAdapter){
        show(message,JRootPane.WARNING_DIALOG,windowAdapter);
    }

    public static void show(String message,int style, WindowAdapter windowAdapter){
        JFrame jFrame = new JFrame();
        jFrame.setUndecorated(true); // 去掉窗口的装饰
        jFrame.getRootPane().setWindowDecorationStyle(style);//采用指定的窗口装饰风格
        jFrame.setSize(500, 200);
        jFrame.setLocationRelativeTo(null);//在屏幕中居中显示
        jFrame.setResizable(false);
        Container container=jFrame.getContentPane();//将窗体转化为容器

        StringBuffer sb = new StringBuffer();
        int size =30;
        if(message.length()>size){
            int offset =0;
            for(int i=0;i<message.length()/size;i++){
                sb.append(message.substring(offset,offset+size)).append("\n");
                offset+=size;
            }
            sb.append(message.substring(offset,message.length()));
        }else {
            while (message.length()<size){
                message+=" ";
            }
            sb.append(message);
        }

        JTextArea jt = new JTextArea(sb.toString());
        jt.setEditable(false);
        jt.setFont(Constant.FONT_SIZE_20);
        jFrame.addWindowListener(windowAdapter);
        container.add(jt);
        jFrame.pack();
        jFrame.setVisible(true);//使窗体可见
    }

}
