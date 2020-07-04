;GUIde 1.4 BlitzPlus export
Include "guideexportinclude.bb"
Include "twitterPost.bb"
Include "com-lib.bb"
Include "usefulFunctions.bb"
Include "hausWeather.bb"
AppTitle "HAUS Control Program"

Global EditWindow
Global bt_fanon
Global bt_fanoff
Global bt_desklampon
Global bt_desklampoff
Global bt_standlampon
Global bt_standlampoff
Global sepLine0
Global chb_tcp
Global chb_com
Global tf_tcp
Global tf_com
Global bt_connect
Global chb_server

Global guest
Global client
Global server
Global arduino%

Global connectionType$ = ""
Global serverTrue = False

EditWindow=CreateWindow("HAUS Control Program",0,00,443,229,0,3)
	bt_fanon=CreateButton("Turn Fan On",0,0,120,24,EditWindow,1)
		SetGadgetLayout bt_fanon,1,0,1,0
	bt_fanoff=CreateButton("Turn Fan Off",0,32,120,24,EditWindow,1)
		SetGadgetLayout bt_fanoff,1,0,1,0
	bt_desklampon=CreateButton("Turn Desk Lamp On",152,0,120,24,EditWindow,1)
		SetGadgetLayout bt_desklampon,1,0,1,0
	bt_desklampoff=CreateButton("Turn Desk Lamp Off",152,32,120,24,EditWindow,1)
		SetGadgetLayout bt_desklampoff,1,0,1,0
	bt_standlampon=CreateButton("Turn Stand Lamp On",304,0,120,24,EditWindow,1)
		SetGadgetLayout bt_standlampon,1,0,1,0
	bt_standlampoff=CreateButton("Turn Stand Lamp Off",304,32,120,24,EditWindow,1)
		SetGadgetLayout bt_standlampoff,1,0,1,0
	sepLine0=CreateLine( "",0,64,424,16,EditWindow,0)
	chb_tcp=CreateButton("TCP",24,88,40,16,EditWindow,2)
		SetGadgetLayout chb_tcp,1,0,1,0
	chb_com=CreateButton("COM",24,104,45,16,EditWindow,2)
		SetGadgetLayout chb_com,1,0,1,0
	tf_tcp=CreateTextField(160,88,144,20,EditWindow)
		SetGadgetText tf_tcp,"IP Address"
		SetGadgetLayout tf_tcp,1,0,1,0
	tf_com=CreateTextField(160,120,96,20,EditWindow)
		SetGadgetText tf_com,"COM Port"
		SetGadgetLayout tf_com,1,0,1,0
	bt_connect=CreateButton("Connect",24,136,64,24,EditWindow,1)
		SetGadgetLayout bt_connect,1,0,1,0
	chb_server=CreateButton("Start Server",24,120,96,16,EditWindow,2)
		SetGadgetLayout chb_server,1,0,1,0


;-mainloop--------------------------------------------------------------

InitVoice()

Repeat
	If serverTrue = True
		tempClient = AcceptTCPStream(server)
		If tempClient
			DebugLog "PRINT YAYA"
			client = tempClient
		Else
			If client <> 0
				If ReadAvail(client)
					cmd$ = ReadLine(client)
					DebugLog cmd
					SendMessage(arduino,cmd)
				EndIf
			EndIf
		EndIf
	EndIf
	
	If connectionType = "COM"
		msg$ = RecvMessage(arduino)
		If msg <> ""
			msg = Right(msg,Len(msg)-2)
			DebugLog msg + Chr(13)
			Select msg
				Case "PACKAGE ARRIVED"
				twitterPost("You got a package!")
				Case "MAIL ARRIVED"
				twitterPost("You've got mail!")
			End Select
		EndIf
	EndIf
	
	Select WaitEvent(1)
		Case $401									; interacted with gadget
			DoGadgetAction( EventSource() )
		Case $803									; close gadget
			Exit
	End Select
Forever

CloseCommPort(arduino)
FreeVoice()

;-gadget actions--------------------------------------------------------

Function DoGadgetAction( gadget )
	Select gadget
		Case bt_fanon	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 040 DEVON")
			Case "Client"
			WriteLine(guest,"SLAVE 040 DEVON")
		End Select

		Case bt_fanoff	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 040 DEVOFF")
			Case "Client"
			WriteLine(guest,"SLAVE 040 DEVOFF")
		End Select
		
		Case bt_desklampon	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 045 DEVON")
			Case "Client"
			SendMessage(arduino,"SLAVE 045 DEVON")
		End Select
		
		Case bt_desklampoff	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 045 DEVOFF")
			Case "Client"
			SendMessage(arduino,"SLAVE 045 DEVOFF")
		End Select		

		Case bt_standlampon	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 050 DEVON")
			Case "Client"
			SendMessage(arduino,"SLAVE 050 DEVON")
		End Select		

		Case bt_standlampoff	; user pressed button
		Select connectionType
			Case "COM"
			SendMessage(arduino,"SLAVE 050 DEVOFF")
			Case "Client"
			SendMessage(arduino,"SLAVE 050 DEVOFF")
		End Select		

		Case chb_tcp	; user changed checkbox
		If ButtonState(chb_tcp)
			SetButtonState(chb_com,False)
			DisableGadget(tf_com)
			EnableGadget(tf_tcp)
		EndIf

		Case chb_com	; user changed checkbox
		If ButtonState(chb_com)
			SetButtonState(chb_tcp,False)
			DisableGadget(tf_tcp)
			EnableGadget(tf_com)
		EndIf
		
		Case chb_server	; user changed checkbox
		If ButtonState(chb_server)
			SetButtonState(chb_tcp,False)
			DisableGadget(tf_tcp)
		EndIf

		Case tf_tcp
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case tf_com
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case bt_connect	; user pressed button
		CloseTCPServer(server)
		CloseTCPStream(client)
		CloseCommPort(arduino)
		If ButtonState(chb_tcp)
			Print "Connecting to " + TextFieldText(tf_tcp) + "..."
			guest = OpenTCPStream(TextFieldText(tf_tcp),80)
			If guest
				Print "Connected!"
			Else
				Print "Failed to Connect!"
			EndIf
			connectionType = "Client"
			serverTrue = False
		EndIf
		If ButtonState(chb_com)
			arduino% = OpenCommPort(val(TextFieldText(tf_com)))
			SetComm(arduino%, "baud=2400 parity=N data=8 stop=1")
			SetCommTimeouts(arduino, 500, 500)
			Print "CONNECTED TO COM"
			connectionType = "COM"
		EndIf
		If ButtonState(chb_server)
			Print "Starting server on Port 80..."
			server = CreateTCPServer(80)
			If server
				Print "Server Started!"
				serverTrue = True
			Else
				Print "Server failed to start!"
				serverTrue = False
			EndIf
		EndIf

	End Select
End Function