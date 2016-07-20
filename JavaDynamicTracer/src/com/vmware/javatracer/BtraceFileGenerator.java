package com.vmware.javatracer;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/*
 * Summary: Class for generating btrace script based on user options from the UI
 * Author: Kannan
 */

public class BtraceFileGenerator {

    //Method to create a btrace script file that will be passed onto bytetracer for monitoring a running jvm.
    public File createBTraceFile(String pid, Element [] elements, StringBuffer [] bscripts) {
        String fileName = "JAVA_TRACER_TMP_" + pid;
        File bTraceFile = new File(fileName + ".java");
        try {
            if (bTraceFile.exists()) {
                bTraceFile.delete();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(bTraceFile));
            String buf = "import com.sun.btrace.annotations.*;\n";
            writer.write(buf, 0, buf.length());
            buf = "import static com.sun.btrace.BTraceUtils.*;\n";
            writer.write(buf, 0, buf.length());
            buf = "import java.lang.reflect.Field; \n";
            writer.write(buf, 0, buf.length());
            buf = "import java.io.File;\n";
            writer.write(buf, 0, buf.length());
            buf = "import java.io.FileInputStream;\n";
            writer.write(buf, 0, buf.length());
            buf = "import java.io.FileOutputStream;\n";
            writer.write(buf, 0, buf.length());
            buf = "@BTrace public class " + fileName + " {\n";
            writer.write(buf, 0, buf.length());

            buf = "    @TLS private static String name;\n";
            writer.write(buf, 0, buf.length());

            int size = elements.length;
            Element elem = null;
            String className = null;
            String methodName = null;
            String fieldName = null;
            int count = 0;
            for (int i=0; i<size; i++) {
                 //elem = (Element)model.getElementAt(i);
                 elem = elements[i];
                 className = elem.getClassName();
                 methodName = elem.getMethodName();
                 fieldName = elem.getFieldName();
                 if (fieldName != null && fieldName.length() > 0) {
                     buf = "    private static Field field" + count + " = field(\"" + className + "\", \"" + fieldName + "\");\n\n";
                     writer.write(buf, 0, buf.length());
                 }
                 buf = "    @OnMethod(\n";
                 writer.write(buf, 0, buf.length());
                 buf = "        clazz=\"" + className + "\",\n";
                 writer.write(buf, 0, buf.length());
                 buf = "        method=\"/" + methodName + "/\"\n";
                 writer.write(buf, 0, buf.length());
                 buf = "    )\n";
                 writer.write(buf, 0, buf.length());
                 buf = "    public static void m" + i + "(@Self Object obj) {\n";
                 writer.write(buf, 0, buf.length());
                 buf = "        print(strcat(\"" + BtraceListener.CLASS_TYPE_STR + "\", strcat(strcat(name(probeClass()), \".\"), probeMethod())));\n";
                 writer.write(buf, 0, buf.length());
                 if (fieldName != null && fieldName.length() > 0) {
                     buf = "    printFields(get(field" + count + ", obj));\n";
                     writer.write(buf, 0, buf.length());
                     count++;
                 }
                 buf = "    }\n";
                 writer.write(buf, 0, buf.length());
            }

            if (bscripts != null) {
                for (int i=0; i<bscripts.length; i++) {
                     if (bscripts[i] != null) {
                         writer.write(bscripts[i].toString(), 0, bscripts[i].length());
		     }
                }
            }

            //End the class
            buf = "}\n";
            writer.write(buf, 0, buf.length());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bTraceFile;
    }
}

//A wrapper class for holding classnames, methodnames and fieldnames and is used as an element in the JList model
class Element {
    private String className;
    private String methodName;
    private String fieldName;

    public Element(String c, String m, String f) {
       className = c;
       methodName = m;
       if (methodName.length() == 0) {
           //Regular expression for selecting all methods in the class
           methodName = ".*";
       }
       fieldName = f.trim();
    }

    //return fully qualified className
    public String getClassName() {
       return className;
    }

    //return method name of a class. A value of ".*" implies all methods of the class
    public String getMethodName() {
       return methodName;
    }

    //return field name of a class
    public String getFieldName() {
       return fieldName;
    }

    //returns  <classname>.<methodname>.<fieldname> or <classname>.<methodname>
    public String toString() {
       if (fieldName.length() > 0) {
           return className + "." + methodName + "." + fieldName;
       } else {
           return className + "." + methodName;
       }
    }
}
