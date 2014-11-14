package com.advanchip.mobile_controlledlighting.test;

import com.advanchip.mobile_controlledlighting.utils.Utils;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

	public void testIsEmailValid() {
		assertEquals(true , Utils.isEmailValid("xyz@gmail.com"));
		assertEquals(false , Utils.isEmailValid("xyz"));
	}

	public void testIsPasswordValid() {
		fail("Not yet implemented");
	}

	public void testGetJsonObjectFromMap() {
		fail("Not yet implemented");
	}

}
