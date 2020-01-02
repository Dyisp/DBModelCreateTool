package self.dy.tool.cache;

import self.dy.tool.generator.ConfigFactory;
import self.dy.tool.utils.ComponentUtils;
import self.dy.tool.window.BoxActionListener;
import self.dy.tool.window.DyJComboBox;
import self.dy.tool.window.DyJTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class WindowLisenerForCache implements WindowListener {

    private final String SPLIT_VALUE_FLAG = "\\^@\\^";
    private final String SPLIT_VALUE_FLAG_STRING = "^@^";
    private final String SPLIT_NAME_FLAG = "~";

    private JPanel paramPanle;
    private String cacheFilePath = ".\\conf\\cache";
    private Map<String, DyJComboBox> allCacheBox;

    public WindowLisenerForCache(JPanel paramPanle) {
        this.paramPanle = paramPanle;
    }

    public Map<String,DyJComboBox> getAllCacheBox(JPanel paramPanel) {
        Map<String, Component> allParamCompMap = new HashMap<>();
        ComponentUtils.listComponent(allParamCompMap,paramPanel);
        Collection<Component> values = allParamCompMap.values();
        Iterator<Component> iterator = values.iterator();
        Map<String,DyJComboBox> cacheBox = new HashMap<>();
        while (iterator.hasNext()){
            Component comp = iterator.next();
            if(comp instanceof DyJComboBox){
                cacheBox.put(comp.getName(),(DyJComboBox) comp);
            }
        }
        return cacheBox;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if(ConfigFactory.properties.getProperty("cachePath")!=null){
            cacheFilePath = ConfigFactory.properties.getProperty("cachePath");
        }
        allCacheBox = getAllCacheBox(this.paramPanle);
        try {
            RandomAccessFile cacheFile = new RandomAccessFile(cacheFilePath, "r");
            String cacheLine;
            while((cacheLine = cacheFile.readLine()).length()>0){
                // 编码转换，解决中文乱码  虽然不会出现这种场景
                cacheLine=new String(cacheLine.getBytes("8859_1"), "utf-8");
                String[] split = cacheLine.split(SPLIT_NAME_FLAG);
                if(split.length>1){
                    String boxName = split[0];
                    String cacheValues = split[1];
                    String[] cacheArray = cacheValues.split(SPLIT_VALUE_FLAG);
                    DyJComboBox dyJComboBox = allCacheBox.get(boxName);
                    if(dyJComboBox!=null){
                        int i= 0;
                        while (i<cacheArray.length){
                            dyJComboBox.addItem(cacheArray[i]);
                            i++;
                        }
                    }
                    dyJComboBox.addActionListener(new BoxActionListener(dyJComboBox));
                }
            };
        } catch (java.io.IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        FileChannel cacheFile = null;
        // 设置缓存区大小为10kb
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
        try {
            File file = new File(cacheFilePath);
            File parent = file.getParentFile();
            if(!parent.exists()){
                parent.mkdirs();
            }
            cacheFile = new RandomAccessFile(cacheFilePath, "rw").getChannel();
        } catch (FileNotFoundException e1) {
            System.out.println("缓存数据写入错误");
            return;
        }
        for(Map.Entry<String,DyJComboBox> boxEntry : allCacheBox.entrySet()){
            String boxName = boxEntry.getKey();
            StringBuilder cacheLine = new StringBuilder(boxName).append(SPLIT_NAME_FLAG);
            DyJComboBox cacheBox = boxEntry.getValue();
            int itemCount = cacheBox.getItemCount();
            int itemIndex = 0;
            while(itemIndex<itemCount){
                if(itemIndex!=0){
                    cacheLine.append(SPLIT_VALUE_FLAG_STRING);
                }
                String itemValue = (String)cacheBox.getItemAt(itemIndex);
                cacheLine.append(itemValue);
                itemIndex++;
            }
            cacheLine.append("\r\n");
            try {
                byteBuffer.put(cacheLine.toString().getBytes("utf-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                try {
                    cacheFile.write(byteBuffer);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            byteBuffer.clear();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
