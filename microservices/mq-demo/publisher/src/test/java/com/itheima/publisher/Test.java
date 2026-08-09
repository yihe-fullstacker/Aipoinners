package com.itheima.publisher;

public class Test {

    public static void main(String[] args) {

        int[] arr = {5,5,5,6,4,5};

        Test t = new Test();

        System.out.println(t.minNumberInRotateArray(arr));

    }

    public int minNumberInRotateArray(int [] array) {
        int left = 0;
        int right = array.length - 1;
        while(left < right){
            int mid = (left + right) / 2;
            //最小的数字在mid右边
            if(array[mid] > array[right])
                left = mid + 1;
                //最小数字要么是mid要么在mid左边
            else
                right --;
        }
        return array[left];
    }
}