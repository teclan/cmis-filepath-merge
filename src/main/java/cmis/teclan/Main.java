package cmis.teclan;

import cmis.teclan.desktop.WorkSpace;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
            WorkSpace.load();
        } catch (Exception e) {
           e.fillInStackTrace();
        }
    }
}
