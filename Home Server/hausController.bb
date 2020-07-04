Include "usefulFunctions.bb"
AppTitle "HAUS Controller"

w_main = CreateWindow("HAUS Controller",100,50,640,480,0,5)

b_wimip = CreateButton("What's My IP",10,0,75,50,w_main,1)

Repeat
	Select WaitEvent()
		Case $401  ;GADGET CHANGE
		Select EventSource()
			Case b_wimip
			Notify("Your IP Address is: " + wimip())
		End Select
		Case $803  ;WINDOW CLOSE
		End
	End Select
Forever