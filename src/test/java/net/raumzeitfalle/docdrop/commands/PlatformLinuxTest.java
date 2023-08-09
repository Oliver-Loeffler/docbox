package net.raumzeitfalle.docdrop.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlatformTest {

	@Test
	public void that_Linux_is_detected_from_osname() {
	    assertEquals(Platform.LINUX, Platform.from("linux"));
	}
	
	@Test
	public void that_Linux_is_detected_before_Windows_from_osname() {
	    assertEquals(Platform.LINUX, Platform.from("winux"));
	}
	
	@Test
	public void that_Windows_is_detected_from_osname() {
	    assertEquals(Platform.WINDOWS, Platform.from("windows"));
	}
	
	@Test
	public void that_Other_is_detected_from_osname() {
	    assertEquals(Platform.OTHER, Platform.from("other"));
	}

}
