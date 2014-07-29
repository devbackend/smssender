import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class ExecutionLogger {

  private File logFile;
  private FileOutputStream fos;

  public ExecutionLogger(int chanel) throws Exception {

    /**
     * todo: дату правильно писать
     */

    Calendar d = Calendar.getInstance();
    String filename = System.getProperty("user.dir") + "/log_" + chanel + "_" + d.get(Calendar.YEAR) + "-"
                                                                              + (d.get(Calendar.MONTH) + 1) + "-"
                                                                              + d.get(Calendar.DAY_OF_MONTH) + "-"
                                                                              + d.get(Calendar.HOUR_OF_DAY) + "-"
                                                                              + d.get(Calendar.MINUTE) + "-"
                                                                              + d.get(Calendar.SECOND) + "-" + ".log";
    logFile = new File(filename);
    logFile.createNewFile();

    this.fos = new FileOutputStream(logFile);
  }

  public void write(String logStr) throws Exception {
    /**
     * todo: оптимизировать запись построчно!
     */

    String enter = "\r\n";
    fos.write(logStr.getBytes());
    fos.write(enter.getBytes());
  }

}
