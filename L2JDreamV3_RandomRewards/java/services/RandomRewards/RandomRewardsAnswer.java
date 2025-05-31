package services.RandomRewards;

import java.util.*;

import l2.commons.util.Rnd;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.utils.ItemFunctions;

public class RandomRewardsAnswer implements OnAnswerListener {
    private Player player1;
    private int item1;
    private static final int SKILL_VISUAL_EFF_ID = 2024;

    public RandomRewardsAnswer(Player player, int item) {
        player1 = player;
        item1 = item;
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

    public void sayYes() {
        int indexItem = findIndex(RandomRewardsConfig.EFFECT_ITEMS, item1);
        ItemFunctions.removeItem(player1, item1, 1L, true);
        player1.broadcastPacket(new MagicSkillUse(player1, player1, SKILL_VISUAL_EFF_ID, 1, 500, 1500L));

        for(Iterator items = RandomRewardsConfig.REWARDS_ITEMS.iterator(); items.hasNext();) {
            int[] reward = (int[]) items.next();
            int count = Rnd.get(reward[1], reward[2]);
            if (Rnd.get(100) < reward[3]) {
                addItem((Player) player1, reward[0], count);
            }
        }
    }

    public void sayNo() {
        player1.sendMessage("You must accept to receive rewards.");
    }


    public static void addItem(Playable playable, int itemId, long count) {
        ItemFunctions.addItem(playable, itemId, count, true);
    }

    public static long getItemCount(Playable playable, int itemId) {
        return ItemFunctions.getItemCount(playable, itemId);
    }

    public static long removeItem(Playable playable, int itemId, long count) {
        return ItemFunctions.removeItem(playable, itemId, count, true);
    }

    public static long removeItem(Playable playable, int itemId, long count, boolean descobrir) {
        return ItemFunctions.removeItem(playable, itemId, count, true, descobrir);
    }

}
