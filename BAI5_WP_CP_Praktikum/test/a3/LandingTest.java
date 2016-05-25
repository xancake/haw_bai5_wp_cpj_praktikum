package a3;

import static org.junit.Assert.*;

import org.junit.Test;

public class LandingTest {
	@Test
	public void testBerechneRichtung_6Landings_0to0() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_0to1() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_0to2() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_0to3() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_0to4() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_0to5() {
		Landing start = new Landing(0, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to0() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to1() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to2() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to3() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to4() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_1to5() {
		Landing start = new Landing(1, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to0() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to1() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to2() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to3() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to4() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_2to5() {
		Landing start = new Landing(2, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to0() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to1() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to2() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to3() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to4() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_3to5() {
		Landing start = new Landing(3, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to0() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to1() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to2() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to3() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to4() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_4to5() {
		Landing start = new Landing(4, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to0() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(0, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to1() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(1, 0, 6);
		assertEquals(Richtung.IM_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to2() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(2, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to3() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(3, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to4() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(4, 0, 6);
		assertEquals(Richtung.GEGEN_UHRZEIGERSINN, Landing.berechneRichtung(start, ziel, 6));
	}
	
	@Test
	public void testBerechneRichtung_6Landings_5to5() {
		Landing start = new Landing(5, 0, 6);
		Landing ziel = new Landing(5, 0, 6);
		assertEquals(null, Landing.berechneRichtung(start, ziel, 6));
	}
}
