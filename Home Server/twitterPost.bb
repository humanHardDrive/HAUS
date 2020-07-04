Const url$ = "arduino-tweet.appspot.com"
Const token$ = "384567178-FwgPEMPsuo5cWNzWoOK1QEFC62vLb5ZDe0RpGfA"

Function twitterPost(mes$)
	tcp = OpenTCPStream(url,80)
	
	If tcp = 0
		Return False
	EndIf

	WriteLine(tcp,"POST http://" + url + "/update HTTP/1.0")
    WriteLine(tcp,"Content-Length: " + Str(Len(mes) + Len(token) + 14))
    WriteLine(tcp,"")
	WriteLine(tcp,"token=" + token + "&status=" + mes)

	CloseTCPStream(tcp)
	Return True
End Function