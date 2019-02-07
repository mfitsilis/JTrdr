call antbuild.bat >out.txt

java -cp "../org-apache-commons-lang.jar;../log4j-1.2.17.jar;../kiss/kiss.jar;../rangeslider/slider.jar;../jedis-2.9.0.jar;../jedis-2.9.0.jar;../miglayout-4.0-swing.jar;../jfreechart-1.0.17/lib/jfreechart-1.0.17.jar;../jfreechart-1.0.17/lib/jcommon-1.0.21.jar";bin Jtrdr.Jtrdr %1 %2
