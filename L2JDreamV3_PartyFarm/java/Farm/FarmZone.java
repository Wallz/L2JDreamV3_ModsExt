//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Farm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmZone extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(FarmZone.class);
    private static ScheduledFuture<?> _startTask;
    boolean _canReward = false;
    static HashMap<String, Integer> playerIps = new HashMap();
    Calendar currentTime = Calendar.getInstance();
    Calendar nextStartTime = null;
    Calendar testStartTime;
    private Calendar NextEvent;
    private static boolean _active = false;
    private static final ArrayList<SimpleSpawner> _ch_spawns = new ArrayList();
    public static boolean _started = false;
    public static boolean _aborted = false;
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    public FarmZone() {
    }

    public void onLoad() {
        FarmConfig.Load();
        CharListenerList.addGlobal(this);
        if (FarmConfig.PARTYDROP_ACTIVE) {
            _active = true;
            StartCalculationOfNextEventTime();
            _log.info("Loaded Event: Party Drop [state: activated]");
        } else {
            _log.info("Loaded Event: Party Drop [state: deactivated]");
        }

    }

    private static boolean isActive() {
        return IsActive("FarmZone");
    }

    public void activateEvent() {
        if (!isActive()) {
            if (_startTask == null) {
                StartCalculationOfNextEventTime();
            }

            ServerVariables.set("partydropschedule", "on");
            _log.info("Event 'PartyDrop Daily' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.PartyDrop.AnnounceEventStarted", (String[])null);
        } else {
            _log.info("Event 'Party Drop Daily' already active.");
        }

        _active = true;
    }

    public void deactivateEvent() {
        if (isActive()) {
            if (_startTask != null) {
                _startTask.cancel(false);
                _startTask = null;
            }

            ServerVariables.unset("partydropschedule");
            _log.info("Event 'Party Drop' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.PartyDrop.AnnounceEventStoped", (String[])null);
        } else {
            _log.info("Event 'Party Drop' not active.");
        }

        _active = false;
    }

    public static boolean is_started() {
        return _started;
    }

    public void startEvent() {
        if (SetActive("FarmZone", true)) {
            spawnEventManagers();
            _aborted = false;
            _started = true;
            _log.info("Event 'Party Drop' started.");
            if (FarmConfig.ENABLE_ANNOUNCE_START) {
                Announcements.getInstance().announceToAll(FarmConfig.START_EVENT_ANNOUNCE, FarmConfig.START_EVENT_CHAT);
            }

            ThreadPoolManager.getInstance().schedule(new StopTask(), (long)FarmConfig.PARTYDROP_ACTIVE_TIME * 1000L * 60L);
            if (FarmConfig.ENABLE_EXTRA_ANNOUNCE_START) {
                Announcements.getInstance().announceToAll(FarmConfig.INITIAL_EXTRA_MESSAGE, FarmConfig.EXTRA_START_EVENT_CHAT);
            }

            if (FarmConfig.ENABLEPARTYCOUNTDOWNHOURS) {
                Announcements.getInstance().announceToAll(FarmConfig.INITIAL_COUNTDOWN + FarmConfig.PARTYDROP_ACTIVE_TIME / 60 + " " + FarmConfig.START_COUNTDOWN_HOURS, FarmConfig.START_COUNTDOWN_EVENT_CHAT);
            }

            if (FarmConfig.ENABLEPARTYCOUNTDOWNMINUTES) {
                Announcements.getInstance().announceToAll(FarmConfig.INITIAL_COUNTDOWN + FarmConfig.PARTYDROP_ACTIVE_TIME + " " + FarmConfig.START_COUNTDOWN_MINUTES, FarmConfig.START_COUNTDOWN_EVENT_CHAT);
            }

            if (FarmConfig.ENABLEPARTYCOUNTDOWN) {
                waiter((long)FarmConfig.PARTYDROP_ACTIVE_TIME * 60L * 1000L);
            }
        } else {
            _log.info("Event 'Party Drop' already started.");
        }

    }

    public void stopEvent() {
        if (SetActive("FarmZone", false)) {
            unSpawnEventManagers();
            _started = false;
            _log.info("Event 'Party Drop' stopped.");
            StartCalculationOfNextEventTime();
            if (FarmConfig.ENABLE_ANNOUNCE_END) {
                Announcements.getInstance().announceToAll(FarmConfig.FINISH_COUNTDOWN_EVENT, FarmConfig.END_EVENT_CHAT);
            }

            if (FarmConfig.ENABLE_EXTRA_ANNOUNCE_FINISH) {
                Announcements.getInstance().announceToAll(FarmConfig.END_EXTRA_MESSAGE, FarmConfig.EXTRA_END_EVENT_CHAT);
            }

            if (FarmConfig.ENABLE_NEXT_EVENT_ANNOUNCE_TYPE1) {
                Announcements.getInstance().announceToAll(FarmConfig.NEXT_EVENT_INITIAL_PHRASE + getNextTime() + " " + FarmConfig.NEXT_EVENT_END_PHRASE, FarmConfig.NEXT_EVENT_CHAT);
            }

            if (FarmConfig.ENABLE_NEXT_EVENT_ANNOUNCE_TYPE2) {
                Announcements.getInstance().announceToAll(FarmConfig.NEXT_EVENT_INITIAL_PHRASE + NextEvent.getTime() + " " + FarmConfig.NEXT_EVENT_END_PHRASE, FarmConfig.NEXT_EVENT_CHAT);
            }
        } else {
            _log.info("Event 'Party Drop' not started.");
        }

    }

    public void onPlayerEnter(Player player) {
    }

    public void onReload() {

        unSpawnEventManagers();
    }

    public void onShutdown() {

        unSpawnEventManagers();
        stopEvent();
        deactivateEvent();
    }

    public static void SpawnNPCs(int npcId, int[][] locations, List<SimpleSpawner> list, int respawn) {
        NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        if (template == null) {
            System.out.println("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
            Thread.dumpStack();
        } else {
            int[][] var5 = locations;
            int var6 = locations.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                int[] location = var5[var7];
                SimpleSpawner sp = new SimpleSpawner(template);
                sp.setLoc(new Location(location[0], location[1], location[2]));
                sp.setAmount(1);
                sp.setRespawnDelay(respawn);
                sp.init();
                if (list != null) {
                    list.add(sp);
                }
            }

        }
    }

    private void spawnEventManagers() {
        int[][] CHESTS = FarmConfig.MONSTER_LOCS;
        SpawnNPCs(FarmConfig.PARTYDROP_MONSTER_ID, CHESTS, _ch_spawns, FarmConfig.PARTYDROP_MONSTER_RESPAWN);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(_ch_spawns);
    }

    public void onDeath(Creature cha, Creature killer) {
        if (_active && cha.getNpcId() == getBossId() && FarmConfig.ENABLEDROP) {
            if (((Player)killer).isInParty()) {
                List<Player> party = ((Player)killer).getParty().getPartyMembers();
                if (FarmConfig.ENABLEPARTYDROP) {
                    Iterator var4 = party.iterator();

                    label102:
                    while(true) {
                        while(true) {
                            Player member;
                            do {
                                while(true) {
                                    if (!var4.hasNext()) {
                                        break label102;
                                    }

                                    member = (Player)var4.next();
                                    if (FarmConfig.CHECKIP) {
                                        String pIp = member.getNetConnection().getHwid();
                                        if (!playerIps.containsKey(pIp)) {
                                            playerIps.put(pIp, 1);
                                            _canReward = true;
                                        } else {
                                            int count = (Integer)playerIps.get(pIp);
                                            if (count < 1) {
                                                playerIps.remove(pIp);
                                                playerIps.put(pIp, count + 1);
                                                _canReward = true;
                                            } else {
                                                member.sendMessage("Already 1 member of your pc have been rewarded, so this character won't be rewarded.");
                                                _canReward = false;
                                            }
                                        }
                                        break;
                                    }

                                    if (cha.getRealDistance3D(member) <= 1000.0) {
                                        Iterator var6 = FarmConfig.PARTY_DROP_REWARD_VALUE.iterator();

                                        while(var6.hasNext()) {
                                            int[] reward = (int[])var6.next();
                                            int count = Rnd.get(reward[1], reward[2]);
                                            if (Rnd.get(100) < reward[3]) {
                                                addItem(member, reward[0], (long)((int)(member.hasBonus() ? (float)count * member.getBonus().getDropItems() : (float)count)));
                                                member.broadcastPacket(new L2GameServerPacket[]{new PlaySound("ItemSound.quest_finish")});
                                            }
                                        }
                                    }
                                }
                            } while(!_canReward);

                            if (cha.getRealDistance3D(member) <= 1000.0) {
                                Iterator var15 = FarmConfig.PARTY_DROP_REWARD_VALUE.iterator();

                                while(var15.hasNext()) {
                                    int[] reward = (int[])var15.next();
                                    int count = Rnd.get(reward[1], reward[2]);
                                    if (Rnd.get(100) < reward[3]) {
                                        addItem(member, reward[0], (long)((int)(member.hasBonus() ? (float)count * member.getBonus().getDropItems() : (float)count)));
                                        member.broadcastPacket(new L2GameServerPacket[]{new PlaySound("ItemSound.quest_finish")});
                                    }
                                }
                            } else {
                                member.sendMessage("You are too far to be rewarded.");
                            }
                        }
                    }
                }

                playerIps.clear();
            } else if (FarmConfig.ENABLEINDIVIDUALDROP) {
                for(Iterator var10 = FarmConfig.INDIVIDUAL_DROP_ITEMS.iterator(); var10.hasNext(); killer.broadcastPacket(new L2GameServerPacket[]{new PlaySound("ItemSound.quest_finish")})) {
                    int[] reward = (int[])var10.next();
                    int count = Rnd.get(reward[1], reward[2]);
                    if (Rnd.get(100) < reward[3]) {
                        addItem((Player)killer, reward[0], (long)((int)(((Player)killer).hasBonus() ? (float)count * ((Player)killer).getBonus().getDropItems() : (float)count)));
                    }
                }
            } else {
                killer.sendMessage("No drop for you because you are not in party!");
            }
        }

    }

    public static int getBossId() {
        return FarmConfig.PARTYDROP_MONSTER_ID;
    }

    public String getNextTime() {
        return NextEvent.getTime() != null ? format.format(NextEvent.getTime()) : "Erro";
    }

    public void StartCalculationOfNextEventTime() {
        try {
            Calendar currentTime = Calendar.getInstance();
            long flush2 = 0L;
            int count = 0;
            String[] var8 = FarmConfig.EVENT_PartyDropStartTime;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String timeOfDay = var8[var10];
                Calendar testStartTime = Calendar.getInstance();
                testStartTime.setLenient(true);
                String[] splitTimeOfDay = timeOfDay.split(":");
                testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
                testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
                testStartTime.set(13, 0);
                if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    testStartTime.add(5, 1);
                }

                long timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
                if (count == 0) {
                    flush2 = timeL;
                    NextEvent = testStartTime;
                }

                if (timeL < flush2) {
                    flush2 = timeL;
                    NextEvent = testStartTime;
                }

                ++count;
            }

            _log.info("Party Farm: Next Event: " + NextEvent.getTime().toString());
            _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), flush2);
        } catch (Exception var13) {
            System.out.println("Party Farm: " + var13);
        }

    }

    protected static void waiter(long interval) {
        long startWaiterTime = System.currentTimeMillis();
        int seconds = (int)(interval / 1000L);

        while(startWaiterTime + interval > System.currentTimeMillis() && !_aborted) {
            --seconds;
            switch (seconds) {
                case 1:
                case 2:
                case 3:
                case 10:
                case 15:
                case 30:
                    if (_started) {
                        Announcements.getInstance().announceToAll(FarmConfig.COUNTDOWN_INITIAL_PHRASE + seconds + " " + FarmConfig.FINISH_COUNTDOWN_SECONDS, FarmConfig.COUNTDOWN_EVENT_CHAT);
                    }
                    break;
                case 60:
                case 120:
                case 180:
                case 240:
                case 300:
                case 600:
                case 900:
                case 1800:
                    if (_started) {
                        Announcements.getInstance().announceToAll(FarmConfig.COUNTDOWN_INITIAL_PHRASE + seconds / 60 + " " + FarmConfig.FINISH_COUNTDOWN_MINUTES, FarmConfig.COUNTDOWN_EVENT_CHAT);
                    }
                    break;
                case 3600:
                case 7200:
                case 10800:
                case 14400:
                case 18000:
                case 21600:
                case 43200:
                    if (_started) {
                        Announcements.getInstance().announceToAll(FarmConfig.COUNTDOWN_INITIAL_PHRASE + seconds / 60 / 60 + " " + FarmConfig.FINISH_COUNTDOWN_HOURS, FarmConfig.COUNTDOWN_EVENT_CHAT);
                    }
            }

            long startOneSecondWaiterStartTime = System.currentTimeMillis();

            while(startOneSecondWaiterStartTime + 1000L > System.currentTimeMillis()) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException var8) {
                }
            }
        }

    }

    public class StopTask extends RunnableImpl {
        public StopTask() {
        }

        public void runImpl() {
            if (FarmZone._active) {
                stopEvent();
            }
        }
    }

    public class StartTask extends RunnableImpl {
        public StartTask() {
        }

        public void runImpl() {
            if (FarmZone._active) {
                startEvent();
            }
        }
    }
}
