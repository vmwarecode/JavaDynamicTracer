
import com.vmware.javatracer.BtracePlugin;
import com.vmware.javatracer.BtraceListener;
import javax.swing.JPanel;

public class ObjectPlugin extends BtracePlugin {

	ObjectPanel panel = new ObjectPanel();
	public JPanel getPanel() {
            return panel;
	}
	public String getTitle() {
	    return new String("Object Tracker");
	}
	public BtraceListener getListener() {
	    BtraceListener listener = new BtraceListener() {
		    public void eventNotify(int type, String msg) {
		       if (type != BtraceListener.OBJECT_TYPE) {
                           return;
		       }
                       panel.updateTable(msg);
		    }
	    };
	    return listener;
	}

	public StringBuffer getBtraceScript() {
            StringBuffer buffer = new StringBuffer();
	    String buf = null;
            buf = "    @OnMethod(\n";
            buffer.append(buf);
            buf = "        clazz=\"/javax\\\\.swing\\\\..*/\",\n";
            buffer.append(buf);
            buf = "        method=\"<init>\"\n";
            buffer.append(buf);
            buf = "    )\n";
            buffer.append(buf);
            buf = "    public static void onnew(@Self Object o) {\n";
            buffer.append(buf);
            buf = "        println(strcat(\"" + BtraceListener.OBJECT_TYPE_STR + "\" , name(classOf(o))));\n";
            buffer.append(buf);
            buf = "    }\n";
            buffer.append(buf);
            return buffer;
	}	
}
