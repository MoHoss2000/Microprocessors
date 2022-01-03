//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class Mai {
//    ReservationStation[] multiply;
//    ReservationStation[] add;
//    StoreBuffer[] store;
//    LoadBuffer[] load;
//    QueueCell[] queue;
//    RegisterFile registerFile;
//    
//    ArrayList<String> issuedInstructions ; 
//    
//    double[] memory ; 
//    
//    int clock;
//
//
//    int addLatency;
//    int subLatency;
//    int mulLatency;
//    int divLatency;
//    int storeLatency;
//    int loadLatency;
//
//    String fileName;
//    ArrayList<String> program;
//    
//    // should we handle the scenario where the queue is less than the size of the program ?!
//
//    public Mai(int addLatency, int subLatency, int mulLatency,
//			   int divLatency, int storeLatency, int loadLatency,
//			   String fileName) {
//        multiply = new ReservationStation[2];
//        add = new ReservationStation[3];
//        load = new LoadBuffer[3];
//        store = new StoreBuffer[3];
//
//        registerFile = new RegisterFile(16);
//        
//        memory = new double [50];
//        
//
//        this.addLatency = addLatency;
//        this.subLatency = subLatency;
//        this.mulLatency = mulLatency;
//        this.divLatency = divLatency;
//        this.storeLatency = storeLatency;
//        this.loadLatency = loadLatency;
//        this.fileName = fileName;
//
//        program = readFile(fileName);
//        issuedInstructions = new ArrayList<String>();
//        queue = new QueueCell[50]; //how big should it be ?
//        runProgram();
//
//    }
//    
//    public void ExecuteAddSub(QueueCell cell )
//    {
//    	if(cell.instruction.equals("ADD.D"))
//    	{
//    		double sum = Double.parseDouble(cell.j) + Double.parseDouble(cell.k);
//    		cell.i = sum +"";
//    	}
//    	else
//    	{
//    		double sub =  Double.parseDouble(cell.j) - Double.parseDouble(cell.k);
//    		cell.i = sub+"";
//    	}
//    }
//    
//    public void ExecuteMulDiv(QueueCell cell )
//    {
//    	if(cell.instruction.equals("MUL.D"))
//    	{
//    		double mul = Double.parseDouble(cell.j) * Double.parseDouble(cell.k);
//    		cell.i = mul +"";
//    	}
//    	else
//    	{
//    		double div =  Double.parseDouble(cell.j) / Double.parseDouble(cell.k);
//    		cell.i = div+"";
//    	}
//    }
//    
//    public boolean finished()
//    {
//    	
//    	for(int i = 0 ; i < program.size() ; i++)
//    	{
//    		QueueCell cell = queue[i];
//    		
//    		if(cell.executionEnd == 0)
//    			return false;
//    	}
//    	
//    	return true ;
//    }
//    
//    public void ExecuteLoad(QueueCell cell)
//    {
//    	// My assumption that the effective address is in binary
//    	
//    	int address = Integer.parseInt(cell.j,2);
//    	
//    	int destination = Integer.parseInt(cell.i.substring(1));
//    	
//    	registerFile.value[destination] = memory[address];
//    }
//    
//    public void ExecuteStore(QueueCell cell)
//    {
//    	// My assumption that the effective address is in binary
//    	
//    	int address = Integer.parseInt(cell.j,2);
//    	
//    	int destination = Integer.parseInt(cell.i.substring(1));
//    	
//    	memory[destination] = registerFile.value[address];
//    }
//
//    public void runProgram() {
//        
//        while(issuedInstructions.size() < program.size() || finished())
//        {
//        	if (clock != program.size() && issuedInstructions.size() < program.size()) {
//        		issuedInstructions.add(program.get(clock));
////                QueueCell queueCell = new QueueCell(program.get(clock), clock);
////                queue[clock] = queueCell;
//            }
//        	
//            boolean someoneWroteBack = false;
//
//            for (QueueCell cell : queue
//            ) {
//            	 tryInsertingInReservationStations(cell);
//            	
//                // started but didn't finish
//                if (cell.executionBegin != 0 && cell.executionEnd == 0) {
//                    int end = cell.executionBegin;
//                    switch (cell.instruction) {
//                        case "ADD.D":
//                        	end += addLatency;
//                            break;
//                        case "SUB.D":
//                            end += subLatency;
//                            break;
//                        case "MUL.D":
//                            end += mulLatency;
//                            break;
//                        case "DIV.D":
//                            end += divLatency;
//                            break;
//                        case "L.D":
//                            end += loadLatency;
//                            break;
//                        default:
//                            end += storeLatency;
//                            break;
//                    }
//
//                    if (end == clock) {
//                        cell.executionEnd = clock;
//                    }
//                  
//                }
//                else //haven't started yet
//                	if(cell.issue < clock && cell.executionBegin == 0)
//                	{
//                		//check if it is in the reservation station 
//                		if(cell.reservationStation != "")
//                		{
//                			char station = cell.reservationStation.charAt(0);
//                			int cellNo = Integer.parseInt(cell.reservationStation.substring(1));
//                			switch (station) 
//                   		  	{
//                   		 	  	case 'A':
//                   		 	  		//you can start executing as we have all the inputs 
//                   		 	  		if(add[cellNo].qj.equals("") && add[cellNo].qk.equals("")) //can multiple functions of the same type be executed at the same time ??
//                   		 	  		{
//                   		 	  			cell.executionBegin = clock; 
//                   		 	  			ExecuteAddSub(cell);
//                   		 	  		}
//                   		 	  		break;
//                   		 	  	case 'M':
//                   		 	  		if(multiply[cellNo].qj.equals("") && multiply[cellNo].qk.equals(""))
//                   		 	  		{
//                   		 	  			cell.executionBegin = clock; 
//                   		 	  			ExecuteMulDiv(cell);
//                   		 	  		}
//                   		 	  		break;
//                   		 	  	case 'L': 
//                   		 	  		cell.executionBegin = clock; 
//                   		 	  		ExecuteLoad(cell);
//                   		 	  		break;
//                   		 	  	default:
//                   		 	  		if(store[cellNo].q.equals(""))
//                   		 	  		{
//                   		 	  			cell.executionBegin = clock ; 
//                   		 	  			ExecuteStore(cell);
//                   		 	  		}
//                   		 	  		break;
//                   		 }
//                	  }
//                	}
//                //finished but did't write back
//                else if (cell.executionBegin != 0 && cell.executionEnd != 0 && cell.writeResult == 0 && !someoneWroteBack) {
//                    someoneWroteBack = true;
//                    cell.writeResult = clock;
//                    writeBack(cell);
//                }
//                
//            }
//        
//
//            clock++;
//        }
//
//    }
//    
//    public void tryInsertingInReservationStations(QueueCell cell)
//    {
//    	switch (cell.instruction) {
//        case "ADD.D":
//        case "SUB.D":
//	        	for(int i = 0 ; i < add.length ; i++)
//	        	{
//	        		ReservationStation r = add[i];
//	        		if(!r.busy)
//	        		{
//	        			cell.reservationStation = "A"+i;
//	        			r.busy = true; 
//	        			r.op = cell.instruction ;
//	        			
//	        			//What is r.a ???
//	
//	        			int a0 =  Integer.parseInt(cell.i.substring(1));
//	        			int a1 =  Integer.parseInt(cell.j.substring(1));
//	        			int a2 =  Integer.parseInt(cell.k.substring(1));
//	        			
//	        			registerFile.qi[a0] = cell.reservationStation;
//	        			
//	        			if(registerFile.qi[a1].equals("0"))
//	        			{
//	        				r.qj = "";
//	        				r.vj = registerFile.value[a1] + "";
//	        			}
//	        			else
//	        			{
//	        				r.vj = "";
//	        				r.qj = registerFile.qi[a1];
//	        			}
//	        			
//	        			if(registerFile.qi[a2].equals("0"))
//	        			{
//	        				r.qk = "";
//	        				r.vk = registerFile.value[a2] + "";
//	        			}
//	        			else
//	        			{
//	        				r.vk = "";
//	        				r.qk = registerFile.qi[a2];
//	        			}
//	        			
//	        		}
//	        	}
//            break;
//        case "MUL.D":
//        case "DIV.D":
//        	for(int i = 0 ; i < multiply.length ; i++)
//        	{
//        		ReservationStation r = multiply[i];
//        		if(!r.busy)
//        		{
//        			cell.reservationStation = "M"+i;
//        			r.busy = true; 
//        			r.op = cell.instruction ;
//        			
//
//        			int m0 =  Integer.parseInt(cell.i.substring(1));
//        			int m1 =  Integer.parseInt(cell.j.substring(1));
//        			int m2 =  Integer.parseInt(cell.k.substring(1));
//        			
//        			registerFile.qi[m0] = cell.reservationStation;
//        			
//        			if(registerFile.qi[m1].equals("0"))
//        			{
//        				r.qj = "";
//        				r.vj = registerFile.value[m1] + "";
//        			}
//        			else
//        			{
//        				r.vj = "";
//        				r.qj = registerFile.qi[m1];
//        			}
//        			
//        			if(registerFile.qi[m2].equals("0"))
//        			{
//        				r.qk = "";
//        				r.vk = registerFile.value[m2] + "";
//        			}
//        			else
//        			{
//        				r.vk = "";
//        				r.qk = registerFile.qi[m2];
//        			}
//        			
//        		}
//        	}
//            break;
//        case "L.D":
//        	for(int i = 0 ; i < load.length ; i++)
//        	{
//        		LoadBuffer r = load[i];
//        		if(!r.busy)
//        		{
//        			cell.reservationStation = "L"+i;
//        			r.busy = true; 
//        			
//        			r.a = cell.j ; 
//        			
//        		    int l1 = Integer.parseInt(cell.i.substring(1));
//        		    
//        		    registerFile.qi[l1] = cell.reservationStation;
//        		}
//        	}
//            break;
//        default:
//        	//store need some work
//        	for(int i = 0 ; i < store.length ; i++)
//        	{
//        		StoreBuffer r = store[i];
//        		if(!r.busy)
//        		{
//        			cell.reservationStation = "S"+i;
//        			r.busy = true; 
//        			
//        			r.a = cell.j ; 
//        			
//        		    int s1 = Integer.parseInt(cell.i.substring(1));
//        		    
//        		    if(registerFile.qi[s1].equals("0"))
//        		    	r.v = registerFile.value[s1] + "";
//        		    else
//        		    	r.q = registerFile.qi[s1];
//        		    
//        		}
//        	}
//            break;
//    	}
//    }
//
//    public void writeBack(QueueCell cell) {
//        String reservationStation = cell.reservationStation;
//        
//        int cellNo = Integer.parseInt(reservationStation.substring(1));
//        
//        switch (cell.instruction) {
//        case "ADD.D":
//        case "SUB.D":
//        	add[cellNo].busy = false ;
//        	add[cellNo].qj = "";
//        	add[cellNo].vj = "";
//        	add[cellNo].qk = "";
//        	add[cellNo].vk = "";
//        	add[cellNo].op = "";
//            break;
//        case "MUL.D":
//        case "DIV.D":
//        	multiply[cellNo].busy = false ;
//        	multiply[cellNo].qj = "";
//        	multiply[cellNo].vj = "";
//        	multiply[cellNo].qk = "";
//        	multiply[cellNo].vk = "";
//        	multiply[cellNo].op = "";
//            break;
//        case "L.D":
//        	load[cellNo].busy = false;
//        	load[cellNo].a = "";
//            break;
//        default:
//        	store[cellNo].busy = false;
//        	store[cellNo].a = "" ;
//        	store[cellNo].q = "" ;
//        	store[cellNo].v = "" ;
//            break;
//    	}
//        
//        for(ReservationStation station : add)
//        {
//        	if(station.qj.equals(reservationStation))
//        	{
//        		station.qj = "";
//        		station.vj = cell.i + "" ;
//        	}
//        	
//        	if(station.qk.equals(reservationStation))
//        	{
//        		station.qk = "";
//        		station.vk = cell.i+"";
//        	}
//        
//        }
//        
//        for(ReservationStation station : multiply)
//        {
//        	if(station.qj.equals(reservationStation))
//        	{
//        		station.qj = "";
//        		station.vj = cell.i + "" ;
//        	}
//        	
//        	if(station.qk.equals(reservationStation))
//        	{
//        		station.qk = "";
//        		station.vk = cell.i+"";
//        	}
//        
//        }
//        
//        for(StoreBuffer station : store)
//        {
//        	if(station.q.equals(reservationStation))
//        	{
//        		station.q = "";
//        		station.v = cell.i + "" ;
//        	}
//        
//        }
//        
//       for(int i = 0 ; i < 16 ; i++)
//       {
//    	   if(registerFile.qi[i].equals(reservationStation))
//    	   {
//    		   registerFile.qi[i] = "0";
//    		   registerFile.value[i] = Double.parseDouble(cell.i); 
//    	   }
//       }
//        
//
//
//    }
//
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Please enter the latency for add");
//        int addLatency = sc.nextInt();
//
//        System.out.println("Please enter the latency for sub");
//        int subLatency = sc.nextInt();
//
//        System.out.println("Please enter the latency for mult");
//        int multLatency = sc.nextInt();
//
//        System.out.println("Please enter the latency for divide");
//        int divLatency = sc.nextInt();
//
//        System.out.println("Please enter the latency for load");
//        int loadLatency = sc.nextInt();
//
//        System.out.println("Please enter the latency for store");
//        int storeLatency = sc.nextInt();
//
//        System.out.println("Please enter the program's file name");
//        String fileName = sc.next();
//
//        Mai cpu = new Mai(addLatency, subLatency, multLatency, divLatency, loadLatency, storeLatency, fileName);
//    	System.out.println(Integer.parseInt("100",2));
//    }
//
//
//    public static ArrayList<String> readFile(String fileName) {
//        ArrayList<String> Inst = new ArrayList<>();
//        try {
//            File myObj = new File("./src/" + fileName);
//            Scanner myReader = new Scanner(myObj);
//            while (myReader.hasNextLine()) {
//                String data = myReader.nextLine();
//                Inst.add(data);
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found");
//        }
//        return Inst;
//    }
//
//}
