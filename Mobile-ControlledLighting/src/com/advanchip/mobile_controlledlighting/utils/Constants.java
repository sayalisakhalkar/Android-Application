package com.advanchip.mobile_controlledlighting.utils;

public class Constants {
	public static final String CLIENT_ID = "android_v1";
	public static final String CLIENT_SECRET = "jzFsKiym6Tn5gKTrRwfnJTaDUYgAcEfg"; // secret
	//public static String SERVER_URL = "5";
	//public static String ADVANCHIP_SERVER = "https://www.advanchiplighting.com/api/v1/gateways";

	public static String SERVER_URL = "http://mobileligting.com:3000";
	
	public static String URL_LOGIN = SERVER_URL + "/oauth2/token";
	public static String URL_SIGNUP = SERVER_URL + "/api/v1/users";
	public static String URL_RESET_PASSWORD = SERVER_URL + "/api/v1/resetPassword";
	public static String URL_FORGOT_PASSWORD = SERVER_URL + "/api/v1/forgotPassword";
	public static String URL_LOGOUT = SERVER_URL + "/api/v1/logout";
	public static String URL_CREATE_GATEWAY = SERVER_URL + "/api/v1/gateways";
	
}
