/*
 * Copyright (c) 2015 Maven Utilities Project
 * project contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.l2x6.pom.tuner.model;

import java.util.Comparator;
import java.util.function.Predicate;
import org.l2x6.pom.tuner.Comparators;

/**
 * A Maven artifact defined by {@code groupId}, {@code artifactId}, {@code version}, {@code type} and
 * {@code classifier}.
 *
 * @since 4.8.0
 */
public class Gavtc {
    static final String DEFAULT_TYPE = "jar";
    static final Comparator<String> TYPE_COMPARATOR = (a, b) -> (a == null ? "jar" : a)
            .compareTo(b == null ? DEFAULT_TYPE : b);
    static final Comparator<String> SAFE_STRING_COMPARATOR = Comparators.safeStringComparator();

    static final Comparator<Gavtc> GROUP_FIRST_COMPARATOR = Comparator
            .comparing(Gavtc::getGroupId, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getArtifactId, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getVersion, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getType, TYPE_COMPARATOR)
            .thenComparing(Gavtc::getClassifier, SAFE_STRING_COMPARATOR);

    static final Comparator<Gavtc> TYPE_FIRST_COMPARATOR = Comparator
            .comparing(Gavtc::getType, TYPE_COMPARATOR)
            .thenComparing(Gavtc::getGroupId, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getArtifactId, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getVersion, SAFE_STRING_COMPARATOR)
            .thenComparing(Gavtc::getClassifier, SAFE_STRING_COMPARATOR);

    public static Gavtc of(String rawGavtcs) {
        String[] gavtcArr = rawGavtcs.split(":");
        if (gavtcArr.length < 3) {
            throw new IllegalStateException("Cannot parse '" + rawGavtcs + " to a " + Gavtc.class.getName()
                    + "; expected '<groupId>:<artifactId>:<version>[:<type>[:<classifier>]]', found too little segments");
        }
        if (gavtcArr.length > 5) {
            throw new IllegalStateException("Cannot parse '" + rawGavtcs + " to a " + Gavtc.class.getName()
                    + "; expected '<groupId>:<artifactId>:<version>[:<type>[:<classifier>]]', found too many segments");
        }
        int i = 0;
        final String groupId = gavtcArr[i++];
        final String artifactId = gavtcArr[i++];
        final String version = gavtcArr[i++];
        final String type = i < gavtcArr.length ? emptyToNull(gavtcArr[i++]) : null;
        final String classifier = i < gavtcArr.length ? emptyToNull(gavtcArr[i++]) : null;
        return new Gavtc(groupId, artifactId, version, type, classifier);
    }

    public static Comparator<Gavtc> groupFirstComparator() {
        return GROUP_FIRST_COMPARATOR;
    }

    public static Comparator<Gavtc> typeFirstComparator() {
        return TYPE_FIRST_COMPARATOR;
    }

    public static Predicate<Gavtc> equalGroupIdAndArtifactId(String groupId, String artifactId) {
        return gavtcs -> groupId.equals(gavtcs.getGroupId()) && artifactId.equals(gavtcs.getArtifactId());
    }

    static String toEffectiveType(String type) {
        return type == null || type.isEmpty() ? DEFAULT_TYPE : type;
    }

    static String emptyToNull(String string) {
        return string != null && !string.isEmpty() ? string : null;
    }

    private final Gav gav;
    private final String type;
    private final String classifier;
    private final int hashCode;

    public Gavtc(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null, null);
    }

    public Gavtc(String groupId, String artifactId, String version, String type, String classifier) {
        this(new Gav(groupId, artifactId, version), type, classifier);
    }

    public Gavtc(Ga ga, String version, String type, String classifier) {
        this(ga.toGav(version), type, classifier);
    }

    public Gavtc(Gav gav, String type, String classifier) {
        this.gav = gav;
        this.type = type == null || type.isEmpty() ? null : type;
        this.classifier = classifier == null || classifier.isEmpty() ? null : classifier;

        final String useType = DEFAULT_TYPE.equals(type) ? null : type;
        int h = 31 * gav.hashCode() + ((classifier == null) ? 0 : classifier.hashCode());
        h = 31 * h + ((useType == null) ? 0 : useType.hashCode());
        this.hashCode = h;
    }

    public String getGroupId() {
        return gav.getGroupId();
    }

    public String getArtifactId() {
        return gav.getArtifactId();
    }

    public String getVersion() {
        return gav.getVersion();
    }

    public String getType() {
        return type;
    }

    public String getClassifier() {
        return classifier;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    public StringBuilder toString(StringBuilder sb) {
        gav.toString(sb);
        if (type != null || classifier != null) {
            sb.append(':');
            if (type != null) {
                sb.append(type);
            }
            if (classifier != null) {
                sb.append(':');
                if (classifier != null) {
                    sb.append(classifier);
                }
            }
        }
        return sb;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Gavtc))
            return false;
        Gavtc other = (Gavtc) obj;
        if (!gav.equals(other.gav)) {
            return false;
        }
        if (classifier == null) {
            if (other.classifier != null)
                return false;
        } else if (!classifier.equals(other.classifier))
            return false;
        final String useType = DEFAULT_TYPE.equals(type) ? null : type;
        final String useOtherType = DEFAULT_TYPE.equals(other.type) ? null : other.type;
        if (useType == null) {
            if (useOtherType != null)
                return false;
        } else if (!useType.equals(useOtherType))
            return false;
        return true;
    }

    /**
     * @return a new {@link Ga} created out of this {@link Gavtc}'s {@link #groupId} and {@link #artifactId}
     */
    public Ga toGa() {
        return gav.toGa();
    }

    public Gav toGav() {
        return gav;
    }

    public Gavtcs toGavtcs(String scope) {
        return new Gavtcs(this, scope);
    }

    public Gavtcs toGavtcs(String scope, Ga exclusion) {
        return new Gavtcs(this, scope, exclusion);
    }

}
