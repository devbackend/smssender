import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.sound.sampled.Port;

//Класс для отправки смс-сообщений

public class SmsSender {
     
     private static SerialPort serialPort;
     private Stack numbers = new Stack();
     private List<String> messages = new ArrayList<String>();
     private static ExecutionLogger log;
     
     public SmsSender(String port, String numbers, String messages, ExecutionLogger log) {
          try {
               this.serialPort = new SerialPort(port);

            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);


            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(log), SerialPort.MASK_RXCHAR);
            try {
              this.setNumbers(numbers);
              this.setMessages(messages);
              this.log = log;
            } catch (Exception e) {
              System.out.println(e.getMessage());
              System.exit(1);
            }

          } catch(Exception e) {
               System.out.println(e.getMessage());
          }
          
     }

  public void restart(long restartSleep) throws SerialPortException, InterruptedException {
    char enter = 0x0D;

    serialPort.writeString("at+cfun=1" + enter);
    Thread.sleep(restartSleep);

    String str = "at+cmgf=0"+enter;
    serialPort.writeString(str);
    Thread.sleep(500); // "засыпаем" на секунду


  }
     
  //Функция разворачивания номера в нужном формате
  //Телефон в международном формате имеет 11 символов (79231111111)
  //11-Нечётное число, поэтому в конце добавляем F
  //И переставляем попарно цифры местами. Этого требует PDU-формат
  private static String reversePhone(String phonenum) {
         
         String phonenumPDU = "";
         phonenum = phonenum+"F";
         
         for (int i=0;i<12;i=i+2)
         {
               //System.out.println(phonenumPDU);
               phonenumPDU = phonenumPDU + phonenum.charAt(i+1) + phonenum.charAt(i);
         }
         
         return phonenumPDU;
     }
     
  //Функция конвертации текста СМС-ки в USC2 формат вместе с длиной сообщения
  //Возвращаемое значение <длина пакета><пакет>
  private static String StringToUSC2(String text) throws IOException {
         String str = "";
         
         byte[] msgb = text.getBytes("UTF-16");
         //Конвертация самой СМС
             String msgPacked = "";
             for (int i = 2; i < msgb.length; i++) {
                 String b = Integer.toHexString((int) msgb[i]);
                 if (b.length() < 2) msgPacked += "0";
                 msgPacked += b;
             }
         
         //Длина получившегося пакета в нужном формате
             String msglenPacked = Integer.toHexString(msgPacked.length() / 2);
         //Если длина нечётная - добавляем в конце 0
             if (msglenPacked.length() < 2) str += "0";
         
         //Формируем строку из длины и самого тела пакета
             str += msglenPacked;
             str += msgPacked;
         
             str = str.toUpperCase();
         
             return str;
         
     }
     
     //Получить длину сообщения
  private static int getSMSLength(String sms) {
         return (sms.length()/2 - 1);
     }

  public static boolean smsSend(String sms,String phone) throws Exception {

    try {

      //Формируем сообщение
      String message = "0011000B91"+reversePhone(phone)+"0008A7"+StringToUSC2(sms);

      //Отправляем запрос устройству

      char c = 0x0D;//Символ перевода каретки CR
      String str;
      //Очистим порт
      serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);

      str = "AT+CMGS="+getSMSLength(message)+c;
      serialPort.writeString(str);

      Thread.sleep(500);
      serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);

      c = 26;//Символ CTRL+Z
      serialPort.writeString(message+c);

      Thread.sleep(5500);

      return true;
    }
      catch (SerialPortException ex) {
      System.out.println(ex);
      return false;
    } catch (InterruptedException e) {
      //System.out.println(e);
      return false;
    }
         
  }

  public void close() throws Exception {

    serialPort.closePort();
  }

  public void setNumbers(String numberPath) throws Exception {
    File numbersFile = new File(numberPath);

    if(!numbersFile.exists())
      throw new Exception("Файл сообщений задан неверно!");

    BufferedReader buffer = new BufferedReader(new FileReader(numbersFile));
    String line;

    while((line = buffer.readLine()) != null)
    {
      this.numbers.add(line);
    }
  }

  public void setMessages(String messagePath) throws Exception {
    File numbersFile = new File(messagePath);

    if(!numbersFile.exists())
      throw new Exception("Файл сообщений задан неверно!");

    BufferedReader buffer = new BufferedReader(new FileReader(numbersFile));
    String line;

    while((line = buffer.readLine()) != null)
    {
      this.messages.add(line);
    }
  }

  public int getNumbersSize() {
    return numbers.size();
  }

  public int getMessagesSize() {
    return messages.size();
  }

  public String getRandomMessage() {
    if(this.messages.isEmpty())
      return null;

    int msgKey = (int) Math.round((Math.random() * (this.messages.size() - 1)));

    return this.messages.get(msgKey);
  }

  public String getNumber() {
    if(numbers.isEmpty())
      return null;

    return (String) this.numbers.pop();
  }

  private static class PortReader implements SerialPortEventListener {

    private static ExecutionLogger log;

    public PortReader(ExecutionLogger log) {
      PortReader.log = log;
    }

    public void serialEvent(SerialPortEvent event) throws Exception {
      if(event.isRXCHAR() && event.getEventValue() > 0){
        try {
          //Получаем ответ от устройства, обрабатываем данные и т.д.
          //String data = serialPort.readString(event.getEventValue());
          log.write(serialPort.readString(event.getEventValue()));
          //И снова отправляем запрос
          serialPort.writeString("Get data");
        }
        catch (SerialPortException ex) {
          System.out.println(ex);
        }
      }
    }
  }
}
