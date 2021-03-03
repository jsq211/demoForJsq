package com.jsq.component.ognl;

import ognl.*;

import java.util.Collections;

public class OgnlSupport {

    public static OgnlContext getContext(Object root) {
        return (OgnlContext) Ognl.createDefaultContext(root, new OgnlMemberAccess(), new DefaultClassResolver(), new DefaultTypeConverter());
    }

    public static OgnlContext getContext() {
        return (OgnlContext) Ognl.createDefaultContext(Collections.emptyMap(), new OgnlMemberAccess(), new DefaultClassResolver(), new DefaultTypeConverter());
    }

    public static Object getValue(Object expression, OgnlContext context) throws OgnlException {

        return Ognl.getValue(expression, context, context.getRoot());

    }

    public static Object getRootValue(Object expression, Object root) throws OgnlException {

        return Ognl.getValue(expression, getContext(), root);

    }

    public static boolean match(Object expression, Object root) throws OgnlException {

        return (boolean) getRootValue(expression, root);
    }

}
