package com.studyio.hellosocket;

/**
 * @author jiayq
 * @Date 2021-03-10
 */
public class Main1 {

    public static void main(String[] args) {
        int[] numbers = new int[]{1, 2, 2, 2, 2, 2, 2, 2};
        int num = -2;
        System.out.println(comp(numbers, num, 0, numbers.length - 1));
    }

    private static int comp(int[] numbers, long n, int start, int end) {
        // 不在数组中
        if (n < numbers[start] || n > numbers[end]) {
            return -1;
        }
        int middle = (start + end) / 2;
        // 后半段
        if (n > numbers[middle]) {
            return comp(numbers, n, middle, end);
        }
        // 正好命中
        else if (n == numbers[middle]) {
            int temp = middle;
            // 处理相等的数组元素
            while (numbers[temp] == n) {
                temp--;
            }
            return ++temp;
        }
        // 前半段
        else if (n < numbers[middle]) {
            return comp(numbers, n, start, middle);
        }
        // 异常
        else {
            return -1;
        }
    }
}
