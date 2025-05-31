package services.Banking;


import l2.gameserver.AutoBankingConfig;
import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.handler.voicecommands.impl.AutoBankingImpl;
import l2.gameserver.listener.actor.player.OnItemPickupListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoBanking extends Functions implements ScriptFile {
    public static final Logger LOG = LoggerFactory.getLogger(AutoBanking.class);
    private static final String DISPLAY_NAME = "AutoBanking";
    private static final CreateGolbarListenerImpl CGLI = new CreateGolbarListenerImpl();

    @Override
    public void onLoad() {
        AutoBankingConfig.load();
        if (AutoBankingConfig.AUTO_BANKING_ENABLE) {
            LOG.info( DISPLAY_NAME + " Service: activated");
            CharListenerList.addGlobal(CGLI);
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new AutoBankingImpl());
        } else {
            LOG.info( DISPLAY_NAME + " Service: deactivated");
        }

    }

    @Override
    public void onReload() {
        onLoad(); //reload configs
    }

    @Override
    public void onShutdown() {
        if (AutoBankingConfig.AUTO_BANKING_ENABLE) {
            CharListenerList.removeGlobal(CGLI);
            LOG.info(DISPLAY_NAME + " Service: shutdown successfully.");
        }
    }

    private static class CreateGolbarListenerImpl implements OnItemPickupListener {

        @Override
        public void onItemPickup(Player player, ItemInstance itemInstance) {
            if (AutoBankingConfig.AUTO_BANKING_AVAILABLE_ONLY_PREMIUM && !player.hasBonus()){
                return;
            }
            if (player.getVarBoolean("isAutoBanking")) {
                if (itemInstance.getItemId() == 57 && player.getInventory().getAdena() >= AutoBankingConfig.AUTO_BANKING_ADENA_COUNT) {
                    removeItem(player, 57, AutoBankingConfig.AUTO_BANKING_ADENA_COUNT);
                    addItem(player, AutoBankingConfig.AUTO_BANKING_ITEM_ID, 1L);
                }
            }
        }
    }
}
