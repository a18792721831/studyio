package com.studyio.hellosocket.chanl;

import java.io.File;

/**
 * @author jiayq
 * @Date 2021-01-30
 */
@FunctionalInterface
public interface FileCopyTest {

    void test(FileCopyRunner fileCopyRunner, File source, int times, String name);

}
