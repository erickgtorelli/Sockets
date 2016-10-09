/*
* Elemental concept of the packages on the projet
* 
**************************************************
* A package must be on the way "#sec:character"	 |
* "sec" is the number of the packages 		     |
* ":" separatoro								 |
* "character" content of the packages 			 |
**************************************************
*/

public class Package{
	private String pack;
	private char content; 
	private int sec; 
	/*
	* basic constructor class
	* @param sec number of the package
	* @param content of the package
	*/
	public Package(int sec, char content){
		pack = "#"  + sec + ":" + content; 
		this.sec = sec;
		this.content = content;	
	}
	public Package(String pack){
		this.pack = pack;
	}
	public void setPackage(String pack){
		this.pack = pack;
	}	
	public String getPackage(){
		return pack;
	}

	public String getPackageContent(){
		//return pack.substring(3,4);
		return this.content;
	}

	public String getPackageSec(){
		//return pack.substring(1,2);
		return this.sec; 
	} 

}