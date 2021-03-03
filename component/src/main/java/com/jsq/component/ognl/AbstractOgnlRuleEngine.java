package com.jsq.component.ognl;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.Optional;

@Slf4j
public abstract class AbstractOgnlRuleEngine<R, P> implements RuleEngine<R, P> {

    private Object matchExpression;

    private final R result;

    public AbstractOgnlRuleEngine(String matchRule, R r) {

        try {
            this.matchExpression = Ognl.parseExpression(matchRule);
            result = r;
        } catch (OgnlException e) {
            log.error(matchRule, e);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Optional<R> match(P p) {
        try {
            return OgnlSupport.match(matchExpression, p)
                    ? Optional.of(result)
                    : Optional.empty();
        } catch (Exception e) {
            log.error("", e);
            throw new ApiException("match expression error");
        }
    }


}
