package ClientSide;

import java.util.LinkedList;

public class HistoryList {

    private int MaxSize ;
    private LinkedList<String> H=new LinkedList<String>();

    public HistoryList(int MaxSize){
        this.MaxSize=MaxSize;
    }

    /**
     * The method of adding the last command, if the size of the list exceeds the maximum, then the oldest is deleted
     * @param command
     */
    public void insert(String command){
        if(H.size()<this.MaxSize){
            H.add(command);
        } else{
            H.poll();
            H.add(command);
        }
    }

    /**
     *Method displaying the last 6 commands (without their arguments)
     */
    public void show(){
        System.out.println(H);
    }
}
