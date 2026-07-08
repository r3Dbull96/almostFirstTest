package ru.bulgakov.webshop.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class JenkinsTest {

    @Test
    @Tags({@Tag("UI"), @Tag("positiveJenkins")})
    @DisplayName("UI positive")
    void jenkinsTest1() {
        System.out.println("UI positive test");
    }

    @Test
    @DisplayName("UI positive")
    @Tags({@Tag("UI"), @Tag("negativeJenkins")})

    void jenkinsTest2() {
        System.out.println("UI negativeJenkins test");
    }

    @Test
    @DisplayName("API positiveJenkins")
    @Tags({@Tag("API"), @Tag("positiveJenkins")})
    void jenkinsTest3() {
        System.out.println("API positiveJenkins test");
    }

    @Test
    @DisplayName("API negativeJenkins")
    @Tags({@Tag("API"), @Tag("negativeJenkins")})
    void jenkinsTest4() {
        System.out.println("API negativeJenkins test");
    }
}
