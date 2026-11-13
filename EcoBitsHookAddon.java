package org.ecobitshook;

import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import dev.drawethree.xprison.api.XPrisonAPI;
import dev.drawethree.xprison.api.addons.XPrisonAddon;
import dev.drawethree.xprison.api.addons.XPrisonAddonContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;
import java.util.logging.Logger;

/** Registers all EcoBits currencies, including additions/removals on EcoBits reload. */
public final class EcoBitsHookAddon implements XPrisonAddon {
    private XPrisonAPI api;
    private Logger log;
    private BukkitTask watcher;
    private final Map<String, EcoBitsXCurrency> registered = new HashMap<>();

    @Override public void onEnable(XPrisonAddonContext context) {
        api = context.getAPI(); log = context.getLogger();
        Plugin ecobits = Bukkit.getPluginManager().getPlugin("EcoBits");
        if (ecobits == null || !ecobits.isEnabled()) {
            log.warning("EcoBits is not enabled; no currencies registered. Restart after installing EcoBits.");
            return;
        }
        syncCurrencies();
        // EcoBits replaces Currency instances on reload; refresh wrappers as needed.
        watcher = Bukkit.getScheduler().runTaskTimer(context.getPlugin(), this::syncCurrencies, 100L, 100L);
    }

    private void syncCurrencies() {
        try {
            Map<String, Currency> live = new HashMap<>();
            for (Currency currency : Currencies.values()) live.put(currency.getId(), currency);
            for (Iterator<Map.Entry<String, EcoBitsXCurrency>> it = registered.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, EcoBitsXCurrency> entry = it.next();
                if (live.get(entry.getKey()) != entry.getValue().source()) {
                    api.getCurrencyApi().unregisterCurrency(entry.getValue());
                    it.remove();
                }
            }
            for (Map.Entry<String, Currency> entry : live.entrySet()) {
                if (registered.containsKey(entry.getKey())) continue;
                EcoBitsXCurrency hook = new EcoBitsXCurrency(entry.getValue());
                api.getCurrencyApi().registerCurrency(hook);
                registered.put(entry.getKey(), hook);
                log.info("Registered EcoBits currency with X-Prison: " + entry.getKey());
            }
        } catch (Exception ex) {
            log.warning("EcoBits currency synchronization failed: " + ex.getMessage());
        }
    }

    @Override public void onDisable() {
        if (watcher != null) watcher.cancel();
        if (api != null) for (EcoBitsXCurrency hook : registered.values())
            api.getCurrencyApi().unregisterCurrency(hook);
        registered.clear();
    }
}
