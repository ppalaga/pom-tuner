package org.l2x6.pom.tuner.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.l2x6.pom.tuner.PomTransformer.TransformationContext;
import org.w3c.dom.Node;

public interface Siblings {
    static Function<Node, List<Node>> none() {
        return node -> Collections.emptyList();
    }

    static Function<Node, List<Node>> previousOrNext(Predicate<Node> nodeSelector) {
        return node -> {
            final List<Node> prev = previous(nodeSelector).apply(node);
            final List<Node> result = new ArrayList<>(prev);
            result.addAll(next(nodeSelector).apply(node));
            return Collections.unmodifiableList(result);
        };
    }

    static Function<Node, List<Node>> previous(Predicate<Node> nodeSelector) {
        return node -> {
            final List<Node> result = new ArrayList<>();
            Node prevSibling = node;
            while ((prevSibling = prevSibling.getPreviousSibling()) != null
                    && nodeSelector.test(node)) {
                result.add(prevSibling);
            }
            return Collections.unmodifiableList(result);
        };
    }

    static Function<Node, List<Node>> next(Predicate<Node> nodeSelector) {
        return node -> {
            final List<Node> result = new ArrayList<>();
            Node nextSibling = node;
            while ((nextSibling = nextSibling.getNextSibling()) != null
                    && nodeSelector.test(node)) {
                result.add(nextSibling);
            }
            return Collections.unmodifiableList(result);
        };
    }

    static Predicate<Node> comments() {
        return n -> n.getNodeType() == Node.COMMENT_NODE;
    }
    static Predicate<Node> commentsOrWhitespace() {
        return comments().or(whitespace());
    }
    static Predicate<Node> whitespace() {
        return TransformationContext::isWhiteSpaceNode;
    }
}
