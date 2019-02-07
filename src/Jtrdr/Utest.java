package Jtrdr;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.JUnitCore;

class Utest {

	@Test
	void testTimestamp2weekday() {
		JUnitCore test= new JUnitCore();
		int result=U.timestamp2weekday(1548007776731L); //2019.01.20T18:09:36.731
		assertEquals(1,result);
		result=U.timestamp2weekday(1547834976731L); //2019.01.18T18:09:36.731
		assertEquals(6,result);
		
	}

}
