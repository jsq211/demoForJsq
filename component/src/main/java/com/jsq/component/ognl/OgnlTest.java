package com.jsq.component.ognl;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author jsq
 * created on 2021/3/3
 **/
public class OgnlTest {
    public static void main(String[] args) {
        ArrayList<InvoiceOgnlRuleEngine> engines = Lists.newArrayList(
                new InvoiceOgnlRuleEngine("(fromDepositId > 21 || fromDepositId <= 20) && toCountry != '86' && settlementCurrency == 'CNY'", true),
                new InvoiceOgnlRuleEngine("(fromDepositId != 22 && toCountry != '55380') or (fromDepositId == 1 && toCountry == '55763')", true)
        );
        HashMap<String, Object> map = new HashMap<>();
        map.put("fromDepositId", 203);
        map.put("toCountry", "826");
        map.put("settlementCurrency", "CNY");

        engines.forEach(e -> {
            Optional<Boolean> res = e.match(map);
            if (res.isPresent()){
                System.out.println(res.get());
            }else {
                System.out.println("false");
            }
        });
    }
}
