package self.dy.tool.window;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoxActionListener implements ActionListener {

    private DyJComboBox dyJComboBox;
    public BoxActionListener(DyJComboBox dyJComboBox){
        this.dyJComboBox = dyJComboBox;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String selectValue = (String)this.dyJComboBox.getSelectedItem();
        this.dyJComboBox.getDyJTextField().setText(selectValue);
        this.dyJComboBox.getDyJTextField().setForeground(Color.BLACK);
    }
}