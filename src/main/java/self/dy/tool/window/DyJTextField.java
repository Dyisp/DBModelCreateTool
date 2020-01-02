package self.dy.tool.window;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class DyJTextField extends JTextField {
    String noticeText;
    DyJTextField(final String text,String name,Dimension dimension){
        super(text);
        setName(name);
        noticeText = text;
        setForeground(Color.GRAY);
        setPreferredSize(dimension);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                Component source = (Component) e.getSource();
                if (source instanceof JTextComponent && Color.gray == source.getForeground()){
                    source.setForeground(Color.BLACK);
                    ((JTextComponent)source).setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                Component source = (Component) e.getSource();
                String text1 = ((JTextComponent) source).getText();
                if (source instanceof JTextComponent && "".compareTo (text1)==0){
                    source.setForeground(Color.GRAY);
                    ((JTextComponent)source).setText(noticeText);
                }
            }
        });
    }
}
