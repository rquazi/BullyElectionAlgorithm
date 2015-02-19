import java.io.*;
import java.util.Random;

    // represents a single election message
    class Message
    {
		int processId;
		boolean isVictoryMsg;
        Message(int id, boolean isVictory){
            processId=id;
            isVictoryMsg = isVictory;
        }
	}
	
	// messagebox for each participant, used for receiving message from peers
	class MessageBox 
	{
		int entries,maxEntries;
		Message[] elements;
		
		public MessageBox(int number)
		{
			maxEntries=number;
			elements=new Message[maxEntries];
			entries=0;
		}
	
    	synchronized void send(Message msg) throws InterruptedException
    	{
    		while(entries==maxEntries) {
    		    System.out.println("Message from "+msg.processId+" is waiting");
    			wait();
    		}
    		elements[entries]=msg;
    		entries++;
    		notifyAll();
    	}
	
    	synchronized Message recieve() throws InterruptedException{
    		while(entries==0)
	    		wait();
	    	Message x;
	    	x=elements[0];
	    	for(int i=1;i<entries;i++)
	    		elements[i-1]=elements[i];
	    	entries--;
	    	notifyAll();
	    	return x;
	    }
	}

	class Participant extends Thread
	{
		MessageBox inbox;
		MessageBox[]neighbour;
		int id;
		
		int leader;
		int self;
		
		public Participant(int id)
		{
		    this.id = id;
		}
		
		public void run()
		{
			// declare myself as leader and let others know
			leader=id;
			self=id;
			
			System.out.println(id+": Starting election process");
			for(int i=0;i<neighbour.length;i++) {
				try{
				    System.out.println(id+": Sending election message to neighbour: "+i);
					neighbour[i].send(new Message(self, false));
				}catch(Exception e){}
			}
				
			try {
			    while(true){
				    Message m=(Message)inbox.recieve();
			    	System.out.println(id+": Recieved election message from: "+m.processId);
				
				    if(m.processId>leader) {
				    	leader=m.processId;
				    	System.out.println(id+": New message from higher process ID. Selected "+leader+" as current leader");
				    }
    			}
    		}catch(Exception e){}
		
    	}
    }


public class bully 
{
    public static void main(String args[])throws IOException
    {
	    final int processNo=7;
	
	    Random randomGenerator=new Random();
	
	    Participant[] processes=new Participant[processNo];
	    MessageBox[] box=new MessageBox[processNo];
	
	    // create participants of election, their inboxes and neighbours
	    for(int i=0;i<processNo;i++){
		    processes[i]=new Participant(randomGenerator.nextInt(100));
		    processes[i].inbox=new MessageBox(processNo);
		    processes[i].neighbour=new MessageBox[processNo];
	    }
	
	    for(int i=0;i<processNo;i++){
		    for(int j=0;j<processNo;j++)
			    processes[i].neighbour[j]=processes[j].inbox;
	    }
	
	    for(int i=0;i<processNo;i++)
		    processes[i].start();
	
	    try{
		    Thread.sleep(100);
	    }catch(Exception e){}
	
	
	    for(int i=0;i<processNo;i++) {
		    processes[i].interrupt();
		    System.out.println(processes[i].id+": Elected Leader is "+processes[i].leader);
	    }
	
	    System.exit(0);
    }
}
