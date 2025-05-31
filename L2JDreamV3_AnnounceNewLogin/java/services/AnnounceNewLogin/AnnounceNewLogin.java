package services.AnnounceNewLogin;

import l2.gameserver.Announcements;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnounceNewLogin
        extends Functions
        implements ScriptFile, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(AnnounceNewLogin.class);

    public void onLoad() {
        CharListenerList.addGlobal(this);
        _log.info("Loaded ext: AnnounceNewLogin");
    }

    public void onPlayerEnter(Player player) {
        if (player.getLevel() >= 1 && player.getLevel() <= 5 && player.getOnlineTime() == 0L) {
            String playerName = player.getName();
            String welcomeMessage = ">" + playerName + "< : * Welcome New Player!";
            Announcements.getInstance().announceToAll(welcomeMessage, ChatType.COMMANDCHANNEL_ALL);
            System.out.println(">" + playerName + "< : New Player!");
        }

    }

    public void onReload() {
        onLoad();
    }

    public void onShutdown() {
        CharListenerList.removeGlobal(this);
    }
}
