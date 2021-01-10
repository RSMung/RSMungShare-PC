package bean;

import java.util.ArrayList;

public class MsgCollector {
    private static ArrayList<MsgBean> list = new ArrayList<>();
    public static int size(){
        return list.size();
    }
    public static void addMsg(MsgBean msgBean){
        list.add(msgBean);
    }
    public static MsgBean getMsg(int index){
        return list.get(index);
    }
    public static ArrayList<MsgBean> getList(){
        return list;
    }
}
