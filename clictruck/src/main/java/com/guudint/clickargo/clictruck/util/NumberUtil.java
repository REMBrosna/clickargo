package com.guudint.clickargo.clictruck.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class NumberUtil {

    private static Logger LOG = Logger.getLogger(NumberUtil.class);

    public static BigDecimal toBigDecimal(String value) {
       if (StringUtils.isBlank(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            LOG.error("Error toBigDecimal", e);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal toBigDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            LOG.error("Error toBigDecimal", e);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            LOG.error("Error toBigDecimal", e);
        }
        return BigDecimal.ZERO;
    }

    public static Integer toInteger(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            //return Integer.parseInt(value.toString());
        	return new Double(value.toString()).intValue();
        } catch (NumberFormatException e) {
            LOG.error("Error toInteger", e);
        }
        return 0;
    }

    public static String currencyFormat(String currency, BigDecimal value) {
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols fromatRp = new DecimalFormatSymbols();
        fromatRp.setCurrencySymbol(currency + " ");
        fromatRp.setMonetaryDecimalSeparator(',');
        fromatRp.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(fromatRp);
        String valueFormat = decimalFormat.format(toBigDecimal(value));
        if (!valueFormat.isEmpty()) {
            return valueFormat.split(",")[0];
        }
        return "";
    }

    public static double toDouble(BigDecimal value) {
        if (value == null) {
            return 0.0;
        }
        return value.doubleValue();
    }
}
