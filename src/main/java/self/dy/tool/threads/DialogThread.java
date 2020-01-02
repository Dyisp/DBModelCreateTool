package self.dy.tool.threads;

import javax.swing.*;
import java.awt.*;

public class DialogThread extends Thread {
    // DBModel生成中...
    String massage;
    Component parentComponent;
    Thread thread;

    public DialogThread(Thread thread,Component parentComponent,String massage) {
       this.massage = massage;
       this.parentComponent = parentComponent;
       this.thread = thread;
    }

    @Override
    public void run() {

        JOptionPane.showMessageDialog(parentComponent,massage);
        if(null!=thread){
            thread.interrupt();
        }
    }
}
