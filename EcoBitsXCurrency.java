package org.ecobitshook;

import com.willfp.ecobits.currencies.Currency;
import dev.drawethree.xprison.api.currency.model.XPrisonCurrency;
import dev.drawethree.xprison.api.currency.model.XPrisonCurrencyHandler;
import java.math.BigDecimal;

public final class EcoBitsXCurrency implements XPrisonCurrency {
    private final Currency currency;
    private final XPrisonCurrencyHandler handler;
    EcoBitsXCurrency(Currency currency) { this.currency = currency; this.handler = new EcoBitsBalanceHandler(currency); }
    Currency source() { return currency; }
    @Override public String getName() { return currency.getId(); }
    @Override public String getDisplayName() { return currency.getName(); }
    @Override public String getPrefix() { return currency.getPrefix(); }
    @Override public String getSuffix() { return currency.getSymbol(); }
    @Override public double getMaxAmount() { BigDecimal max = currency.getMax(); return max == null ? Double.MAX_VALUE : max.doubleValue(); }
    @Override public BigDecimal getMaxAmountExact() { return currency.getMax() == null ? BigDecimal.valueOf(Double.MAX_VALUE) : currency.getMax(); }
    @Override public BigDecimal getStartingAmountExact() { return currency.getDefault(); }
    @Override public double getStartingAmount() { return currency.getDefault().doubleValue(); }
    @Override public String getFormatPattern() { return currency.isDecimal() ? "#,##0.##" : "#,##0"; }
    @Override public boolean isShortFormat() { return false; }
    @Override public boolean isTrimZeros() { return true; }
    @Override public String format(double amount) { return currency.getSymbol() + " " + BigDecimal.valueOf(amount).stripTrailingZeros().toPlainString(); }
    @Override public XPrisonCurrencyHandler getHandler() { return handler; }
}
