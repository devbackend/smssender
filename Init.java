/**
 * jSmsSender - программа для рассылки СМС-сообщений по заданной базе номеров
 * version 0.99
 * todo: убрать весь хард-код и тогда будет полноценная версия 1.0
 * todo: навести порядок в работе, инициализации и объявлении переменных
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.print.DocFlavor;
import java.io.*;

import java.util.ArrayList;

class Init {
	
	public static void main(String[] args) throws Exception {

      //начало отсчёта времени исполнения
	  long before = System.currentTimeMillis();

      //файл конфига
      File jsonSettings = new File("config.json");

      //проверяем существует ли файл
      if(!jsonSettings.exists())
        throw new Exception("Путь к файлу с настройками указан неверно!");

      //передаем входные параметры в переменные
      String filepathToNumbers = args[0], filepathToMessages = args[1], chanelNumber = args[2];

      //открываем для записи файл логов
      ExecutionLogger log = new ExecutionLogger(Integer.parseInt(chanelNumber));
      log.write("-------------------------------------------=Начало работы скрипта=--------------------------------------------------");

      //получаем все настройки
      JSONParser parserJson = new JSONParser();
      JSONObject settings = (JSONObject) parserJson.parse(new FileReader(jsonSettings));

      final long COUNT_BY_SIM          = (long)       settings.get("countSmsBySim");
      final long RESTART_LATENCY       = (long) ((JSONObject) settings.get("latency")).get("reloadLatency");
      final JSONObject SIMBANK_CHANELS = (JSONObject) settings.get("simbankChanels");
      final JSONObject DEVICE_LIST     = (JSONObject) settings.get("chanel2device");

      ArrayList<Long> simList;
      simList = (ArrayList<Long>) SIMBANK_CHANELS.get(chanelNumber);

      String device = (String) DEVICE_LIST.get(chanelNumber);
      //настройки получены

      //открываем порт модема
      SmsSender sms = new SmsSender(device, filepathToNumbers, filepathToMessages, log);

      String number, smsMessage;
      int sendedSmsCount = 0, currentSimKey  = 0;
      long currentSimcard;

      while((number = sms.getNumber()) != null)
      {
        if(sendedSmsCount%COUNT_BY_SIM == 0)
        {
          if(currentSimKey == simList.size())
            break;

          currentSimcard = simList.get(currentSimKey);
          Init.changeSim(Integer.parseInt(chanelNumber), currentSimcard, sms, RESTART_LATENCY);
          log.write("Смена сим-карты на номер " + currentSimcard);
          currentSimKey++;
          log.write("Перезагрузка модема");
        }


        smsMessage = sms.getRandomMessage();
        sms.smsSend(smsMessage, number);
        log.write("На номер " + number + " отправлено сообщение: \"" + smsMessage + "\"");
        System.out.println("На номер " + number + " отправлено сообщение: \"" + smsMessage + "\"");
        sendedSmsCount++;
      }

      sms.close();

      long after = System.currentTimeMillis();
      long diff = (after - before)/1000;
      log.write("-------------------------------------------=Конец работы скрипта=--------------------------------------------------");
      log.write("Время исполнения: " + diff + " сек");
	  System.out.println("Exec in " + diff + " sec");
	}

    public static void changeSim(int chanel, long sim, SmsSender smsSender, long restartLatency) throws Exception {
      /*
      System.out.println("swb " + chanel + " " + sim);
      SimBank simBank = new SimBank("/dev/ttyACM0");
      simBank.changeSim(chanel, sim);
      smsSender.restart(restartLatency);
      */
    }
}
