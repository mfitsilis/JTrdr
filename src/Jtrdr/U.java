package Jtrdr;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jfree.data.time.Millisecond;

import io.reactivex.Flowable;
import kiss.util.As;
import kx.c.KException;

/** contains utility methods and variables as static */
public class U {
	final static Logger logger=Logger.getLogger(U.class);

	public static final String dateformat="yyyy.MM.dd";
    public static final String timeformatsec="HH:mm:ss";
	public static final String datetimeformat="yyyy.MM.dd HH:mm:ss";
		
	/** no outofbounds error - returns 0 or last element if out of bounds */
	public static <T> T get(ArrayList<T> al,int i){
        if (al.size()==0) { logger.fatal("array size is 0"); exit(); return null; }
        else if (i<0)
        	return al.get(i);
        else if (i>=al.size())
        	return last(al);
        else 
        	return al.get(i);
    }
	public static <T> T last(ArrayList<T> al){
        return al.get(al.size()-1);
    }
	public static <T> T last(ArrayList<T> al,int i){ //last(object,0) same as last(object)
        return al.get(al.size()-1-i);
    }
	public static <T> T first(ArrayList<T> al){
        return al.get(0);
    }
	/** incrementing by one */
	public static void inc(ArrayList<Integer> al,int i){
		int value = al.get(i)+1;
		al.set(i, value);
    }
	/** decrementing by one */
	public static void dec(ArrayList<Integer> al,int i){
		int value = al.get(i)-1;
		al.set(i, value);
    }
    
	public static long round2min(long ts) {
        return 60000*(ts/60000);
    }
    public static long round2sec(long ts) {
        return 1000*(ts/1000);
    }
    public static String ts2str(long ts) {
        return DateTimeFormatter.ofPattern(timeformatsec).withZone(ZoneId.of( "America/New_York" )).format(Instant.ofEpochMilli(ts));
    }
    public static long timestampNow() { //for live or simulated live trading
        return LocalDateTime.now().atZone(ZoneId.of( "America/New_York" )).toInstant().toEpochMilli();
    }
    public static String timestamp2datestr(long ts,boolean flag) {
		if(flag) //pb
			return DateTimeFormatter.ofPattern(dateformat).withZone(ZoneOffset.UTC).format(Instant.ofEpochMilli(ts));
		else
			return DateTimeFormatter.ofPattern(dateformat).withZone(ZoneId.of( "America/New_York" )).format(Instant.ofEpochMilli(ts));
    }
	public static String timestamp2timestr(long ts,boolean flag) {
		if(flag) //pb
			return DateTimeFormatter.ofPattern(timeformatsec).withZone(ZoneOffset.UTC).format(Instant.ofEpochMilli(ts));
		else
			return DateTimeFormatter.ofPattern(timeformatsec).withZone(ZoneId.of( "America/New_York" )).format(Instant.ofEpochMilli(ts));
    }
    public static String timestamp2datetimestr(long ts,boolean flag) {
		if(flag) //pb
			return DateTimeFormatter.ofPattern(datetimeformat).withZone(ZoneOffset.UTC).format(Instant.ofEpochMilli(ts));
		else
			return DateTimeFormatter.ofPattern(datetimeformat).withZone(ZoneId.of( "America/New_York" )).format(Instant.ofEpochMilli(ts));
    }	
    public static LocalDate timestamp2date(long ts,boolean flag) {
		if(flag) //pb
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneOffset.UTC).toLocalDate();
		else
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).toLocalDate();   
    }
    public static int timestamp2currentdayofyear(long ts,boolean flag) {
		if(flag) //pb
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneOffset.UTC).getDayOfYear();
		else
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).getDayOfYear();   
    }
    public static int timestamp2currentweekofyear(long ts,boolean flag) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).get(weekFields.weekOfWeekBasedYear());
        if(flag) //pb
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneOffset.UTC).get(weekFields.weekOfWeekBasedYear());
		else
	        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).get(weekFields.weekOfWeekBasedYear());
    }
    /** java.util.date has no timeZone ? 
     * https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date */
    public static long date2timestamp(Date date) {//,boolean flag) {  
    	Calendar c = Calendar.getInstance();
        c.setTime(date);
        long time = c.getTimeInMillis();
        return time; 
			//if(flag) //pb
			//return ldt.toEpochSecond(ZoneOffset.UTC);
		//	return ldt.toEpochSecond((ZoneOffset) ZoneId.of( "America/New_York" ));    	
    }
	public static long datetime2ts(LocalDateTime datetime,boolean flag) {
        if(flag) //pb
            return datetime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
		else
        	return datetime.atZone(ZoneId.of( "America/New_York" )).toInstant().toEpochMilli();
    }

	public static long date2ts(Object obj) {
        return ((java.util.Date)obj).getTime();
    }
	
	public static long time2ts(String time,long ts,boolean flag) {
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern(datetimeformat);
        LocalDateTime datetime=LocalDateTime.parse(time,dtf);
        if(flag) //pb
            return datetime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
		else
        	return datetime.atZone(ZoneId.of( "America/New_York" )).toInstant().toEpochMilli();
    }
	//for playback timecross should be used but not for live timertask!
    public static int timeafter(String tm1,String tm2){
        if(tm1.compareTo(tm2)>0) return 1;
        else return 0;
    }
    public static int timeaftereq(String tm1,String tm2){
        if(tm1.compareTo(tm2)>=0) return 1;
        else return 0;
    }    
    public static int timecross(long timeold,long tm1,String dt2,String tm2,boolean flag){ // tm1 >= tm2 
        if((timediff(timeold,dt2,tm2,flag)<0)&&(timediff(tm1,dt2,tm2,flag)>=0)&&(timeold!=0)) return 1;
        else return 0;
    }    
    public static int timecross(long timeold,long tm1,long tm2){ // tm1 >= tm2 
        if((timediff(timeold,tm2)<0)&&(timediff(tm1,tm2)>=0)&&(timeold!=0)) return 1;
        else return 0;
    }
    public static long timediff(long ts1,long ts2){ //>0 if first is later
        return ts1-ts2;
    }
    public static long timediff(long ts,String datestr,String timestr,boolean flag){ //>0 if first is later
        return ts-time2tsToday(datestr,timestr,flag);
    }
    public static long secsSinceMidnight(LocalTime ltime){ 
        return ChronoUnit.SECONDS.between(LocalTime.parse("00:00:00"),ltime);
    }
    public static long time2tsToday(String date,String time,boolean flag) {
        //String dtstr=timestamp2datestr(ts)+" "+time;
        String dtstr=date+" "+time;
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern(datetimeformat);
        LocalDateTime datetime=LocalDateTime.parse(dtstr,dtf);
        if(flag) //pb
            return datetime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
		else
            return datetime.atZone(ZoneId.of( "America/New_York" )).toInstant().toEpochMilli();
    }

//    public static void write2kdb(String str){
//        try { if(LT.pbflag) c3.ks(str); else c5.ks(str); } catch (Exception ex) {  System.err.println ("Ex16:"+ex);   }
//    }

    public static void println(Object line) {
        System.out.println(line);
    }
    public static void print(Object line) {
        System.out.print(line);
    }
    public static void printlnx(Object... obj) { //print multiple objects in one line(if bad object, no print???)
       for(Object s:obj)
            System.out.print(s+" ");
       System.out.print("\n");
    }
    public static void exit(String str) {
        System.out.println("Exiting: "+str);
        System.exit(1);
    }
    public static void exit() {
        System.exit(1);
    }
    public static double round(double val) { //default 100
        return (double)Math.round(val * 100d) / 100d;
    }
    public static double round5(double val) { //default 10000
        return (double)Math.round(val * 100000d) / 100000d;
    }
    public static String strf(double x){
        return String.format( "%.2f",x);
    }
    public static String strfx(double x,int y){
        return String.format( "%."+y+"f",x);
    }
    public static int timestamp2weekday(long ts) {
    	//convert mo-sa 1-7 to 2345601
        return (LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).getDayOfWeek().getValue() + 1) %7;
    }
    public static int timestamp2monthday(long ts) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).getDayOfMonth();
    }
    public static int timestamp2month(long ts) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of( "America/New_York" )).getMonthValue();
    }
    public static String formattedDateTimeNow(String dateformat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat));
    }

    public static int timestamp2daynum(long ts) {
    	return (int)((long)ts/86400000L - 10957);
    }
    public static Millisecond tmst2Millisecond(long ms,boolean flag) {
		Calendar cal; 
		if(flag)
       	   cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        else
       	   cal = Calendar.getInstance();
	
		cal.setTimeInMillis(ms);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1; // Note: zero based!
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int millis = cal.get(Calendar.MILLISECOND);
		
		return new Millisecond(millis,second,minute,hour,day,month,year);
	}

    
//    public static void setmoc(){ //at new day(RT), 1st tick(pb)
//        tday=timestamp2weekday(tmst);
//        tmonth=timestamp2month(tmst);
//        tmday=timestamp2monthday(tmst);
//        if(datestr.substring(5,10).equals("12.24")||datestr.substring(5,10).equals("07.03")||((tday==6)&&(tmonth==11)&&(tmday>22)))
//             { eodstr=eodestr; mocstr=mocestr; eod_mins=780; tot_mins=210; } 
//        else { eodstr=eodnstr; mocstr=mocnstr; eod_mins=960; tot_mins=390; }
//    }       
	
    
}
