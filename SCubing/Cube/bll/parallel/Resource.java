package bll.parallel;

import java.util.concurrent.LinkedBlockingQueue;


public class Resource<S> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453120505489300725L;
	private LinkedBlockingQueue<S> registers;
	protected boolean finished;

	public Resource(){
		this.registers = new LinkedBlockingQueue<S>();
		this.finished = false;		
	}

	public void putRegister(S register){

		this.registers.offer(register);
		wakeup();

	}

	protected synchronized void wakeup(){
		this.notify();
	}

	public S getRegister() throws Exception{
		//if(!this.registers.isEmpty())
			return this.registers.poll();
		//else {
		//	if(finished==false)
		//		suspend();
		//	return null;		
		//}
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
