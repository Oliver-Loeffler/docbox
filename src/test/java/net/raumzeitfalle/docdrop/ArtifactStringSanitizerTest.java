package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.raumzeitfalle.docdrop.storage.Artifact;

public class ArtifactStringSanitizerTest {

    @Test
    void test() {
        var badString = " - {} * '' ! ___ \" \n^^//``\t"
                      + "....He    l lo+_|- ? :W\t&o?[]()rld .2024 _---  ";

        assertEquals("-_.Hello+_-World.2024_-",
                Artifact.sanitize(badString));
    }

}
