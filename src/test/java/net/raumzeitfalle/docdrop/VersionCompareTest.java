package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

import org.junit.jupiter.api.Test;

class VersionCompareTest {

    @Test
    void test1() {
//        Semver oldest = new Semver("v20230101.12", SemverType.LOOSE);
        Semver oldie  = new Semver("20230101.12", SemverType.LOOSE);
        Semver oldest = new Semver("v20230101.12", SemverType.NPM);
        Semver latest = new Semver("20230101.13", SemverType.LOOSE);
        
        assertTrue(latest.compareTo(oldest) > 0);
        assertEquals(oldie, oldest);
    }
    
    @Test
    void test2() {

      Semver oldie  = new Semver("20230101.12", SemverType.LOOSE);
      Semver oldest = new Semver("20230101.12", SemverType.LOOSE);
      Semver latest = new Semver("20230101.13");
      
      assertTrue(latest.compareTo(oldest) > 0);
      assertEquals(oldie, oldest);
  }

}
