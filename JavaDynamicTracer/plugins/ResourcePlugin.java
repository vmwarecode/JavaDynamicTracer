
import com.vmware.javatracer.BtracePlugin;
import com.vmware.javatracer.BtraceListener;
import javax.swing.JPanel;

public class ResourcePlugin extends BtracePlugin {

	ResourcePanel panel = new ResourcePanel();
	public JPanel getPanel() {
            return panel;
	}
	public String getTitle() {
	    return new String("Resource Tracker");
	}
	public BtraceListener getListener() {
	    BtraceListener listener = new BtraceListener() {
                public void eventNotify(int type, String msg) {
                   if (type != BtraceListener.FILE_TYPE_READ && 
                       type != BtraceListener.FILE_TYPE_WRITE) {
                       return;
                   }
                   String mode = null;
                   if (type == BtraceListener.FILE_TYPE_READ) {
                       mode = panel.READ_MODE;
                       panel.nReads++;
                   } else if (type == BtraceListener.FILE_TYPE_WRITE) {
                       mode = panel.WRITE_MODE;
                       panel.nWrites++;
                   }
                   panel.updateTable(msg, mode);
                }
	    };
	    return listener;
	}

	public StringBuffer getBtraceScript() {
            StringBuffer buffer = new StringBuffer();
	    String buf = null;

           //Code for creating Btrace script for capturing all files that are read using FileInputStream

            buf = "    \n";
            buffer.append(buf);
            buf = "    @OnMethod(\n";
            buffer.append(buf);
            buf = "        clazz=\"java.io.FileInputStream\",\n";
            buffer.append(buf);
            buf = "        method=\"<init>\"\n";
            buffer.append(buf);
            buf = "    )\n";
            buffer.append(buf);
            buf = "    public static void onNewFileInputStream(@Self FileInputStream self, File f) {\n";
            buffer.append(buf);
            buf = "        name = str(f);\n";
            buffer.append(buf);
            buf = "    }\n";
            buffer.append(buf);
            buf = "    \n";
            buffer.append(buf);
            buf = "    @OnMethod(\n";
            buffer.append(buf);
            buf = "        clazz=\"java.io.FileInputStream\",\n";
            buffer.append(buf);
            buf = "        method=\"<init>\",\n";
            buffer.append(buf);
            buf = "        type=\"void (java.io.File)\",\n";
            buffer.append(buf);
            buf = "        location=@Location(Kind.RETURN)\n";
            buffer.append(buf);
            buf = "    )\n";
            buffer.append(buf);
            buf = "    \n";
            buffer.append(buf);
            buf = "    public static void onNewFileInputStream() {\n";
            buffer.append(buf);
            buf = "        if (name != null) {\n";
            buffer.append(buf);
            buf = "           println(strcat(\"" + BtraceListener.FILE_TYPE_READ_STR + "\", name));\n";
            buffer.append(buf);
            buf = "           name = null;\n";
            buffer.append(buf);
            buf = "        }\n";
            buffer.append(buf);
            buf = "    }\n";
            buffer.append(buf);

            //code for tracking file resources that are being written to 
            buf = "    \n";
            buffer.append(buf);
            buf = "    @OnMethod(\n";
            buffer.append(buf);
            buf = "        clazz=\"java.io.FileOutputStream\",\n";
            buffer.append(buf);
            buf = "        method=\"<init>\"\n";
            buffer.append(buf);
            buf = "    )\n";
            buffer.append(buf);
            buf = "    public static void onNewFileOutputStream(@Self FileOutputStream self, File f) {\n";
            buffer.append(buf);
            buf = "        name = str(f);\n";
            buffer.append(buf);
            buf = "    }\n";
            buffer.append(buf);
            buf = "    \n";
            buffer.append(buf);
            buf = "    @OnMethod(\n";
            buffer.append(buf);
            buf = "        clazz=\"java.io.FileOutputStream\",\n";
            buffer.append(buf);
            buf = "        method=\"<init>\",\n";
            buffer.append(buf);
            buf = "        type=\"void (java.io.File)\",\n";
            buffer.append(buf);
            buf = "        location=@Location(Kind.RETURN)\n";
            buffer.append(buf);
            buf = "    )\n";
            buffer.append(buf);
            buf = "    \n";
            buffer.append(buf);
            buf = "    public static void onNewFileOutputStream() {\n";
            buffer.append(buf);
            buf = "        if (name != null) {\n";
            buffer.append(buf);
            buf = "           println(strcat(\"" + BtraceListener.FILE_TYPE_WRITE_STR + "\", name));\n";
            buffer.append(buf);
            buf = "           name = null;\n";
            buffer.append(buf);
            buf = "        }\n";
            buffer.append(buf);
            buf = "    }\n";
            buffer.append(buf);

            return buffer;
	}	
}
