package com.jsq.demo.ognl;

import java.util.Optional;

public interface RuleEngine<R, P> {


    /**
     * 根据参数匹配判断结果集是否命中
     *
     * @param p 参数
     * @return 匹配结果
     */
    Optional<R> match(P p);

}
