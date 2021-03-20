package com.studyio.hellosocket;

/**
 * @author jiayq
 * @Date 2021-03-13
 */
public class Main5 {

    /**
     * 用数组实现交替
     * 数组只有两个元素，因此如果times是奇数，就修改数组第二个元素，否则修改第一个元素
     * 因为是两个线程都要访问，因此需要加上volatile保证现场访问的数据是一致的
     * 不会出现脏读，幻读，脏写等问题
     */
    private static volatile long[] numbers = new long[2];

    /**
     * 初始化为1，表示从线程1，也就是奇数线程开始
     */
    private static volatile int times = 1;

    /**
     * 防止线程死循环
     */
    private static final long flag = 100;

    public static void main(String[] args) {
        Sequeces sequeces1 = new Sequeces("线程1 : ", 1);
        Sequeces sequeces2 = new Sequeces("线程2 : ", 0);
        sequeces1.start();
        sequeces2.start();
    }

    private static class Sequeces extends Thread {

        private String name;

        private int num;

        public Sequeces(String name, int num) {
            this.name = name;
            this.num = num;
        }

        @Override
        public void run() {
            // 线程内部是死循环
            while (true) {
                // 使用synchronized关键字获取主类的类锁
                synchronized (Main5.class) {
                    int temp;
                    // 获取是奇数线程还是偶数线程
                    if ((temp = times % 2) == num) {
                        // 打印并更新线程对应变量的值
                        System.out.println(name + (numbers[temp] = (numbers[0] + numbers[1])));
                        // 如果是0则要加1
                        if (numbers[0] == 0) {
                            numbers[0]++;
                        }
                        // 每次修改完变量的值，都需要将有效循环次数加1
                        times++;
                    }
                    // 如果没到本线程，但是线程已经获取了锁，所以需要尽快释放锁
                    else {
                        // 调用continue尽快结束本次循环，释放锁
                        continue;
                    }
                    // 如果次数超出限制，那么结束线程
                    if (times > flag) {
                        return;
                    }
                }
            }
        }

    }

}
