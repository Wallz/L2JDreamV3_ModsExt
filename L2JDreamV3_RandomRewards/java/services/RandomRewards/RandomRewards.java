package services.RandomRewards;

import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomRewards extends Functions implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(RandomRewards.class);

    public RandomRewards() {
    }

    public void onLoad() {
        RandomRewardsConfig.load();
        if (RandomRewardsConfig.EFFECT_ENABLED)
        {   _log.info("[Random Rewards]:Loaded Random Rewards");
        }
        else
        _log.info("[Random Rewards]: Disabled");

    }

    public void onReload() {
        onLoad();
    }

    public void onShutdown() {
    }
}
