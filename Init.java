import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        SmsSender sms = new SmsSender("/dev/ttyUSB0", args[0], args[1]);
        SimBank simBank = new SimBank("/dev/ttyACM0");

        String number, smsMessage;

        while((number = sms.getNumber()) != null)
        {
          smsMessage = sms.getRandomMessage();
          sms.smsSend(smsMessage, number);
          System.out.println("На номер " + number + " отправлено сообщение: \"" + smsMessage + "\"");
          //Thread.sleep(3000);
        }

      sms.close();
    */

      File jsonSettings = new File("config.json");
      if(!jsonSettings.exists())
        throw new Exception("Путь к файлу с настройками указан неверно!");

      JSONParser parserJson = new JSONParser();
      JSONObject settings = (JSONObject) parserJson.parse(new FileReader(jsonSettings));

      final long COUNT_BY_SIM          = (long) settings.get("countSmsBySim");
      final JSONObject SIMBANK_CHANELS = (JSONObject) settings.get("simbankChanels");
      final JSONObject DEVICE_LIST     = (JSONObject) settings.get("chanel2device");

      ArrayList<Integer> simList;
      for(int i=1; i<=SIMBANK_CHANELS.size(); i++)
      {
        System.out.println(DEVICE_LIST.get("" + i + ""));
        simList = (ArrayList<Integer>) SIMBANK_CHANELS.get("" + i + "");
        for(int k=0; k<simList.size(); k++)
        {
          /**
           * todo: собственно отправка сообщений и вся логика здесь будет
           *
           */
        }
      }



      long after = System.currentTimeMillis();
      long diff = (after - before)/1000;
	  System.out.println("Exec in " + diff + " sec");
	}
}
