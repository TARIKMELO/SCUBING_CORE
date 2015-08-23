package bll.parallel;

import java.util.LinkedList;


public class Resource<S> {
	
	private LinkedList<S> registers;
	protected boolean finished;
		
	public Resource(){
		this.registers = new LinkedList<S>();
		this.finished = false;		
	}
		
	public synchronized void putRegister(S register){
		this.registers.addLast(register);
		wakeup();
	}
	
	protected synchronized void wakeup(){
		this.notify();
	}
							
	public synchronized S getRegister() throws Exception{
		if(!this.registers.isEmpty())
			return this.registers.removeFirst();
		else {
			if(finished==false)
				suspend();
			return null;		
		}
	}
	
	protected synchronized void suspend()throws Exception{
		if(finished == false)
			wait();
	}
	
	public int getNumOfRegisters(){
		return this.registers.size();
	}
	
	public synchronized void setFinished(){
		this.finished = true;
		this.notifyAll();
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
}
