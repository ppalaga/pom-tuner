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

import java.util.Objects;
import java.util.StringTokenizer;

/**
 * An immutable {@link #groupId}, {@link #artifactId}, {@link #version} triple with a fast {@link #hashCode()} and
 * {@link #equals(Object)}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Gav implements Comparable<Gav> {

    /**
     * Returns a new {@link Gav} instance parsed out of the given {@code gavString}.
     *
     * @param  gavString the string to parse, something of the form {@code groupId:artifactId:version}
     * @return           a new {@link Gav} instance parsed out of the given {@code gavString}
     */
    public static Gav of(String gavString) {
        StringTokenizer st = new StringTokenizer(gavString, ":");
        if (!st.hasMoreTokens()) {
            throw new IllegalStateException("Cannot parse '" + gavString + " to a " + Gav.class.getName()
                    + "; expected '<groupId>:<artifactId>:<version>', found too little segments");
        } else {
            final String g = st.nextToken();
            if (!st.hasMoreTokens()) {
                throw new IllegalStateException("Cannot parse '" + gavString + " to a " + Gav.class.getName()
                        + "; expected '<groupId>:<artifactId>:<version>', found too little segments");
            } else {
                final String a = st.nextToken();
                if (!st.hasMoreTokens()) {
                    throw new IllegalStateException("Cannot parse '" + gavString + " to a " + Gav.class.getName()
                            + "; expected '<groupId>:<artifactId>:<version>', found too little segments");
                } else {
                    final String v = st.nextToken();
                    if (st.hasMoreTokens()) {
                        throw new IllegalStateException("Cannot parse '" + gavString + " to a " + Gav.class.getName()
                                + "; expected '<groupId>:<artifactId>:<version>', found too many segments");
                    }
                    return new Gav(g, a, v);
                }
            }
        }
    }

    private final Ga ga;
    private final int hashCode;
    private final String version;

    public Gav(Ga ga, String version) {
        this.ga = ga;
        this.version = Gavtc.emptyToNull(version);
        this.hashCode = 31 * ga.hashCode() + (version == null ? 0 : version.hashCode());
    }

    public Gav(String groupId, String artifactId, String version) {
        this(new Ga(groupId, artifactId), version);
    }

    @Override
    public int compareTo(Gav o) {
        int result = this.ga.compareTo(o.ga);
        if (result != 0) {
            return result;
        } else {
            return Gavtc.SAFE_STRING_COMPARATOR.compare(version, o.version);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Gav other = (Gav) obj;
        return Objects.equals(this.version, other.version) && this.ga.equals(other.ga);
    }

    public String getArtifactId() {
        return ga.getArtifactId();
    }

    public String getGroupId() {
        return ga.getGroupId();
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    public StringBuilder toString(StringBuilder sb) {
        ga.toString(sb).append(':');
        if (version != null) {
            sb.append(version);
        }
        return sb;
    }

    public Ga toGa() {
        return ga;
    }

    public Gavtc toGavtc(String type, String classifier) {
        return new Gavtc(this, type, classifier);
    }

}
