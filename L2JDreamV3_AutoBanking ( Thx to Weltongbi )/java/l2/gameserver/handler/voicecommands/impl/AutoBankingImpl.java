package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.AutoBankingConfig;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;

import java.text.DecimalFormat;

public class AutoBankingImpl extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS = {"autogoldbar", "isAutoBanking", "goldbar", "deposit", "withdraw"};
    private static final Long ITEMS_MAX_AMOUNT = 2_147_483_647L; // max amount of items that can be deposited/withdrawn

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {

        if (!AutoBankingConfig.AUTO_BANKING_ENABLE || player == null) {
            return false;
        }

        if (AutoBankingConfig.AUTO_BANKING_AVAILABLE_ONLY_PREMIUM && !player.hasBonus()) {
            return false;
        }

        if (command.equalsIgnoreCase(COMMANDS[0]) || command.equalsIgnoreCase(COMMANDS[1])) {
            if (player.getVarBoolean("isAutoBanking")) {
                player.unsetVar("isAutoBanking");
                player.sendMessage(new CustomMessage("scripts.services.AutoBanking.deactivated", player));
                htmlBuilder(player);
                return false;
            } else {
                player.setVar("isAutoBanking", "true", -1);
                player.sendMessage(new CustomMessage("scripts.services.AutoBanking.activated", player));
                htmlBuilder(player);
                return true;
            }
        } else if (command.equalsIgnoreCase(COMMANDS[2])) {
            htmlBuilder(player);
            return true;
        } else if (command.equalsIgnoreCase(COMMANDS[3])) {
            boolean deposit = this.deposit(player);
            htmlBuilder(player);
            return deposit;
        } else if (command.equalsIgnoreCase(COMMANDS[4])) {
            boolean withdraw = this.withdraw(player);
            htmlBuilder(player);
            return withdraw;
        }
        return false;
    }

    private void htmlBuilder(Player player) {
        if (player != null && AutoBankingConfig.AUTO_BANKING_SHOW_PAGE) {
            String html = HtmCache.getInstance().getNotNull("scripts/services/banking/autobanking.htm", player);
            html = html.replace("%onOff%", player.getVarBoolean("isAutoBanking") ? "OFF" : "ON");
            html = html.replace("%enabled%", player.getVarBoolean("isAutoBanking") ? "<font color=\"00FF00\">Active</font>" : "<font color=\"FF0000\">Disabled</font>");
            html = html.replace("%goldbar%", String.valueOf(player.getInventory().getCountOf(AutoBankingConfig.AUTO_BANKING_ITEM_ID)));
            html = html.replace("%adena%", formatNumber(player.getInventory().getAdena()));
            html = html.replace("%adena_golbar%", formatNumber(AutoBankingConfig.AUTO_BANKING_ADENA_COUNT));
            html = html.replace("%deposit_command%", "bypass -h user_deposit");
            html = html.replace("%withdraw_command%", "bypass -h user_withdraw");
            Functions.show(html, player, null);

        }
    }

    private String formatNumber(long number) {
        DecimalFormat df = new DecimalFormat("#,###,###,###,###,###,##0");
        return df.format(number).replace(",", ".");
    }

    public boolean deposit(Player player) {
        if (getItemCount(player, 57) < AutoBankingConfig.AUTO_BANKING_ADENA_COUNT) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        } else {
            removeItem(player, 57, AutoBankingConfig.AUTO_BANKING_ADENA_COUNT);
            player.sendMessage(new CustomMessage("scripts.services.AutoBanking.deposit", player));
            addItem(player, AutoBankingConfig.AUTO_BANKING_ITEM_ID, 1L);
            return true;
        }
    }

    public boolean withdraw(Player player) {
        if (getItemCount(player, AutoBankingConfig.AUTO_BANKING_ITEM_ID) < 1L) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        } else if (getItemCount(player, 57) >= ITEMS_MAX_AMOUNT) {
            player.sendMessage(new CustomMessage("scripts.services.AutoBanking.withdraw", player).addNumber(ITEMS_MAX_AMOUNT));
            return false;
        } else {
            removeItem(player, AutoBankingConfig.AUTO_BANKING_ITEM_ID, 1L);
            player.sendMessage(new CustomMessage("scripts.services.AutoBanking.withdraw", player));
            addItem(player, 57, AutoBankingConfig.AUTO_BANKING_ADENA_COUNT);
            return true;
        }
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}
