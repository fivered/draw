package com.conn.path;

public class AllPath {
	private  static String getFuadress = "http://draw.ideer.cn/api.php";
	private static String listUrl = "http://draw.ideer.cn/api.php/Api/List/insert.json";
	private static String serchUrl = "http://draw.ideer.cn/api.php/Api/List/get.json";
	private static String updateUrl = "http://draw.ideer.cn/api.php/Api/App/info";
	
	public static String getGetFuadress() {
		return getFuadress;
	}
	public static void setGetFuadress(String getFuadress) {
		AllPath.getFuadress = getFuadress;
	}
	public static String getMakeMaxDifferenceUrl(){
		String path = getFuadress+ "/Api/Pic/pic/";
		return path;
	}
	
	public static String getListUrl(){
		String path = listUrl;
		return path;
	}
	
	public static String getSerchUrl(){
		String path = serchUrl;
		return path;
	}
	
	public static String getUpdateUrl(){
		String path = updateUrl;
		return path;
	}


}
