symbol DATAOUT=1
symbol STATUS=2
symbol PACKAGEIN=3
symbol MAILIN=4

symbol PACKAGEDELV=b0
symbol MAILDELV=b1
symbol PACKAGECOUNTER=b2
symbol MAILCOUNTER=b3

gosub setup

main:
	if pinC.3=1 and PACKAGEDELV=0 then
		let PACKAGEDELV=1
		let b6 = 0
		gosub package
	endif
	if pinC.4=1 and MAILDELV=0 then
		let MAILDELV=1
		let b7=0
		gosub mail
	endif
	
	if pinC.3=0 and PACKAGEDELV=1 then
		let b6 = b6+1
	endif
	if pinC.4=0 and MAILDELV=1 then
		let b7 = b7+1
	endif
	
	if b6=255 then
		let b6=0
		let PACKAGEDELV=0
	endif
	
	if b7=255 then
		let b7=0
		let MAILDELV=0
	endif
	
	`serout C.1,T4800,(10)
	goto main

package:
	for b5=1 to 5
	for b4=1 to 250
		serout C.1,T4800,(150)
	next b4
	next b5
	return

mail:
	for b5 = 1 to 5
	for b4 = 1 to 250
		serout C.1,T4800,(200)
		pause 1
	next b4
	next b5
	return
	
setup:
	high STATUS
	return