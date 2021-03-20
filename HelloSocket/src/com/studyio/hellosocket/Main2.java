package com.studyio.hellosocket;

import java.util.Arrays;

/**
 * @author jiayq
 * @Date 2021-03-10
 */
public class Main2 {

    public static void main(String[] args) {
        Point[] source = new Point[]{new Point(0.5, 0.5), new Point(2, 2), new Point(3, 1), new Point(4, 4)};
        Point point = new Point(2, 2);
        Point[] target = getNearestPoints(source, point, 3);
        Arrays.stream(target).forEach(p -> System.out.println("(" + p.x + "," + p.y + ")"));
    }

    private static Point[] getNearestPoints(Point[] points, Point p, int n) {
        if (n >= points.length) {
            return points;
        }
        if (n <= 0) {
            return new Point[0];
        }
        // 点的平移
        Arrays.stream(points).forEach(m -> {
            m.x = m.x - p.x;
            m.y = m.y - p.y;
        });
        // x的最大值
        double max = Arrays.stream(points).mapToDouble(m -> Math.abs(m.x)).max().getAsDouble();
        Point[] target = new Point[points.length];
        calc(points, target, max, n, 0);
        Point[] result = new Point[n];
        int temp = 0;
        for (int i = 0; i < n; i++) {
            Point point = null;
            while (point == null && temp < target.length) {
                point = target[temp++];
            }
            result[i] = point;
        }
        // 点的还原
        Arrays.stream(result).forEach(m -> {
            m.x = m.x + p.x;
            m.y = m.y + p.y;
        });
        return result;
    }

    /**
     * @param source 待查询点数组
     * @param target 空数组，需要和待查询点数组大小保持一致
     * @param end    结束
     * @param n      个数
     */
    private static void calc(Point[] source, Point[] target, double end, int n, int sum) {
        // 使用二分法找中点
        double middle = end / 2;
        // 得到半径的平方
        double square = middle * middle;
        for (int i = 0; i < source.length; i++) {
            // sorce 的点 在园内 => target 数组中
            if (source[i] != null && ((source[i].x < middle && source[i].y < middle) || (source[i].x * source[i].x + source[i].y * source[i].y < square))) {
                // 将点移动到结果数组中
                target[i] = source[i];
                source[i] = null;
                sum++;
            } else if (source[i].x == middle && source[i].y == middle) {
                // 将点移动到结果数组中
                target[i] = source[i];
                source[i] = null;
                sum++;
                if (sum == n) {
                    break;
                }
            }
            // target 的点 在圆外 => source 数组中
            if (target[i] != null && ((target[i].x > middle && target[i].y > middle) || (target[i].x * target[i].x + target[i].y + target[i].y > square))) {
                // 将点移动到待查数组中
                source[i] = target[i];
                target[i] = null;
                sum--;
            }

        }
        // 没有达到个数
        if (sum < n) {
            calc(source, target, (middle + end) / 2, n, sum);
        }
        // 刚刚好
        else if (sum == n) {
            return;
        }
        // 超了
        else if (sum > n) {
            calc(source, target, middle / 2, n, sum);
        }
        // 异常
        else {
            return;
        }
    }

    static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
