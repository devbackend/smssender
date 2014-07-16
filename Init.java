import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.print.DocFlavor;
import java.io.*;

import java.util.ArrayList;

class Init {
	
	public static void main(String[] args) throws Exception {
		
		/**
		 *	1. Скопировать файлики из папки jssc_so в папку /home/{username}/.jssc/linux/
	     *  todo: остановка при Exception порта
         *  /home/developer/IdeaProjects/jSmsSender/src/messages.txt
         *  /home/developer/IdeaProjects/jSmsSender/src/numbers.txt
         */
      long before = System.currentTimeMillis();
      /*

      */

      File jsonSettings = new File("config.json");
      if(!jsonSettings.exists())
        throw new Exception("Путь к файлу с настройками указан неверно!");

      JSONParser parserJson = new JSONParser();
      JSONObject settings = (JSONObject) parserJson.parse(new FileReader(jsonSettings));

      String chanelNumber = args[2];

      final long COUNT_BY_SIM          = (long)       settings.get("countSmsBySim");
      final JSONObject SIMBANK_CHANELS = (JSONObject) settings.get("simbankChanels");
      final JSONObject DEVICE_LIST     = (JSONObject) settings.get("chanel2device");

      ArrayList<Integer> simList;
      simList = (ArrayList<Integer>) SIMBANK_CHANELS.get(chanelNumber);

      String device = (String) DEVICE_LIST.get(chanelNumber);


      /**
       * todo: собственно отправка сообщений и вся логика здесь будет
       *
       */


      SmsSender sms = new SmsSender(device, args[0], args[1]);

      String number, smsMessage;
      int sendedSmsCount = 0;
      int currentSimKey  = 0;

      while((number = sms.getNumber()) != null)
      {
        if(sendedSmsCount%COUNT_BY_SIM == 0)
        {
          if(currentSimKey == simList.size())
            break;

          /**
           * todo: функция на смену симкарты должна принимать int
           */
          Init.changeSim(Integer.parseInt(chanelNumber), (Number) simList.get(currentSimKey));
          currentSimKey++;
        }


        smsMessage = sms.getRandomMessage();
        //sms.smsSend(smsMessage, number);
        System.out.println("На номер " + number + " отправлено сообщение: \"" + smsMessage + "\"");
        sendedSmsCount++;
      }

      sms.close();

      long after = System.currentTimeMillis();
      long diff = (after - before)/1000;
	  System.out.println("Exec in " + diff + " sec");
	}

    public static void changeSim(int chanel, Number sim) throws Exception {
      //SimBank simBank = new SimBank("/dev/ttyACM0");
      //simBank.changeSim(chanel, sim);
      System.out.println("svb " + chanel + ' ' + sim);
    }
}
