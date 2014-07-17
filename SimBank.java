import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SimBank {

  private SerialPort serialPort;

  public SimBank(String port) {
    try {
      this.serialPort = new SerialPort(port);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public void changeSim(int chanel, Number sim) throws SerialPortException {
    try {
      //Открываем порт
      serialPort.openPort();
      //Выставляем параметры
      serialPort.setParams(SerialPort.BAUDRATE_115200,
              SerialPort.DATABITS_8,
              SerialPort.STOPBITS_1,
              SerialPort.PARITY_NONE);

      //запишем клавиши в переменные
      char enter = 0x0D;

      //команда на смену сим-карты
      String str = "swb " + chanel + " " + sim + enter; //уточнить нужен ли Enter
      serialPort.writeString(str);

      Thread.sleep(1000); // "засыпаем" на секунду

      serialPort.closePort();
    } catch (SerialPortException ex) {
      System.out.println(ex);
    } catch (InterruptedException e) {
      System.out.println(e);
    }
  }

  public int getSimInChanel(int chanel) {
    try {
      //Открываем порт
      serialPort.openPort();
      //Выставляем параметры
      serialPort.setParams(SerialPort.BAUDRATE_115200,
              SerialPort.DATABITS_8,
              SerialPort.STOPBITS_1,
              SerialPort.PARITY_NONE);

      //запишем клавиши в переменные
      char enter = 0x0D;

      //команда на смену сим-карты
      String str = "swb " + chanel + enter;
      serialPort.writeString(str);

      Thread.sleep(1000); // "засыпаем" на секунду

      /**
       * todo: получение ответа от симБанка
       */

      serialPort.closePort();

      return 1;

    } catch (SerialPortException ex) {
      System.out.println(ex);
      return 0;
    } catch (InterruptedException e) {
      System.out.println(e);
      return 0;
    }
  }
}
