res:algores  / select from algores where pnl<>0
res:delete from res where ((pnl=0) and (num>1))
res:`date xcol update time:time.date,ntrades:count pnl by time.date from res
res2:select prc:price,price:first price,trades:first ntrades,pnl,sumpnl:sum pnl,prcpnl:100*sum (pnl%price) by date from res	
update num:i from `res2;
trades2:raze exec trades from res2
update cpnl:(1+1*prcpnl%100) *\ from `res2;
update cumpnl:sumpnl +\ from `res2;
cpnl:exec cumpnl from res2
pnlexc:(count cpnl)#-100.0
exc: (count cpnl)#-100.0
j:0;do[count cpnl;$[cpnl[j]<pnlexc[j-1];pnlexc[j]:pnlexc[j-1];pnlexc[j]:cpnl[j]];j+:1]
j:0;do[count cpnl;$[pnlexc[j]=pnlexc[j-1];exc[j]:exc[j-1]+1;exc[j]:1.0];j+:1]
update series:?[exc[i]<exc[i-1];exc[i-1];1] from `res2;
/rename pnl to trades
res2:`date`prc`price`ntrades`trades`sumpnl`prcpnl`num`cpnl`cumpnl`series xcol res2;
/algoresq:select date,ntrades,sumpnl,prcpnl,num,cpnl,cumpnl,series,trades:`$ raze each ($)trades from res2    / this one has no gaps :(
/every entry must be a single symbol with gaps! (type -11) - the next one works and drops the 0 in front!
/if only one trade cl2op, do not remove
/convert to symbol
/update trades:0 from `res2 where ntrades=1;
$[1=count distinct exec ntrades from res2;;update {1_ x} each trades from `res2];  /remove 1st trade (but only is 0 !!!)
/tradecount:10;
/update {$[tradecount>count x;x;tradecount#x]} each trades from `res2;
update sumpnl:{sum x} each trades from `res2;
update cumpnl:sums sumpnl from `res2;
update prcpnl:100*(sumpnl%price) from `res2;
update cpnl:prds (1+prcpnl%100) from `res2;
/select date,ntrades:{count x} each trades,trades:{`$raze raze string (0!res2)[`trades][x] ,' " "} each til count (0!res2)[`trades] from res2

algoresq:select date,ntrades:{count x} each trades,sumpnl,prcpnl,num,cpnl,cumpnl,series,prc:{`$raze raze string (0!res2)[`prc][x] ,' " "} each til count (0!res2)[`prc], trades:{`$raze raze string (0!res2)[`trades][x] ,' " "} each til count (0!res2)[`trades] from res2
/replace `symbol$() with ` to properly display in qlikview
update trades:` from `algoresq where ntrades=0;
algoresq:0!(`date xkey algoresq) ij select trade1st:100*first 1_pnl%price,trade1stnum:first 1_num by time.date from algores
