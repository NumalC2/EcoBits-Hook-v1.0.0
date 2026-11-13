package org.ecobitshook;

import com.willfp.ecobits.currencies.Currency;
import com.willfp.ecobits.currencies.CurrencyUtils;
import dev.drawethree.xprison.api.currency.enums.LostCause;
import dev.drawethree.xprison.api.currency.enums.ReceiveCause;
import dev.drawethree.xprison.api.currency.model.XPrisonCurrencyHandler;
import org.bukkit.OfflinePlayer;
import java.math.BigDecimal;

/** Delegates balances to EcoBits; no duplicate currency storage. */
public final class EcoBitsBalanceHandler implements XPrisonCurrencyHandler {
    private final Currency currency;
    EcoBitsBalanceHandler(Currency currency) { this.currency = currency; }
    // Integer-only currencies cannot store fractional amounts. Round debits UP
    // so purchases are never undercharged; round credits/assignments DOWN.
    private BigDecimal debit(BigDecimal amount) {
        return currency.isDecimal() ? amount : amount.setScale(0, java.math.RoundingMode.CEILING);
    }
    private BigDecimal credit(BigDecimal amount) {
        return currency.isDecimal() ? amount : amount.setScale(0, java.math.RoundingMode.FLOOR);
    }
    private boolean valid(BigDecimal amount) {
        return amount != null && amount.signum() >= 0;
    }
    private BigDecimal fromDouble(double amount) {
        return Double.isFinite(amount) ? BigDecimal.valueOf(amount) : null;
    }
    @Override public double getBalance(OfflinePlayer player) { return getBalanceExact(player).doubleValue(); }
    @Override public BigDecimal getBalanceExact(OfflinePlayer player) { return CurrencyUtils.getBalance(player, currency); }
    @Override public boolean has(OfflinePlayer player, double amount) { return has(player, fromDouble(amount)); }
    @Override public boolean has(OfflinePlayer player, BigDecimal amount) {
        return valid(amount) && getBalanceExact(player).compareTo(debit(amount)) >= 0;
    }
    @Override public boolean setBalance(OfflinePlayer player, double amount) { return setBalance(player, fromDouble(amount)); }
    @Override public boolean setBalance(OfflinePlayer player, BigDecimal amount) {
        if (!valid(amount)) return false;
        BigDecimal target = credit(amount);
        if (currency.getMax() != null && target.compareTo(currency.getMax()) > 0) return false;
        return CurrencyUtils.changeBalance(player, currency, old -> target);
    }
    @Override public boolean addBalance(OfflinePlayer player, double amount, ReceiveCause cause) {
        return addBalance(player, fromDouble(amount), cause);
    }
    @Override public boolean addBalance(OfflinePlayer player, BigDecimal amount, ReceiveCause cause) {
        if (!valid(amount)) return false;
        BigDecimal delta = credit(amount);
        return CurrencyUtils.changeBalance(player, currency, old -> {
            BigDecimal next = old.add(delta);
            return currency.getMax() != null && next.compareTo(currency.getMax()) > 0 ? null : next;
        });
    }
    @Override public boolean removeBalance(OfflinePlayer player, double amount, LostCause cause) {
        return removeBalance(player, fromDouble(amount), cause);
    }
    @Override public boolean removeBalance(OfflinePlayer player, BigDecimal amount, LostCause cause) {
        if (!valid(amount)) return false;
        BigDecimal delta = debit(amount);
        return CurrencyUtils.changeBalance(player, currency, old -> old.compareTo(delta) < 0 ? null : old.subtract(delta));
    }
}
