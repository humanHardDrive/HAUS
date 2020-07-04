Type weather
	Field time$
	Field condition$
	Field temp$
	Field percip$
	Field humid$
	Field wind$
End Type

;Weather URL http://www.weather.com/weather/hourbyhour/zipCode
Function weatherReport(zipCode$)
	tcp = OpenTCPStream("www.weather.com",80)
	
	WriteLine(tcp,"GET http://www.weather.com/weather/hourbyhour/" + zipCode + " HTTP/1.0")
	WriteLine(tcp,Chr(10))
	
	While Not Eof(tcp)
		urlLine$ = Trim(ReadLine(tcp))
		If Instr(urlLine,Chr(34)+"hbhTRHour")
			weather.weather=New weather
			
			tempLine$ = Trim(ReadLine(tcp))
			time$ = Right(tempLine,Len(tempLine) - 28)
			time = Left(time,Len(time) - 12)
			weather\time = time
			
			tempLine = Trim(ReadLine(tcp))
			tempLine = Trim(ReadLine(tcp))
			
			cond$ = Right(tempLine,Len(tempLine) - 36)
						
			If(Instr(cond,"deg") <> 0)
				cond = Right(cond,Len(cond) - 17)
			Else
				cond = Right(cond,Len(cond) - 12)
			EndIf
			
			cond = Left(cond,Len(cond) - 12)
			weather\condition = cond
			
			tempLine = Trim(ReadLine(tcp))
			
			temp$ = Right(tempLine,Len(tempLine) - 29)
			
			If(Instr(temp,"deg") <> 0)
				temp = Left(temp,Len(temp) - 19)
			Else
				temp = Left(temp,Len(temp) - 14)
			EndIf
			
			weather\temp = temp
			
			tempLine = Trim(ReadLine(tcp))
			
			percip$ = Right(tempLine,Len(tempLine) - 30)
			percip = Left(percip,Len(percip) -13)
			
			weather\percip = percip
			
			tempLine = Trim(ReadLine(tcp))
			
			humid = Right(tempLine,Len(tempLine) - 32)
			
			weather\humid = humid
			
			tempLine = Trim(ReadLine(tcp))
			
			wind$ = Left(tempLine,Len(tempLine) - 16)
			wind = Right(wind,Len(wind) - Instr(wind,"<br>") - 4)
			
			weather\wind = wind
		EndIf
	Wend
End Function