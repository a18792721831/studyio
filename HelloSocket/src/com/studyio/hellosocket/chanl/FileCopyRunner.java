package com.studyio.hellosocket.chanl;

import java.io.File;

/**
 * @author jiayq
 * @Date 2021-01-30
 */
@FunctionalInterface
public interface FileCopyRunner {

    void copyFile(File source, File target);

}
