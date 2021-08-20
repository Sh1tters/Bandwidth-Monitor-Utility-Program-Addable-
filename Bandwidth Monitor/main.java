import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;



/* A small utility program that tracks how much data you have uploaded and downloaded from the net
 * during the course of your current online session. */

class Bandwidth_Monitor {
    
    public static int[] timeData = {};
    public static int[] transmittedData = {};
    public static int[] receivedData = {};
    public static int[] session = {};
    public static void main(String[] args) throws Exception{

        /** Console Log */
        int reportCount = 0;
            while(true){
        /** Pc Uptime */
        long upTime = Algorithm.getPcUptime();

        long ms = TimeUnit.MILLISECONDS.convert(upTime, TimeUnit.MILLISECONDS) % 1000;
        long s = (TimeUnit.SECONDS.convert(upTime, TimeUnit.MILLISECONDS) % 60);
        long m = (TimeUnit.MINUTES.convert(upTime, TimeUnit.MILLISECONDS) % 60);
        long h = (TimeUnit.HOURS.convert(upTime, TimeUnit.MILLISECONDS) % 24);
        long d = (TimeUnit.DAYS.convert(upTime, TimeUnit.MILLISECONDS));
        /** Pc Uptime */

        /** Bytes Transmitted */
        String BytesTransmitted = Algorithm.getBytesTransmitted();
        /** Bytes Transmitted */

        /** Bytes Received */
        String BytesReceived = Algorithm.getBytesReceived();
        /** Bytes Received */

        /** Application Uptime */
        long AppMilliseconds = Algorithm.GetApplicationUptime() % 1000;
        long AppSeconds = (Algorithm.GetApplicationUptime() / 1000) % 60;
        long AppMinutes = (Algorithm.GetApplicationUptime() / 1000) / 60;

        /** Application Uptime */

                reportCount++;
                
                
                System.out.print("\nReport: " + reportCount + "\n" +
                    "PC Uptime: " + d + " days, " + h + " hours, " + m + " minutes, " + s + " seconds and " + ms +  " milliseconds\n" +
                    "Bytes Transmitted while in this online session: "+ BytesTransmitted + "\n" +
                    "Bytes Received while in this online session: "+ BytesReceived + "\n" +
                    "\n" +
                    "Application Uptime: " + AppMinutes + " minutes, " + AppSeconds + " seconds and " + AppMilliseconds + " milliseconds." + "\n" +
                    "");
                  
                // long to integers
                int minutes = (int) m;
                int sessionData = (int) AppMinutes;
                int DataReceived = Integer.parseInt(BytesReceived);
                int DataTransmitted = Integer.parseInt(BytesTransmitted);
        
                // Append values to the arrays
                timeData = Arrays.copyOf(timeData, timeData.length + 1);
                timeData[timeData.length - 1] = minutes;

                session = Arrays.copyOf(session, session.length + 1);
                session[session.length - 1] = sessionData;

                transmittedData = Arrays.copyOf(transmittedData, transmittedData.length + 1);
                transmittedData[transmittedData.length - 1] = DataTransmitted;

                receivedData = Arrays.copyOf(receivedData, receivedData.length + 1);
                receivedData[receivedData.length - 1] = DataReceived;

                /** timeData = pc uptime
                 * session = application uptime
                 * transmittedData = data that was transmitted
                 * receivedData = data that was received
                 */

                /**
                 * x line = timeData
                 * y line red = transmittedData
                 * y line blue = receivedData
                 */
        
                System.out.println(Arrays.toString(timeData));
                System.out.println(Arrays.toString(session));
                System.out.println(Arrays.toString(transmittedData));
                System.out.println(Arrays.toString(receivedData));

                /** Loop every 1 minute */
                Thread.sleep(6000 * 10);
            }
        
    }
}


class Algorithm {

    public static String getBytesReceived() throws Exception {
        String bytesReceived = "0";
        Process bytesTransmittedProc = Runtime.getRuntime().exec("net statistics workstation");
        BufferedReader in = new BufferedReader(new InputStreamReader(bytesTransmittedProc.getInputStream()));
        String line;
            while((line = in.readLine()) != null ){
                if(line.contains("Bytes received")) {
                    String regex = "  Bytes received\s+(.*)";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.matches()){
                        bytesReceived = matcher.group(1);
                        break;
                    }
                    break;
                }
            }
        return bytesReceived;
    }

    public static String getBytesTransmitted() throws Exception {
        String bytesTransmitted = "0";
        Process bytesTransmittedProc = Runtime.getRuntime().exec("net statistics workstation");
        BufferedReader in = new BufferedReader(new InputStreamReader(bytesTransmittedProc.getInputStream()));
        String line;
            while((line = in.readLine()) != null ){
                if(line.contains("Bytes transmitted")) {
                    String regex = "  Bytes transmitted\s+(.*)";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.matches()){
                        bytesTransmitted = matcher.group(1);
                        break;
                    }
                    break;
                }
            }

        return bytesTransmitted;
    }



    public static long getPcUptime() throws Exception {
        long uptime = -1;
        Process uptimeProc = Runtime.getRuntime().exec("net statistics workstation");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Statistics since")) {
                    SimpleDateFormat format = new SimpleDateFormat("'Statistics since' dd/MM/yyyy hh.mm.ss");
                    Date bootime = format.parse(line);
                    uptime = System.currentTimeMillis() - bootime.getTime();
                    break;
                }
            }

        return uptime;
    }

    public static long GetApplicationUptime() throws Exception {
        long ms = 0;
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        ms = rb.getUptime();

        return ms;
    }
}