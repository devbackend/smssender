/**
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
		
		/**
		 *	1. Скопировать файлики из папки jssc_so в папку /home/{username}/.jssc/linux/
         *  /home/developer/IdeaProjects/jSmsSender/src/messages.txt
         *  /home/developer/IdeaProjects/jSmsSender/src/numbers.txt
         */
      long before = System.currentTimeMillis();

      ExecutionLogger log = new ExecutionLogger(Integer.parseInt(args[2]));
      log.write("-------------------------------------------=Начало работы скрипта=--------------------------------------------------");
      //System.exit(1);

      File jsonSettings = new File("config.json");
      if(!jsonSettings.exists())
        throw new Exception("Путь к файлу с настройками указан неверно!");

      JSONParser parserJson = new JSONParser();
      JSONObject settings = (JSONObject) parserJson.parse(new FileReader(jsonSettings));

      String chanelNumber = args[2];

      final long COUNT_BY_SIM          = (long)       settings.get("countSmsBySim");
      final long RESTART_LATENCY       = (long) ((JSONObject) settings.get("latency")).get("reloadLatency");
      final JSONObject SIMBANK_CHANELS = (JSONObject) settings.get("simbankChanels");
      final JSONObject DEVICE_LIST     = (JSONObject) settings.get("chanel2device");

      ArrayList<Long> simList;
      simList = (ArrayList<Long>) SIMBANK_CHANELS.get(chanelNumber);

      String device = (String) DEVICE_LIST.get(chanelNumber);

      SmsSender sms = new SmsSender(device, args[0], args[1]);

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
      System.out.println("swb " + chanel + " " + sim);
      SimBank simBank = new SimBank("/dev/ttyACM0");
      simBank.changeSim(chanel, sim);
      smsSender.restart(restartLatency);
    }
}
