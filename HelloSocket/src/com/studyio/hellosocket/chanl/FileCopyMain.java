package com.studyio.hellosocket.chanl;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author jiayq
 * @Date 2021-01-30
 */
public class FileCopyMain {

    public static void main(String[] args) {

        // 不使用缓存区，使用输入流输出流之间的字节拷贝
        FileCopyRunner noBufferStreamCopy = (source, target) -> {
            try (InputStream inputStream = new FileInputStream(source);
                 OutputStream outputStream = new FileOutputStream(target)) {
                int data;
                LocalDateTime startTime = LocalDateTime.now();
                while ((data = inputStream.read()) != -1) {
                    outputStream.write(data);
                }
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                System.out.println(" noBufferStreamCopy : start - end = " + duration.toMillis());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // 使用带有缓存区的输入流输出流的字节拷贝
        FileCopyRunner bufferedStreamCopy = (source, target) -> {
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(source));
                 BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target))) {
                byte[] datas = new byte[1024];
                int index;
                LocalDateTime startTime = LocalDateTime.now();
                while ((index = inputStream.read(datas)) != -1) {
                    outputStream.write(datas, 0, index);
                }
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                System.out.println(" bufferedStreamCopy : start - end = " + duration.toMillis());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // 使用channel的缓存区的拷贝
        FileCopyRunner nioBufferCopy = (source, target) -> {
            try (FileChannel inputChannel = new FileInputStream(source).getChannel();
                 FileChannel outputChannel = new FileOutputStream(target).getChannel()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                LocalDateTime startTime = LocalDateTime.now();
                while (inputChannel.read(byteBuffer) != -1) {
                    byteBuffer.flip(); // 写模式 -> 读模式
                    while (byteBuffer.hasRemaining()) {
                        outputChannel.write(byteBuffer);
                    }
                    byteBuffer.clear(); // 读模式 -> 写模式
                }
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                System.out.println(" nioBufferCopy : start - end = " + duration.toMillis());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // 使用channel之间数据交换的拷贝
        FileCopyRunner nioTransferCopy = (source, target) -> {
            try (FileChannel inputChannel = new FileInputStream(source).getChannel();
                 FileChannel outputChannel = new FileOutputStream(target).getChannel()) {
                LocalDateTime startTime = LocalDateTime.now();
                long sum = 0L;
                long size = inputChannel.size();
                while (sum < size) {
                    sum += inputChannel.transferTo(0, size, outputChannel);
                }
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                System.out.println(" nioTransferCopy : start - end = " + duration.toMillis());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        // 测试
        FileCopyTest fileCopyTest = (CopyRunner, source, times, name) -> {
            File target = new File(source.getName() + "copy");
            long datetime = 0L;
            for (int i = 0; i < times; i++) {
                LocalDateTime startTime = LocalDateTime.now();
                CopyRunner.copyFile(source, target);
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                datetime += duration.toMillis();
                target.delete();
            }
            System.out.println(name + " : start - end = " + datetime + " , avg = " + datetime / times);
        };
        File smallFile = new File("K:/temp/6.mp4");
        File middleFile = new File("K:/temp/36.mp4");
        File bigFile = new File("K:/temp/396.mp4");
        int times = 10;
        System.out.println("small file : ");
        fileCopyTest.test(bufferedStreamCopy, smallFile, times, "bufferedStreamCopy");
        fileCopyTest.test(nioBufferCopy, smallFile, times, "nioBufferedCopy");
        fileCopyTest.test(nioTransferCopy, smallFile, times, "nioTransferCopy");

        System.out.println("\n");
        System.out.println("middile file : ");

        fileCopyTest.test(bufferedStreamCopy, middleFile, times, "bufferedStreamCopy");
        fileCopyTest.test(nioBufferCopy, middleFile, times, "nioBufferedCopy");
        fileCopyTest.test(nioTransferCopy, middleFile, times, "nioTransferCopy");

        System.out.println("\n");
        System.out.println("big file : ");

        fileCopyTest.test(bufferedStreamCopy, bigFile, times, "bufferedStreamCopy");
        fileCopyTest.test(nioBufferCopy, bigFile, times, "nioBufferedCopy");
        fileCopyTest.test(nioTransferCopy, bigFile, times, "nioTransferCopy");
    }

}
