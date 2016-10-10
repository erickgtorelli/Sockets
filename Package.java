import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	int sec; 
	Pattern p = Pattern.compile("(-?[0-9]+)");
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

	public char getPackageContent(){
		return pack.charAt((pack.length() - 1));
		//return this.content;
	}

	public int getPackageSec(){
		//return pack.substring(1,2);
		Matcher m = p.matcher(pack);
		int ret = 0;
		if (m.find()) {
			System.out.println(m.group(0));
    		ret = Integer.parseInt(m.group(0));
    		
		}
		else{
			System.out.println("NO MATCH");
		}
		

		return ret; 
	} 
	public void setSec(int sec){
		this.sec = sec; 
	}

}