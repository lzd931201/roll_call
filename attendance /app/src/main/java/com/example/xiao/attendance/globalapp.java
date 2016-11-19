package com.example.xiao.attendance;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by xiao on 2016/5/18.
 */
public class globalapp extends Application {
    class student_state{
        private String stu_num;
        private int state;
        public String getStu_num(){
            return stu_num;
        }
        public int getState(){
            return state;
        }
        public void setStudent(String a,int b){
            stu_num=a;
            state=b;
        }

        @Override
        public boolean equals(Object other){
            if(other == null) return false;
            if(other == this) return true;
            if(!(other instanceof student_state)) return false;
            student_state otherStudent = (student_state)other;
            return otherStudent.stu_num.equals(this.stu_num);
        }
        @Override
        public int hashCode() {
            return stu_num.hashCode();
        }
    }
    private ArrayList<String> m_arraylist=new ArrayList<String>();
    private ArrayList<student_state>m_arraylist_state=new ArrayList<student_state>();
    private int someVariable;
    private String class_num;
    private int class_choose=-1;
    private boolean is_class_choose=false;
    private boolean first=true;
    private boolean first_AL=true;


    public student_state new_student_state(){
        return new student_state();
    }

    public void add_AL(String a){
        m_arraylist.add(a);
        first_AL=false;
    }

    public void set_AL(ArrayList<String> a){
        m_arraylist=a;
        first_AL=false;
    }

    public ArrayList<String> get_AL(){
        return m_arraylist;
    }

    public boolean remove_AL(String a){
        boolean x=false;
        if(m_arraylist.remove(a))
            x=true;
        return  x;
    }

    public boolean check_AL(){
        return first_AL;
    }

    public void setSomeVariable(int someVariable) {
        this.someVariable = someVariable;
        first=false;
    }

    public int getSomeVariable() {
        return someVariable;
    }

    public void setClass_num(String num){
        class_num=num;
    }

    public boolean getIs_class_choose(){return is_class_choose;}

    public void setIs_class_choose(boolean a){
        is_class_choose=a;
    }

    public int getClass_choose(){return class_choose;}

    public void setClass_choose(int choose){
        class_choose=choose;
    }

    public String getClass_num(){return class_num;}

    public boolean check(){
        return first;
    }

    public void add_ALS(student_state a){
        m_arraylist_state.add(a);
        first_AL=false;
    }

    public void set_ALS(ArrayList<student_state> a){
        m_arraylist_state=a;
        first_AL=false;
    }

    public ArrayList<student_state> get_ALS(){
        return m_arraylist_state;
    }

    public void remove_ALS(student_state a){
        m_arraylist_state.remove(a);
    }

    public void change_ALS(String a,int start,int end){
        globalapp.student_state tmp=new_student_state();
        tmp.setStudent(a,start);
        remove_ALS(tmp);
        tmp=new_student_state();
        tmp.setStudent(a,end);
        add_ALS(tmp);
    }//将start状态改为end状态

    public int find_state(String a){
        int x=-1;
        for(student_state tmp:m_arraylist_state){
            if(tmp.getStu_num().equals(a)){
                x=tmp.getState();
                break;
            }
        }
        return x;
    }

    public int find_choose(String name){
        int x=-1;
        if(name.equals("102203 通信原理"))x=0;
        else if(name.equals("102254 数字信号处理"))x=1;
        else if(name.equals("100256 高等数学"))x=2;
        return x;
    }

    public void clearall(){
        m_arraylist=new ArrayList<String>();
        m_arraylist_state=new ArrayList<student_state>();
        someVariable=0;
        first=true;
        first_AL=true;
    }
}
