package self.dy.tool.window;

public class DyExecption extends Exception {
    public String getDesc() {
        return desc;
    }

    private String desc;
    public DyExecption(String desc){
        super();
        this.desc = desc;
    }
}
