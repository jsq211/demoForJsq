package com.jsq.demo.test;

import com.alibaba.fastjson.JSON;

/**
 * @author Administrator
 */
public class SegmentTreeTest {
    Node root;
    public SegmentTreeTest(int left,int right){
        root=buildTree(left,right);
    }
    public Node buildTree(int left,int right){
        Node root=new Node(left,right);
        root.val = right + left;
        if(right-left>=1){
            int mid=(left+right)/2;
            root.lchild=buildTree(left,mid);
            root.rchild=buildTree(mid+1,right);
        }
        return root;
    }
    public int search(int left,int right){
        return search(root,left,right);
    }
    private int search(Node node,int left,int right){
        if(right>left && node == null) {
            return 0;
        }
        if(node.left==left&&node.right==right){
            return node.val;
        }
        int mid = (node.left + node.right) / 2;
        int value = 0;
        if (right <= mid){
            value = search(node.lchild, left, right);
        }else if (left > mid){
            value = search(node.rchild, left, right);
        } else{
            value = search(node.lchild, left, mid) + search(node.rchild, mid + 1, right);
        }
        return value;
    }
    public void update(int index,int val){
        update(root,index,val);
    }
    private int update(Node node,int index,int val){
        if(node.right==node.left && node.right==index){
            node.val=val;
            return node.val;
        }else{
            int mid=(node.left+node.right)/2;
            if(index<=mid){
                node.val=node.rchild.val+update(node.lchild,index,val);
            }else{
                node.val=node.lchild.val+update(node.rchild,index,val);
            }
            return node.val;
        }
    }

    class Node {
        int val; //存储  left~right的和
        int left;
        int right;
        Node lchild;
        Node rchild;

        public Node(int left, int right) {
            this.left = left;
            this.right = right;
            val = 0;
            lchild = null;
            rchild = null;
        }
    }

    public static void main(String[] args) {
        SegmentTreeTest segmentTreeTest = new SegmentTreeTest(0,10);
        segmentTreeTest.update(3,2);
        System.out.println(JSON.toJSONString(segmentTreeTest));
    }
}
