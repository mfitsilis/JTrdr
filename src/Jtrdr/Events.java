package Jtrdr;

import kiss.API.Generator;

class UpdateEveryXSecondsEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateEverySecondEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateEveryTradeEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateEveryQuoteEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateEveryMinuteEvent extends Generator<String>{void call(String s1){send(s1);}}

class ClearBarChartEvent extends Generator<String>{void call(String s1){send(s1);}}

class ClearLineChartEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateChartsEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateAlgoTickEvent extends Generator<String>{void call(String s1){send(s1);}}

class UpdateWatchlistTime extends Generator<String>{void call(String s1){send(s1);}}

class UpdateNumberOfSymbols extends Generator<String>{void call(String s1){send(s1);}}

class UpdateNumberOfTrades extends Generator<Integer>{void call(Integer i1){send(i1);}}

class SelectWatchlistEvent extends Generator<Integer>{void call(Integer s1){send(s1);}}

class ModeUpdateEvent extends Generator<String>{void call(String s1){send(s1);}}

class NewWatchedTickEvent extends Generator<String>{void call(String s1){send(s1);}}

