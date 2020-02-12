import com.yedibit.kandilli4j.EarthquakeInfo;
import com.yedibit.kandilli4j.EarthquakeListProvider;
import com.yedibit.kandilli4j.exception.ParseEarthquakeException;
import com.yedibit.kandilli4j.exception.ReadEarthquakeException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Project: deprem Created by Mustafa Çelik on 10/3/19, Thu Contact: celikmustafa89@gmail.com
 */
public class Deprem {
    public static void main(String[] args) {

        EarthquakeListProvider iProvider = new EarthquakeListProvider();

        try {

            File yourFile= new File("/Users/mustafacelik/Projects/deprem/src/main/resources/alarm.wav");
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);

            // reads all earthquakes
            Vector<EarthquakeInfo> all = iProvider.getLastEarthquakes();

            // reads last ten earthquakes
            Vector<EarthquakeInfo> lastten = iProvider.getLastEarthquakes(10);

            // reads all earthquakes greater than 4.0 (Ml)
            Vector<EarthquakeInfo> allGreaterThan4Ml = iProvider.getLastEarthquakes(4.0);

            // reads last ten earthquakes greater than 4.0 (Ml)
            Vector<EarthquakeInfo> lastTenGreaterThan4Ml = iProvider.getLastEarthquakes(10, 4.0);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            // System.out.println(formatter.format(date));
            long nowTime = date.getTime();
            Date updateTime = new Date(nowTime - 10000000 * 1000000000);

            while(true){
                TimeUnit.SECONDS.sleep(20);
                Vector<EarthquakeInfo> lastEarthquakes = iProvider.getLastEarthquakes(100, 3.5);
                Collections.reverse(lastEarthquakes);
                for (EarthquakeInfo iInfo : lastEarthquakes) {
                    if(iInfo.getDate().after(updateTime)){
                        updateTime = iInfo.getDate();
                        System.out.println(iInfo.toString());
                        System.out.println("DANGER DANGER DANGER !!!");
                        clip.start();
                    }
                }
            }

        } catch (ReadEarthquakeException e) {
            System.out.println("can not read earthquake information");
            e.printStackTrace();
        } catch (ParseEarthquakeException e) {
            System.out.println("can not parse earthquake information");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
