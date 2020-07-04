symbol SERIALIN=b0

symbol OK_LED=2
symbol ALERT_LED=4
symbol RELAY=1
symbol RECV=3

symbol SYNC=10
symbol DEVON=14
symbol DEVOFF=18
symbol ALERTON=22
symbol ALERTOFF=26
symbol CLOSE=30

symbol OFFCASE=b9

symbol ID=45 ;THIS NEEDS TO BE DIFFERENT FOR EVERY SLAVE MODULE
symbol READY=b2

gosub findSync

setup:
	if SERIALIN<>SYNC then
		gosub findSync
	endif
	high OK_LED
	goto main
	

main:
	serin 3,T2400_4,b0
	debug b0
	select SERIALIN
		case SYNC:
		OFFCASE=0
		
		case DEVON:
		if READY=1 then high RELAY endif
		
		case DEVOFF:
		if READY=1 then low RELAY endif
		
		case ALERTON:
		if READY=1 then high ALERT_LED endif
		
		case ALERTOFF:
		if READY=1 then low ALERT_LED endif
		
		case ID
		let READY=1
		
		case CLOSE
		let READY=0
		
		else
		OFFCASE=OFFCASE+1
		
	endselect
	if OFFCASE>50 then gosub setup
	goto main

findSync:
	OFFCASE=0
	high OK_LED
	high ALERT_LED
	pause 500
	low OK_LED
	low ALERT_LED
	pause 500
	high OK_LED
	high ALERT_LED
	pause 500
	low OK_LED
	low ALERT_LED
	pause 500
	for b10=1 to 50
		serin 3,T2400_4,SERIALIN
	next
	pause 10
	return