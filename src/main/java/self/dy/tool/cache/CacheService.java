package self.dy.tool.cache;

import self.dy.tool.threads.CreateThread;
import self.dy.tool.utils.ComponentUtils;
import self.dy.tool.window.DyJComboBox;
import self.dy.tool.window.WindowStart;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CacheService {

    /**
     * 缓存窗口中的文本组件内容，加入去重处理
     * @param paramCompMap
     */
    public static void addCache(Map<String, Component> paramCompMap) {
        Collection<Component> values = paramCompMap.values();
        List<Component> components = new ArrayList<>(values);
        Iterator<Component> iterator = components.iterator();
        while(iterator.hasNext()){
            Component component = iterator.next();
            if(component instanceof DyJComboBox){
                JTextField jTextField = ((DyJComboBox) component).getDyJTextField();
                if(!jTextField.getForeground().equals(Color.GRAY)){
                    String value = jTextField.getText();
                    int itemCount = ((DyJComboBox) component).getItemCount();
                    List<String> histValue = new ArrayList<>();
                    while(itemCount-->0){
                        String itemValue = (String) ((DyJComboBox) component).getItemAt(itemCount);
                        histValue.add(itemValue);
                    }
                    // 清理历史数据
                    ((DyJComboBox) component).removeAllItems();
                    List<String> newBox = new ArrayList<>();
                    // 最近使用的值排至最前
                    if(histValue.contains(value)){
                        // 若果是历史输入，则先删除，然后将位置置为第一
                        histValue.remove(value);
                    }
                    newBox.add(value);
                    newBox.addAll(histValue);
                    Iterator<String> iterator1 = newBox.iterator();
                    int histNum = 10;
                    while (histNum-->0){
                        if(iterator1.hasNext()){
                            String next = iterator1.next();
                            ((DyJComboBox) component).addItem(next);
                        }
                    }
                }
            }
        }
    }

}
