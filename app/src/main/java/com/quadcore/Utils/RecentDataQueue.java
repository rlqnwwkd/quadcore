package com.quadcore.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dalvik.annotation.TestTargetClass;

/**
 * Created by bbong on 2016-10-20.
 */

//////////////////////////////////////////
// 최신값 Constants._RECENT_QUEUE_SIZE 만큼만 유지
///////////////////////////////////////
public class RecentDataQueue<E> {
    private Queue<E> queue;
    private int maxSize;

    public int getSize(){
        return queue.size();
    }

    public void makeQueue(int size)
    {
        this.maxSize=size;
        queue = new LinkedList<E>();
    }

    public void insertData(E newData)
    {
        queue.offer(newData);
        if(queue.size()>maxSize)
        {
            queue.remove();
        }
    }

    public ArrayList<E> getAllDatas()
    {
        return new ArrayList<E>(queue);
    }
}