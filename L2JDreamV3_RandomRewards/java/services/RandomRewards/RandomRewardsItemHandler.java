package services.RandomRewards;

import handler.items.SimpleItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.MagicSkillUse;

import java.util.List;

public class RandomRewardsItemHandler extends SimpleItemHandler {
    private int item1;
    private static final int[] itemIDs;
    private static final int SKILL_VISUAL_EFF_ID = 2024;
    static {
        itemIDs = RandomRewardsConfig.EFFECT_ITEMS;
    }
    public static int findIndex(int[] arr, int t) {
        if (arr == null) {
            return -1;
        } else {
            int len = arr.length;

            for(int i = 0; i < len; ++i) {
                if (arr[i] == t) {
                    return i;
                }
            }
            return -1;
        }
    }
    public RandomRewardsItemHandler() {
    }
    public int[] getItemIds() {
        return itemIDs;
    }
    private static void skillToUse(Player player, ItemInstance item) {
        MagicSkillUse msu = new MagicSkillUse(player, player, SKILL_VISUAL_EFF_ID, 1, 500, 1500L);
        List<Player> players = World.getAroundPlayers(player);
    }
    protected boolean useItemImpl(Player player, ItemInstance item, boolean paramBoolean) {
      //  System.out.println("Used EffectOnUse Item");

        int indexItem = RandomRewardsAnswer.findIndex(RandomRewardsConfig.EFFECT_ITEMS, item.getItemId());

        ConfirmDlg confirmDlg = (ConfirmDlg)(new ConfirmDlg(SystemMsg.S1, -1)).addString("You will receive random rewards. Continue?");
        player.getPlayer().ask(confirmDlg, new RandomRewardsAnswer(player.getPlayer(), item.getItemId()));
       // player.broadcastPacket(new MagicSkillUse(player, player, SKILL_VISUAL_EFF_ID, 1, 500, 1500L));
        
        return false;
    }
}
