package mil.af.abms.midas.helpers;

import org.springframework.beans.BeanUtils;

import mil.af.abms.midas.api.helper.Builder;

public class TestUtil {

    @SuppressWarnings(value = "unchecked")
    public static <T> T clone(T original) {
        T clone = (T) Builder.build(original.getClass()).get();
        BeanUtils.copyProperties(original, clone);

        return clone;
    }
}
