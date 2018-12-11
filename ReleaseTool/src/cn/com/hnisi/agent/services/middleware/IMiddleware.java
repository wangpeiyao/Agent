package cn.com.hnisi.agent.services.middleware;



public interface IMiddleware {
	
	public void backup();
	
	public void stop();
	
	public void start();
	
	public void uploadFile(String dirPath);
}
