package org.l2x6.pom.tuner.transform;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.l2x6.pom.tuner.PomTransformer.ContainerElement;
import org.l2x6.pom.tuner.PomTransformer.TransformationContext;
import org.l2x6.pom.tuner.PomTransformer.Transformer;

public interface modules {
    public static Transformer addIfNeeded(String module) {
        return addIfNeeded(null, null, Collections.singleton(module));
    }

    public static Transformer addIfNeeded(String profileId, String... modulePaths) {
        return addIfNeeded(profileId, null, Arrays.asList(modulePaths));
    }

    public static Transformer addIfNeeded(String module, Comparator<String> comparator) {
        return (TransformationContext context) -> {
            ContainerElement modules = context.getOrAddContainerElement("modules");
            context.addTextChildIfNeeded(modules, "module", module, comparator);
        };
    }

    public static Transformer addIfNeeded(String profileId, Collection<String> modulePaths) {
        return addIfNeeded(profileId, null, modulePaths);
    }
    public static Transformer addIfNeeded(String profileId, Comparator<String> comparator,
            Collection<String> modulePaths) {
        return (TransformationContext context) -> {
            final ContainerElement profileParent = context.getOrAddProfileParent(profileId);
            final ContainerElement modules = profileParent.getOrAddChildContainerElement("modules");
            for (String m : modulePaths) {
                if (comparator != null) {
                    context.addTextChildIfNeeded(modules, "module", m, comparator);
                } else {
                    modules.addChildTextElement("module", m);
                }
            }
        };
    }

}
