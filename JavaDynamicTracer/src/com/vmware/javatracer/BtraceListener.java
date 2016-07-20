package com.vmware.javatracer;

/*
 * Summary: Listener for handling event based on messages received from Btrace scripts
 * Author: Kannan Balasubramanian
 */

public interface BtraceListener {
   public static final int CLASS_TYPE = 0;
   public static final int FILE_TYPE_READ = 1;
   public static final int FILE_TYPE_WRITE = 2;
   public static final int OBJECT_TYPE = 3;
   public static final String CLASS_TYPE_STR = "CLASS:";
   public static final String FILE_TYPE_READ_STR = "FILE:READ:";
   public static final String FILE_TYPE_WRITE_STR = "FILE:WRITE:";
   public static final String OBJECT_TYPE_STR = "OBJECT:";
   //event notification containing the command name and the message
   public void eventNotify(int cmdType, String msg);
}
