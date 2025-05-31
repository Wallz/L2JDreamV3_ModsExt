//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Farm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import l2.commons.configuration.ExProperties;
import l2.gameserver.network.l2.components.ChatType;

public class FarmConfig {
    private static final Logger _log = Logger.getLogger(FarmConfig.class.getName());
    public static String[] EVENT_PartyDropStartTime;
    public static ChatType START_EVENT_CHAT;
    public static ChatType END_EVENT_CHAT;
    public static ChatType EXTRA_START_EVENT_CHAT;
    public static ChatType EXTRA_END_EVENT_CHAT;
    public static ChatType START_COUNTDOWN_EVENT_CHAT;
    public static ChatType COUNTDOWN_EVENT_CHAT;
    public static ChatType NEXT_EVENT_CHAT;
    public static List<int[]> PARTY_DROP_REWARD_VALUE;
    public static List<int[]> INDIVIDUAL_DROP_ITEMS;
    public static boolean ENABLEDROP;
    public static boolean ENABLE_ANNOUNCE_START;
    public static boolean ENABLE_ANNOUNCE_END;
    public static boolean ENABLEPARTYDROP;
    public static boolean ENABLEINDIVIDUALDROP;
    public static boolean ENABLE_EXTRA_ANNOUNCE_START;
    public static boolean ENABLE_EXTRA_ANNOUNCE_FINISH;
    public static boolean PARTYDROP_ACTIVE;
    public static boolean CHECKIP;
    public static boolean ENABLEPARTYCOUNTDOWN;
    public static boolean ENABLEPARTYCOUNTDOWNHOURS;
    public static boolean ENABLEPARTYCOUNTDOWNMINUTES;
    public static boolean ENABLE_NEXT_EVENT_ANNOUNCE_TYPE1;
    public static boolean ENABLE_NEXT_EVENT_ANNOUNCE_TYPE2;
    public static String START_EVENT_ANNOUNCE;
    public static String INITIAL_COUNTDOWN;
    public static String FINISH_COUNTDOWN_HOURS;
    public static String FINISH_COUNTDOWN_MINUTES;
    public static String FINISH_COUNTDOWN_SECONDS;
    public static String COUNTDOWN_INITIAL_PHRASE;
    public static String INITIAL_EXTRA_MESSAGE;
    public static String END_EXTRA_MESSAGE;
    public static String START_COUNTDOWN_HOURS;
    public static String START_COUNTDOWN_MINUTES;
    public static String FINISH_COUNTDOWN_EVENT;
    public static String NEXT_EVENT_INITIAL_PHRASE;
    public static String NEXT_EVENT_END_PHRASE;
    public static int PARTYDROP_ACTIVE_TIME;
    public static int PARTYDROP_MONSTER_ID;
    public static int PARTYDROP_MONSTER_RESPAWN;
    public static int[][] MONSTER_LOCS;

    public FarmConfig() {
    }

    private static int[][] parseLocs(String locs) {
        if (locs != null && !locs.isEmpty()) {
            StringTokenizer st = new StringTokenizer(locs, ";");
            int[][] locations = new int[st.countTokens()][3];

            for(int index = 0; st.hasMoreTokens(); ++index) {
                String[] xyz = st.nextToken().split(",");
                int[] realHourMin = new int[]{Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])};
                locations[index] = realHourMin;
            }

            return locations;
        } else {
            return (int[][])null;
        }
    }

    public static final ExProperties initProperties(String filename) {
        ExProperties result = new ExProperties();

        try {
            result.load(new File(filename));
        } catch (IOException var3) {
            _log.warning("FarmConfig: Error loading \"" + filename + "\" config.");
        }

        return result;
    }

    public static void Load() {
        ExProperties ep = initProperties("./config/custom/partyfarm.properties");
        PARTYDROP_ACTIVE = ep.getProperty("ActiveEvent", false);
        START_EVENT_CHAT = ChatType.valueOf(ep.getProperty("StartEventChatType", "CRITICAL_ANNOUNCE"));
        END_EVENT_CHAT = ChatType.valueOf(ep.getProperty("EndEventChatType", "CRITICAL_ANNOUNCE"));
        EXTRA_START_EVENT_CHAT = ChatType.valueOf(ep.getProperty("ExtraStartAnnounceChatType", "CRITICAL_ANNOUNCE"));
        EXTRA_END_EVENT_CHAT = ChatType.valueOf(ep.getProperty("ExtraEndAnnounceChatType", "CRITICAL_ANNOUNCE"));
        COUNTDOWN_EVENT_CHAT = ChatType.valueOf(ep.getProperty("CountDownChatType", "CRITICAL_ANNOUNCE"));
        START_COUNTDOWN_EVENT_CHAT = ChatType.valueOf(ep.getProperty("StartCountDownChatType", "CRITICAL_ANNOUNCE"));
        NEXT_EVENT_CHAT = ChatType.valueOf(ep.getProperty("NextEventChatType", "CRITICAL_ANNOUNCE"));
        ENABLE_ANNOUNCE_START = ep.getProperty("EnableAnnounceStart", false);
        ENABLE_ANNOUNCE_END = ep.getProperty("EnableAnnounceEnd", false);
        ENABLEDROP = ep.getProperty("DropEnable", false);
        ENABLEPARTYDROP = ep.getProperty("PartyDropEnable", false);
        ENABLEINDIVIDUALDROP = ep.getProperty("IndividualDropEnable", false);
        CHECKIP = ep.getProperty("CheckIp", false);
        ENABLEPARTYCOUNTDOWNHOURS = ep.getProperty("EnableCountdownHours", false);
        ENABLEPARTYCOUNTDOWNMINUTES = ep.getProperty("EnableCountdownMinutes", false);
        ENABLEPARTYCOUNTDOWN = ep.getProperty("EnableCountdown", false);
        ENABLE_EXTRA_ANNOUNCE_START = ep.getProperty("EnableExtraAnnounceStart", false);
        ENABLE_EXTRA_ANNOUNCE_FINISH = ep.getProperty("EnableExtraAnnounceFinish", false);
        ENABLE_NEXT_EVENT_ANNOUNCE_TYPE1 = ep.getProperty("EnableNextEventAnnounceType1", false);
        ENABLE_NEXT_EVENT_ANNOUNCE_TYPE2 = ep.getProperty("EnableNextEventAnnounceType2", false);
        START_EVENT_ANNOUNCE = ep.getProperty("StartEventAnnounce", "Party Farm: Event Started.");
        INITIAL_EXTRA_MESSAGE = ep.getProperty("StartEventExtraAnnounce", "Party Farm: Teleport in the GK to Party Zone!");
        END_EXTRA_MESSAGE = ep.getProperty("FinishEventExtraAnnounce", "Party Farm: Teleport in the GK to Party Zone!");
        INITIAL_COUNTDOWN = ep.getProperty("AnnounceStartEventDurationInitial", "Party Farm: Duration: ");
        START_COUNTDOWN_HOURS = ep.getProperty("AnnounceStartPhraseHours", " Hours(s)!");
        START_COUNTDOWN_MINUTES = ep.getProperty("AnnounceStartPhraseMinutes", " Minute(s)!");
        FINISH_COUNTDOWN_HOURS = ep.getProperty("AnnounceEventDurationHours", " Hours(s) till event finish!");
        FINISH_COUNTDOWN_MINUTES = ep.getProperty("AnnounceEventDurationMinutes", " Minute(s) till event finish!");
        FINISH_COUNTDOWN_SECONDS = ep.getProperty("AnnounceEventDurationSeconds", " Seconds(s) till event finish!");
        COUNTDOWN_INITIAL_PHRASE = ep.getProperty("CountdownInitialPhrase", "Party Farm: ");
        FINISH_COUNTDOWN_EVENT = ep.getProperty("AnnounceFinishEvent", "Party Farm: Event has been Disabled!");
        NEXT_EVENT_INITIAL_PHRASE = ep.getProperty("AnnounceNextEventInitialPhrase", "Party Farm: Next Event: ");
        NEXT_EVENT_END_PHRASE = ep.getProperty("AnnounceFinishEventEndlPhrase", "(GMT-3).");
        PARTYDROP_ACTIVE_TIME = ep.getProperty("PartyDropDuration", 10);
        PARTYDROP_MONSTER_RESPAWN = ep.getProperty("PartyDropMonsterRespawn", 60);
        PARTYDROP_MONSTER_ID = ep.getProperty("PartyDropMonsterID", 22215);
        EVENT_PartyDropStartTime = ep.getProperty("PartyDrop_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
        MONSTER_LOCS = parseLocs(ep.getProperty("MonsterLoc", "null"));
        PARTY_DROP_REWARD_VALUE = new ArrayList();
        String[] partydropReward = ep.getProperty("PartyDropRewards", "57,100000,100000,100").split(";");
        String[] individualdropReward = partydropReward;
        int var3 = partydropReward.length;

        int var4;
        for(var4 = 0; var4 < var3; ++var4) {
            String reward = individualdropReward[var4];
            String[] rewardSplit = reward.split(",");
            if (rewardSplit.length == 4) {
                try {
                    PARTY_DROP_REWARD_VALUE.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]), Integer.parseInt(rewardSplit[2]), Integer.parseInt(rewardSplit[3])});
                } catch (NumberFormatException var10) {
                    if (!reward.isEmpty()) {
                    }
                }
            }
        }

        INDIVIDUAL_DROP_ITEMS = new ArrayList();
        individualdropReward = ep.getProperty("IndividualDropRewards", "57,100000,100000,100").split(";");
        String[] var11 = individualdropReward;
        var4 = individualdropReward.length;

        for(int var12 = 0; var12 < var4; ++var12) {
            String reward = var11[var12];
            String[] rewardSplit = reward.split(",");
            if (rewardSplit.length == 4) {
                try {
                    INDIVIDUAL_DROP_ITEMS.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]), Integer.parseInt(rewardSplit[2]), Integer.parseInt(rewardSplit[3])});
                } catch (NumberFormatException var9) {
                    if (!reward.isEmpty()) {
                    }
                }
            }
        }

    }
}
