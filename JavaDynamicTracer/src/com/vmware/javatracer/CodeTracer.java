
/*
 * Class for tracing in a running jvm
 * Author: Kannan Balasubramanian
 */

package com.vmware.javatracer;

import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import com.sun.btrace.client.Client;
import com.sun.btrace.client.Main;
import com.sun.btrace.CommandListener;
import com.sun.btrace.comm.Command;
import com.sun.btrace.comm.ErrorCommand;
import com.sun.btrace.comm.ExitCommand;
import com.sun.btrace.comm.MessageCommand;
import java.util.Vector;

public class CodeTracer {

   static Client client = null;
   static Vector<BtraceListener> listeners = new Vector<BtraceListener>();
   public static final int BTRACE_DEFAULT_PORT = 2020;
   public static boolean DEBUG;
   public static boolean DUMP_CLASSES;
   public static String DUMP_DIR;
   public static String PROBE_DESC_PATH;

   static {
       DEBUG = Boolean.getBoolean("com.sun.btrace.debug");
       DUMP_CLASSES = Boolean.getBoolean("com.sun.btrace.dumpClasses");
       DUMP_DIR = System.getProperty("com.sun.btrace.dumpDir", ".");
       PROBE_DESC_PATH = System.getProperty("com.sun.btrace.probeDescPath", ".");
   }

   //Just a test method to run this class as a standalone...
   public static void main(String[] args) {

       if (args.length < 2) {
           System.out.println("Usage: java CodeTrace <pid> <agent>");
           return;
       }
       attachProcess(args[0], args[1], false);

   }
  
   public static void addListener(BtraceListener l) {
       listeners.addElement(l);
   }
  
   public static void removeListener(BtraceListener l) {
       listeners.removeElement(l);
   }

   //Method to attach to a running jvm given its pid and notify registered listeners of the event notification
   public static void attachProcess(String pid, String fileName, boolean detachFlag) {
       if (detachFlag) {
           if (client == null) {
               return;
           }
           try {
              client.close();
           } catch (Exception e) {
              e.printStackTrace();
           }
           client = null;
           return;
       }
      client = new Client(BTRACE_DEFAULT_PORT, PROBE_DESC_PATH, DEBUG, false, false, DUMP_CLASSES, DUMP_DIR, null);
       try {
            if (! new File(fileName).exists()) {
                System.err.println("File not found: " + fileName);
            }
            byte[] code = client.compile(fileName, null);
            if (code == null) {
                System.err.println("btrace compilation failed");
            }
            client.attach(pid, null, null);
            String [] jargs = {""};
            CommandListener cmdlistener = new CommandListener() {
                public void onCommand(Command cmd) throws IOException {
                      PrintWriter out = System.console().writer();
                      int type = cmd.getType();
                      if (type == Command.MESSAGE) {
                          final MessageCommand mcmd = (MessageCommand)cmd;
                          String msg = mcmd.getMessage();
                          if (msg != null) {
                              int msgType = BtraceListener.CLASS_TYPE;
                              String msgTypeStr = BtraceListener.CLASS_TYPE_STR;
                              if (msg.startsWith(BtraceListener.FILE_TYPE_READ_STR)) {
                                      msgType = BtraceListener.FILE_TYPE_READ;
                                      msgTypeStr = BtraceListener.FILE_TYPE_READ_STR;
                              } else if (msg.startsWith(BtraceListener.FILE_TYPE_WRITE_STR)) {
                                      msgType = BtraceListener.FILE_TYPE_WRITE;
                                      msgTypeStr = BtraceListener.FILE_TYPE_WRITE_STR;
                              } else if (msg.startsWith(BtraceListener.OBJECT_TYPE_STR)) {
                                      msgType = BtraceListener.OBJECT_TYPE;
                                      msgTypeStr = BtraceListener.OBJECT_TYPE_STR;
                              }
                              msg = msg.substring(msgTypeStr.length());
                              final String cmdmsg = msg;
                              for (int i=0; i<listeners.size(); i++) {
                                   BtraceListener l = listeners.elementAt(i);
                                   l.eventNotify(msgType, cmdmsg);
                              }
                              //out.print(mcmd.getMessage());
                              out.flush();
                          }
                      } else if (type == Command.EXIT) {
                         ExitCommand ecmd = (ExitCommand)cmd;
                         System.exit(ecmd.getExitCode());
                      } else if (type == Command.ERROR) {
                        ErrorCommand ecmd = (ErrorCommand)cmd;
                        Throwable cause = ecmd.getCause();
                        if (cause != null) {
                            cause.printStackTrace();
                        }
                      }
                }
           };
	   System.err.println("Before btrace script submitted..."); //kannan
           client.submit(fileName, code, jargs,cmdlistener);
	   System.err.println("After btrace script submitted..."); //kannan
           // createCommandListener(client));
       } catch (IOException exp) {
           System.err.println(exp.getMessage());
       }
   }
} 
