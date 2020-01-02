package self.dy.tool.window;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class DyJComboBox extends JComboBox {

    public DyJTextField getDyJTextField() {
        return jTextField;
    }

    private DyJTextField jTextField;
    DyJComboBox(DyJTextField jTextField, String name, Dimension dimension){
        this.jTextField = jTextField;
        setName(name);
        setPreferredSize(dimension);
    }

    DyJComboBox(String name, Dimension dimension){
        setName(name);
        setPreferredSize(dimension);
    }
}
