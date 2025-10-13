package org.l2x6.pom.tuner.transform;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.l2x6.pom.tuner.PomTransformer.ContainerElement;
import org.l2x6.pom.tuner.PomTransformer.TextElement;
import org.l2x6.pom.tuner.PomTransformer.TransformationContext;
import org.l2x6.pom.tuner.PomTransformer.Transformer;
import org.w3c.dom.Node;

public interface properties {
    public static Transformer addOrSet(String name, String value) {
        return addOrSet(name, value, null, null);
    }

    public static Transformer addOrSet(String name, String value, Comparator<Map.Entry<String, String>> comparator) {
        return addOrSet(name, value, null, null);
    }

    public static Transformer addOrSet(String name, String value, String profileId, Comparator<Map.Entry<String, String>> comparator) {
        return (TransformationContext context) -> {
            final ContainerElement profileParent = context.getOrAddProfileParent(profileId);
            final ContainerElement props = profileParent.getOrAddChildContainerElement("properties");
            props.addChildTextElementIfNeeded(name, value, comparator);
        };
    }

    public static Transformer remove(Predicate<Map.Entry<String, String>> propertySelector, Predicate<String> profilesSelector, Function<Node, List<Node>> siblingsSelector) {
        return (TransformationContext context) -> {

            context.getProfiles().stream()
            .filter(profile -> profile.childTextElementsStream().anyMatch(textElement -> textElement.getNode().getLocalName().equals("id") && profilesSelector.test(textElement.getNode().getTextContent())))
            .map(profile -> profile.getChildContainerElement("properties"))
            .filter(Optional::isPresent)
            .map(Optional::get)
            ;
            if (profilesSelector.test(null)) {
                context
                    .getContainerElement("properties")
                    .ifPresent(props -> {
                        props.childTextElementsStream()
                            .filter(prop -> propertySelector.test(new AbstractMap.SimpleImmutableEntry<String, String>(prop.getNode().getLocalName(), prop.getNode().getTextContent())))
                            .forEach(prop -> prop.remove(siblingsSelector))
                        ;
                    });
            }

            final ContainerElement profileParent = context.getOrAddProfileParent(profileId);
            final ContainerElement props = profileParent.getOrAddChildContainerElement("properties");
            props.childTextElementsStream()
                .filter(m -> modulePath.equals(m.getNode().getTextContent()))
                .findFirst()
                .ifPresent(m -> m.remove(removePrecedingComments, removePrecedingWhitespace));
        };
    }

    static Map.Entry<String, String> toEntry(Node n) {
        return ;
    }

    public static Transformer removeAll() {
        return (TransformationContext context) -> {
            final ContainerElement profileParent = context.getOrAddProfileParent(profileId);
            final ContainerElement props = profileParent.getOrAddChildContainerElement("properties");
            props.childTextElementsStream()
                .filter(m -> modulePath.equals(m.getNode().getTextContent()))
                .findFirst()
                .ifPresent(m -> m.remove(removePrecedingComments, removePrecedingWhitespace));
        };
    }

}
