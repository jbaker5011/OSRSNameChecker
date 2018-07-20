package stonewall.osrs.namechecker;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.*;
import java.util.concurrent.*;


public class DiscordBot
{
    private static String fileName = "nameChecker.checks";
    private static int checks;
    private static Backup backup = new Backup();

        public static void main(String[] args)
        {
            UpdateActivity botUpdateActivity = null;
            String token = "DISCORDTOKEN";

            File file = new File(fileName);
            ExecutorService backupExecutor = new ScheduledThreadPoolExecutor(1);

            if (file.exists())
            {
                Reader reader = null;
                try
                {
                    reader = new BufferedReader(new FileReader(fileName));
                    int i = reader.read();
                    if (i > 0)
                    {
                        botUpdateActivity = new UpdateActivity(i);
                        checks = i;
                        System.out.println("Loaded " + String.valueOf(i) + " previous checks.");
                    }
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    if (file.createNewFile())
                    {
                        botUpdateActivity = new UpdateActivity();
                        checks = 0;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
            ExecutorService executor = Executors.newFixedThreadPool(20);

            ((ScheduledThreadPoolExecutor) backupExecutor).scheduleAtFixedRate(backup, 0, 10000, TimeUnit.MILLISECONDS);
            UpdateActivity finalBotUpdateActivity = botUpdateActivity;
            api.addMessageCreateListener(event ->
            {
                if (event.getMessage().getContent().toLowerCase().startsWith("!check")) {
                    String name = event.getMessage().getContent().split(" ", 2)[1];
                    if (!(name.length() > 13))
                    {
                        try
                        {
                            Future<Boolean> submit = executor.submit(new NameLookup(name.toLowerCase()));
                            if (submit.get(2000, TimeUnit.MILLISECONDS))
                            {
                                event.getChannel().sendMessage("The username " + name + " is available.");
                            }
                            else
                            {
                                event.getChannel().sendMessage("The username " + name + " is not available.");
                            }
                            Future<Integer> botActivity = executor.submit(finalBotUpdateActivity);
                            if (botActivity.get(2000, TimeUnit.MILLISECONDS) != null)
                            {
                                checks = botActivity.get();
                                api.updateActivity("Preformed " + checks + " name checks");                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            event.getChannel().sendMessage("An unknown error occurred, please contact Kanye.");
                        }
                    }
                    else
                    {
                        event.getChannel().sendMessage("Error: Username must not exceed 13 characters in length");
                    }
                }
            });
        }
        public static class Backup implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName, false);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                out.write(checks);
                out.close();
                fileOutputStream.close();
                System.out.println("Backing up total username checks... Current checks at: " + String.valueOf(checks));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
