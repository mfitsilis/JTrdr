package Jtrdr;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/**
 * calculate the market holidays for a given year
 */
public class Holidays {
	private ArrayList<String> holidays= new ArrayList<>();
	/**
	 * @param year
	 * Mon is 2
	 * month starts at 0                                                                             
	 * nth day starts at 1                                                                           
	 * mlk day(3rd Mo Jan),wash.birthday(3rd Mo Feb),memorial day(last Mo May),labor day(1st Mo Sept)
	 * 1st Jan, 4th July, 25th Dec, 4th Fri Nov ...todo observed holiday                             
	 * good Fri needs own function
	 * mlk 1,0,3 - wash 1,1,3 - mem - 1,4,4/5 - lab 1,8,1 - thank 5,10,4                                                                   
	 */                             
	public Holidays(int year){
		holidays.add(year+".01.01");//todo shift if day is during wknd...
		year=2016;
		Date d1=getHolDate(2,0,year,3);
		Date d2=getHolDate(2,1,year,3);
		Date d3=getHolDate(2,4,year,5);
		Date d4=getHolDate(2,8,year,1);
		if((new SimpleDateFormat("MM").format(d3)).equals("06")) //if 5th Mon is in June take 4th 
			d3=getHolDate(2,4,year,4);
		Date d5=getHolDate(5,10,year,4);
		holidays.add(new SimpleDateFormat(U.dateformat).format(d1));  
		holidays.add(new SimpleDateFormat(U.dateformat).format(d2));  
		String easter=getGoodFridayDate(year);
		holidays.add(easter);
		holidays.add(new SimpleDateFormat(U.dateformat).format(d3));  
		holidays.add(year+".07.04");
		holidays.add(new SimpleDateFormat(U.dateformat).format(d4));  
		holidays.add(new SimpleDateFormat(U.dateformat).format(d5));  		
		holidays.add(year+".12.25");
	}
	/**
	 * https://stackoverflow.com/questions/39528331/get-third-friday-of-a-month
	 */
	public Date getHolDate(int wd,int m,int y,int num){
		return nthWeekdayOfMonth(wd, m, y, num, TimeZone.getTimeZone("America/New York"));
	}
	public Date nthWeekdayOfMonth(int dayOfWeek, int month, int year, int week, TimeZone timeZone) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeZone(timeZone);
	    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
	    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, week);
	    calendar.set(Calendar.MONTH, month);
	    calendar.set(Calendar.YEAR, year);
	    return calendar.getTime();
	}	
	//https://stackoverflow.com/questions/26022233/calculate-the-date-of-easter-sunday
	public String getGoodFridayDate(int year)
    {
        int a = year % 19,
            b = year / 100,
            c = year % 100,
            d = b / 4,
            e = b % 4,
            g = (8 * b + 13) / 25,
            h = (19 * a + b - d - g + 15) % 30,
            j = c / 4,
            k = c % 4,
            m = (a + 11 * h) / 319,
            r = (2 * e + 2 * j - k - h + m + 32) % 7,
            n = (h - m + r + 90) / 25,
            p = (h - m + r + n + 19) % 32;

        if(p==0){
        	p=31;n--; 
        }
        else if(p==1){
        	p=30;n--;
        }
        else {
        	p=p-2;
        }
        return String.format("%d.%02d.%02d",year,n,p);
    }
	public ArrayList<String> holidays(){
		return holidays;
	}
}
