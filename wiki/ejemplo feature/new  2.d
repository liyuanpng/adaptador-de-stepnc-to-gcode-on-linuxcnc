#47=MACHINING_WORKINGSTEP('ConicalBottomHole_RGH',#15,#34,#25,$);
  #34=ROUND_HOLE('Furo com base conica',#2,(#25,#48),#35,#43,#44,$,#46);
    #25=CENTER_DRILLING($,$,'Center Drilling',5.0,#26,#27,#32,#33,$,10.0,$,$,$,$);
	  #27=MILLING_CUTTING_TOOL('center drill',#28,(#30),$,$,$);
        #28=CENTER_DRILL(#29,2,.RIGHT.,$,25.0);
           #29=MILLING_TOOL_DIMENSION(6.0,0.5235987755982988,0.0,10.0,0.0,0.0,0.0);
		   
	  #32=MILLING_TECHNOLOGY(0.175,.TCP.,11.0,-5.83568124670283,$,.F.,.F.,.F.,$);
	  
      #33=MILLING_MACHINE_FUNCTIONS(.T.,$,$,.F.,$,(),.T.,$,$,());   
	  
	#35=AXIS2_PLACEMENT_3D('Furo com base conica placement',#36,#37,#38);
      #36=CARTESIAN_POINT('',(100.0,75.0,30.0));
	  
	#46=CONICAL_HOLE_BOTTOM(0.3490658503988659,$);