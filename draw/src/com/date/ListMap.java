package com.date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Bitmap;
import android.net.Uri;

import com.unit.user.Child;
public class ListMap {
	
	private static  List<Child> lc;
    private static  Map<Integer,Bitmap> lb ;
    private static int i=0;
    private static int j=0;
    private static int index = 0;
    private static Map<Integer,String> li ;
    private static int imgsize = 0;
    private static int WindowWigth = 0;
    private static int WindowHigth = 0;
    private static Uri imgUri=null;
    
    
	public static Uri getImgUri() {
		return imgUri;
	}

	public static void setImgUri(Uri imgUri) {
		ListMap.imgUri = imgUri;
	}

	public static int getWindowWigth() {
		return WindowWigth;
	}

	public static void setWindowWigth(int windowWigth) {
		WindowWigth = windowWigth;
	}

	public static int getWindowHigth() {
		return WindowHigth;
	}

	public static void setWindowHigth(int windowHigth) {
		WindowHigth = windowHigth;
	}

	public static int getImgsize() {
		return imgsize;
	}

	public static void setImgsize(int imgsize) {
		ListMap.imgsize = imgsize;
	}

	public static int getJ() {
		return j;
	}

	public static void setJ(int j) {
		ListMap.j = j;
	}

	public static int getI() {
		return i;
	}

	public static void setI(int i) {
		ListMap.i = i;
	}

	public static List<Child> getLc() {
		if(lc == null){
			lc = new ArrayList<Child>();
		}
		return lc;
	}

	public static Map<Integer, Bitmap> getLb() {
		if(lb == null){
			lb = new HashMap<Integer, Bitmap>();
		}
		return lb;
	}


	public static int getIndex() {
		return index;
	}

	public static void setIndex(int index) {
		ListMap.index = index;
	}

	public static Map<Integer, String> getLi() {
		if(li == null){
			li = new HashMap<Integer, String>();
		}
		return li;
	}
	
	
}
   
	

