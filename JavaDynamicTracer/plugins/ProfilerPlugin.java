
import com.vmware.javatracer.BtracePlugin;
import com.vmware.javatracer.BtraceListener;
import javax.swing.JPanel;

public class ProfilerPlugin extends BtracePlugin {

	ProfilerPanel panel = new ProfilerPanel();
	public JPanel getPanel() {
            return panel;
	}
	public String getTitle() {
	    return new String("Profiler");
	}
	public BtraceListener getListener() {
	    BtraceListener listener = new BtraceListener() {
               public void eventNotify(int type, String msg) {
                  if (type != BtraceListener.CLASS_TYPE) {
                      return;
                  }
                  int index = msg.lastIndexOf(".");
                  if (index != -1) {
                      String classN = msg.substring(0, index);
                      String methodN = msg.substring(index+1);
                      panel.updateTable(classN, methodN);
                  }
               }
	    };
	    return listener;
	}

	public StringBuffer getBtraceScript() {
            return null;
	}	
}
