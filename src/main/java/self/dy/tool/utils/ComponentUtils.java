package self.dy.tool.utils;

import self.dy.tool.window.CompsName;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentUtils {
    /**
     * 递归处理：列出JPanel包含的所有组件（包括其子容器中的组件，不包括子容器）
     * @param compMap
     * @param component
     */
    public static void listComponent(Map<String, Component> compMap, Component component) {
        if(component instanceof JPanel){
            Component[] components = ((JPanel) component).getComponents();
            List<Component> componentList = Arrays.asList(components);
            for(Component componentTemp:componentList){
                listComponent(compMap, componentTemp);
            }
        }else{
            compMap.put(component.getName(),component);
        }
    }

    /**
     * 递归处理：列出JPanel包含的指定类型组件（包括其子容器中的组件，不包括子容器）
     * @param compMap
     * @param component
     */
    public static void listComponent(Map<String, Component> compMap, Component component,Class classType) {
        if(component instanceof JPanel){
            Component[] components = ((JPanel) component).getComponents();
            List<Component> componentList = Arrays.asList(components);
            for(Component componentTemp:componentList){
                listComponent(compMap, componentTemp,classType);
            }
        }else{
            if(classType==null||component.getClass().equals(classType)){
                compMap.put(component.getName(),component);
            }
        }
    }

    /**
     * 列出组件包含的所有组件（包括子容器），只向下列一层
     * @param component
     */
    public static Map<String, Component> listSonComp(Component component) {
        Map<String, Component> compMap = new HashMap<>();
        if(component instanceof JPanel || component instanceof JFrame){
            Component[] components = ((JPanel) component).getComponents();
            List<Component> componentList = Arrays.asList(components);
            for(Component componentTemp:componentList){
                compMap.put(componentTemp.getName(),componentTemp);
            }
        }
        return compMap;
    }

    /**
     * 提取窗口组件的值并放入Map
     * @param paramCompMap
     * @param filterEmpty 是否过滤空字符串
     * @param containCacheBox
     * @return
     */
    public static Map<String, String> getCompsValue(Map<String, Component> paramCompMap, boolean filterEmpty, boolean containCacheBox) {
        HashMap<String, String> valueMap = new HashMap<>();
        Set<Map.Entry<String, Component>> entries = paramCompMap.entrySet();
        Iterator<Map.Entry<String, Component>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Component> next = iterator.next();
            if (next.getKey() != null) {
                Component value = next.getValue();
                if (value instanceof JTextField) {
                    if (value.getForeground() != Color.GRAY) {
                        String text = ((JTextField) value).getText();
                        if (value.getName().compareToIgnoreCase(CompsName.DBModelRootClassFullPath) == 0) {
                            String[] split = text.split("\\.");
                            String rootClassName = split[split.length - 1];
                            valueMap.put(CompsName.DBModelRootClassName, rootClassName);
                            valueMap.put(value.getName(), text);
                        } else if (value.getName().compareToIgnoreCase(CompsName.DBDaoRootClassFullPath) == 0) {
                            String[] split = text.split("\\.");
                            String rootClassName = split[split.length - 1];
                            valueMap.put(CompsName.DBDaoRootClassName, rootClassName);
                            valueMap.put(value.getName(), text);
                        } else {
                            valueMap.put(value.getName(), text);
                        }
                    } else {
                        // 过滤空值
                        if (!filterEmpty) {
                            valueMap.put(value.getName(), "");
                        }
                    }
                } else if (value instanceof JRadioButton) {
                    if (value.getName() != null && ((JRadioButton) value).isSelected()) {
                        if (value.getName().compareToIgnoreCase("class") == 0) {
                            valueMap.put(CompsName.DaoClassType, "Y");
                        } else {
                            valueMap.put(CompsName.DaoClassType, "N");
                        }
                    }
                } else if (value instanceof JCheckBox) {
                    boolean selected = ((JCheckBox) value).isSelected();
                    if (value.getName() != null) {
                        if (selected) {
                            if (value.getName().compareToIgnoreCase(CompsName.SuppressAllComments) == 0) {
                                valueMap.put(value.getName(), "false");
                            } else if (value.getName().compareToIgnoreCase(CompsName.MakeGetSet) == 0) {
                                valueMap.put(value.getName(), "N");
                            }
                        } else {
                            if (value.getName().compareToIgnoreCase(CompsName.SuppressAllComments) == 0) {
                                valueMap.put(value.getName(), "true");
                            } else if (value.getName().compareToIgnoreCase(CompsName.MakeGetSet) == 0) {
                                valueMap.put(value.getName(), "Y");
                            }
                        }
                    }
                } else if (value instanceof JComboBox) {
                    if(!containCacheBox){
                        String name = value.getName();
                        if(name!=null&&name.endsWith(CompsName.CacheBox)){
                            continue;
                        }
                    }

                    String actionCommand = (String) ((JComboBox) value).getSelectedItem();
                    if(filterEmpty&&(actionCommand==""||null==actionCommand)){
                        continue;
                    }
                    valueMap.put(value.getName(), actionCommand);
                }
            }
        }
        return valueMap;
    }
}
