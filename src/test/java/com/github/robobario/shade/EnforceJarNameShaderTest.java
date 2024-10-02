package com.github.robobario.shade;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnforceJarNameShaderTest {

    public static final String SHADED_JAR = "arbitrary";

    @Test
    public void isFilteredDoesNotFilterAnything() {
        EnforceJarNameShader.EnforceJarNameFilter filter = createEnforceFilter("abc", false);
        assertThat(filter.isFiltered("arbitrary")).isFalse();
    }

    @Test
    public void finishedDoesNothing() {
        EnforceJarNameShader.EnforceJarNameFilter filter = createEnforceFilter("abc", false);
        assertThatCode(filter::finished).doesNotThrowAnyException();
    }

    @Test
    public void canFilterConfiguredToAllowViolation() {
        EnforceJarNameShader.EnforceJarNameFilter filter = createEnforceFilter("good", false);
        assertThat(filter.canFilter(new File("bad"))).isFalse();
        assertThat(filter.canFilter(new File("good"))).isFalse();
        assertThatCode(filter::finished).doesNotThrowAnyException();
    }

    @Test
    public void canFilterConfiguredToThrowOnViolation() {
        boolean failOnViolation = true;
        EnforceJarNameShader.EnforceJarNameFilter filter = createEnforceFilter("abc", failOnViolation);
        filter.canFilter(new File("bad.jar"));
        assertThatThrownBy(filter::finished).isInstanceOf(RuntimeException.class)
                .hasMessage("compliance violation while shading " + SHADED_JAR + ": bad.jar does not contain abc");
    }

    @Test
    public void canFilterAllowsCompliantName() {
        EnforceJarNameShader.EnforceJarNameFilter filter = createEnforceFilter("expected", true);
        assertThat(filter.canFilter(new File("expected.jar"))).isFalse();
    }

    private static EnforceJarNameShader.EnforceJarNameFilter createEnforceFilter(String expectJarNameContains, boolean failOnViolation) {
        return new EnforceJarNameShader.EnforceJarNameFilter(expectJarNameContains, failOnViolation, new File(SHADED_JAR));
    }
}