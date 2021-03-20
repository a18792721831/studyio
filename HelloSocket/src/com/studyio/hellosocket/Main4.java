package com.studyio.hellosocket;

import java.util.Optional;

/**
 * @author jiayq
 * @Date 2021-03-11
 */
public class Main4 {

    public static void main(String[] args) {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(1, 0);
        Point p4 = new Point(2, 1);
        Point result = intersection(p1, p2, p3, p4);
        if (Optional.ofNullable(result).isEmpty()) {
            System.out.println("null");
        } else {
            System.out.println(result.x + "," + result.y);
        }
    }

    public static Point intersection(Point p1, Point p2, Point p3, Point p4) {
        double a = (p2.y - p1.y) / (p2.x - p1.x);
        double a_ = (p4.y - p3.y) / (p4.x - p3.x);
        if (a == a_) {
            return null;
        }
        double b = p1.y - a * p1.x;
        double b_ = p3.y - a_ * p3.x;
        return new Point((b_ - b) / (a - a_), a * (b_ - b) / (a - a_) + b);
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
