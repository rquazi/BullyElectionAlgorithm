package Bully;

import java.io.*;
import java.util.Random;


public class bully_Algorithm {
	
	static class Message{
		
		Participant process;
		Message(Participant p){
			
			process=p;
		}
	}
	
	static class MessageBox{
		
		int entries,maxEntries;
		Object[] elements;
		
		public MessageBox(int number)
		{
			maxEntries=number;
			elements=new Object[maxEntries];
			entries=0;
		}
	
	
	synchronized void send(Object msg)throws InterruptedException{
		
		while(entries==maxEntries)
			wait();
		elements[entries]=msg;
		entries=entries+1;
		notifyAll();
	}
	
	synchronized Object recieve()throws InterruptedException{
		
		while(entries==0)
			wait();
		Object x;
		x=elements[0];
		for(int i=1;i<entries;i++)
			elements[i-1]=elements[i];
		entries=entries-1;
		notifyAll();
		return x;
	}
	}

	static class Participant extends Thread{
		
		MessageBox inbox;
		MessageBox[]neighbour;
		int value;
		
		Participant leader;
		Participant self;
		
		public void run(){
			
			leader=this;
			self=this;
			
			for(int i=0;i<neighbour.length;i++)
				
				try{
					neighbour[i].send(new Message(self));
				}catch(Exception e){}
				
				try{while(true){
					Message m=(Message)inbox.recieve();
					System.out.println(value+"Recieves "+m.process.value);
					
					if(m.process.value>leader.value)
						leader=m.process;
				
				}
				
		}catch(Exception e){}
		
	}
}
	
public static void main(String args[])throws IOException{
	
	final int processNo=7;
	final int[] value=new int[processNo];
	
	Random randomGenerator=new Random();
	
	
	//Assigning Random ID to the process
	for(int i=0;i<value.length;i++){
		
		value[i]=randomGenerator.nextInt(100);
	}
	
	Participant[] processes=new Participant[processNo];
	MessageBox[] box=new MessageBox[processNo];
	
	for(int i=0;i<processNo;i++){
		
		processes[i]=new Participant();
		processes[i].value=i;
		box[i]=new MessageBox(4);
		
	}
	
	for(int i=0;i<processNo;i++){
		processes[i].inbox=box[i];
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
	
	
	for(int i=0;i<processNo;i++)
		processes[i].interrupt();
	
	for(int i=0;i<processNo;i++)
	{
		if(processes[i].leader!=null)
			System.out.println(processes[i].value+"Elected Leader is "+processes[i].leader.value);
		
			
	}
	System.exit(0);
	
}
	
}
