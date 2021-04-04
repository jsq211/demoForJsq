package com.jsq.demo.ognl;

import ognl.MapPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.text.NumberFormat;
import java.util.Map;

public class InvoiceOgnlRuleEngine extends AbstractOgnlRuleEngine<Boolean, Object> {


    public InvoiceOgnlRuleEngine(String matchRule, Boolean integer) {
        super(matchRule, integer);
    }


    static {
        OgnlRuntime.setPropertyAccessor(Map.class, new ParamPropertiesAccessor());
    }

    /**
     * 自定义表达式方法解析
     */
    static class ParamPropertiesAccessor extends MapPropertyAccessor {

        /**
         * 订单完成时间
         */
        static final String DONE_TIME_KEY = "orderDoneTime";

        static NumberFormat formatter = NumberFormat.getNumberInstance();

        static {
            formatter.setMinimumIntegerDigits(3);
            formatter.setGroupingUsed(false);
        }

        @Override
        public Object getProperty(Map context, Object target, Object name) throws OgnlException {

            if (DONE_TIME_KEY.equals(name)) {
                Object orderDoneTime = ((Map) target).get(name);
                if (orderDoneTime == null) {
                    return null;
                }
                String orderDoneTimeStr = orderDoneTime.toString();
                int unitIndex = orderDoneTimeStr.length() - 1;
                int time = Integer.parseInt(orderDoneTimeStr.substring(0, unitIndex));
                String unit = orderDoneTimeStr.substring(unitIndex);
                switch (unit) {
                    case "d": // 天

                        break;
                    case "M": // 月
                    default: // 月

                }
                // 时间如需定义到规则里，在这里自定义
                return time;
            }
            return super.getProperty(context, target, name);
        }
    }

}
