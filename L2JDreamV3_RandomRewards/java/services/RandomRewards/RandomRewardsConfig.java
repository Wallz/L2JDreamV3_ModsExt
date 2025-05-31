package services.RandomRewards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import l2.commons.configuration.ExProperties;

public class RandomRewardsConfig {
    protected static final Logger _log = Logger.getLogger(RandomRewardsConfig.class.getName());
    private static final String ECONFIG_FILE = "./config/events/RandomRewards.properties";
    public static boolean EFFECT_ENABLED;
    public static int[] EFFECT_ITEMS;
    public static List<int[]> REWARDS_ITEMS;

    public RandomRewardsConfig() {
    }

    public static int[] StringArrToIntArr(String[] s) {
        int[] result = new int[s.length];
        for(int i = 0; i < s.length; ++i) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    public static void load() {
        ExProperties cfg = initProperties(ECONFIG_FILE);
        EFFECT_ENABLED = cfg.getProperty("Enable", false);
        EFFECT_ITEMS = StringArrToIntArr(cfg.getProperty("EffectItemIDs").split(","));
        REWARDS_ITEMS = new ArrayList();
        String[] Reward;
        int var4;
        Reward = cfg.getProperty("Rewards", "57,100,100,100").split(";");
        String[] var11 = Reward;
        var4 = Reward.length;
        for(int var12 = 0; var12 < var4; ++var12) {
            String reward = var11[var12];
            String[] rewardSplit = reward.split(",");
            if (rewardSplit.length == 4) {
                try {
                    REWARDS_ITEMS.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]), Integer.parseInt(rewardSplit[2]), Integer.parseInt(rewardSplit[3])});
                } catch (NumberFormatException var9) {
                    if (!reward.isEmpty()) {
                    }
                }
            }
        }
    }

    public static ExProperties initProperties(String filename) {
        ExProperties result = new ExProperties();
        try {
            result.load(new File(filename));
        } catch (IOException var3) {
            _log.warning("Random Rewards: Error loading \"" + filename + "\" config.");
        }
        return result;
    }
}
