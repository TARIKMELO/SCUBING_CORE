package bll.parallel;


import java.util.concurrent.LinkedBlockingQueue;

public class ResourceII<S> extends Resource<S>{
	private LinkedBlockingQueue<S> registers;
	
	public ResourceII(){
		this.registers = new LinkedBlockingQueue<S>();
		finished= false;	
	}
	
	public void putRegister(S register){
		
			this.registers.offer(register);
			wakeup();
		
	}
	
	public S getRegister() throws Exception{
		
			if(!this.registers.isEmpty())
				return this.registers.poll();
			else {
				if(finished==false)
					suspend();
				return null;		
			}		
	}
	
	
	public int getNumOfRegisters(){
		return this.registers.size();
	}
}
