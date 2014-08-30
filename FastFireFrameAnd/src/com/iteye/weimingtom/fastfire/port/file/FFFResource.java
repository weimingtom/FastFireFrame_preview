package com.iteye.weimingtom.fastfire.port.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.iteye.weimingtom.fastfire.port.window.FFFWindow;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;

public class FFFResource {
	public final static boolean USE_CACHE = true;
	
	private final static String ZIP_FILENAME = "data.zip";
	private final static String DATA_PATH = "data";
	private final static String CGDATA_PATH = "cgdata";
	private final static String RULE_PATH = "rule";
	
	private String WORKPATH = "./";
	private String ZIPFILE = WORKPATH + ZIP_FILENAME;
	
	public Map<String, Bitmap> imageMap = new Hashtable<String, Bitmap>();
	public Map<String, ByteBuffer> dataMap = new Hashtable<String, ByteBuffer>();
	public Map<String, String> textMap = new HashMap<String, String>();
	public Map<String, String> imagePathMap = new Hashtable<String, String>();
	
	/**
	 * FIXME:create directory failed?
	 */
	public void init(FFFWindow win) {
		FFFLog.trace("FFFResource::init()");
		if (win != null) {
			Context ctx = win.getContext();
			if (ctx != null) {
				String root = Environment.getExternalStorageDirectory() + "/Android/data/" + ctx.getPackageName();
		        File path = new File(root);
		        File pathData = new File(root + File.separator + DATA_PATH);
		        File pathCGData = new File(root + File.separator + CGDATA_PATH);
		        File pathRule = new File(root + File.separator + RULE_PATH);
		        File zipfile = new File(path, ZIP_FILENAME);
		        
		        WORKPATH = path.getAbsolutePath();
		        
		        boolean mExternalStorageAvailable = false;
		        boolean mExternalStorageWriteable = false;
		        String state = Environment.getExternalStorageState();
		        if (Environment.MEDIA_MOUNTED.equals(state)) {
		            mExternalStorageAvailable = mExternalStorageWriteable = true;
		        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		            mExternalStorageAvailable = true;
		            mExternalStorageWriteable = false;
		        } else {
		            mExternalStorageAvailable = mExternalStorageWriteable = false;
		        }
		        if (mExternalStorageAvailable && mExternalStorageWriteable) {
		        	path.mkdirs();
		        	pathData.mkdirs();
		        	pathCGData.mkdirs();
		        	pathRule.mkdirs();
		        	ZIPFILE = zipfile.getAbsolutePath();
		        } else {
		        	ZIPFILE = null;
		        }
		        
				AssetManager am = ctx.getAssets();
				if (am != null) {
			    	InputStream inputStream = null;
			        BufferedInputStream istr = null; 
			        OutputStream outputStream = null;
			        BufferedOutputStream ostr = null;
			        try {
			        	ArrayList<String> fileAssets = new ArrayList<String>();
			        	String[] filesRoot = am.list("");
			        	if (false) {
				        	FFFLog.trace("init start:" + filesRoot.length);
				        	for (int i = 0; filesRoot != null && i < filesRoot.length; i++) {
				        		FFFLog.trace("init start:" + filesRoot[i]);
				        		if (filesRoot[i] != null && filesRoot[i].equals(ZIP_FILENAME)) {
				        			fileAssets.add(ZIP_FILENAME);
				        		}
				        	}
			        	} else {
			        		fileAssets.add(ZIP_FILENAME);
			        	}
			        	{
				        	String[] filesData = am.list(DATA_PATH);
				        	for (int i = 0; filesData != null && i < filesData.length; i++) {
				        		fileAssets.add(DATA_PATH + File.separator + filesData[i]);
				        	}
			        	}
			        	{
				        	String[] filesCGData = am.list(CGDATA_PATH);
				        	for (int i = 0; filesCGData != null && i < filesCGData.length; i++) {
				        		fileAssets.add(CGDATA_PATH + File.separator + filesCGData[i]);
				        	}
			        	}
			        	{
				        	String[] filesRule = am.list(RULE_PATH);
				        	for (int i = 0; filesRule != null && i < filesRule.length; i++) {
				        		fileAssets.add(RULE_PATH + File.separator + filesRule[i]);
				        	}
			        	}
			        	for (int i = 0; i < fileAssets.size(); i++) {
			        		FFFLog.trace("FFFResource::init() copying assets..." + fileAssets.get(i));
			        		inputStream = am.open(fileAssets.get(i));
				        	istr = new BufferedInputStream(inputStream);
				        	outputStream = new FileOutputStream(WORKPATH + File.separator + fileAssets.get(i));
				        	ostr = new BufferedOutputStream(outputStream);
				        	byte[] bytes = new byte[2048];
				            int size = 0;
				            while (true) {
				                size = istr.read(bytes);
				                if (size >= 0) {
				                	ostr.write(bytes, 0, size);
				                	ostr.flush();
				                } else {
				                	break;
				                }
				            }
			        	}
			        } catch (IOException e) {
			        	e.printStackTrace();
			        } finally {
			        	if (ostr != null) {
			        		try {
			        			ostr.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        	if (outputStream != null) {
			        		try {
			        			outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        	if (istr != null) {
				        	try {
								istr.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        	if (inputStream != null) {
				        	try {
				        		inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        }
				}
			}
		}
	}
	
	public void loadClassText(String name, Class<?> cls, String resName) {
		String str = null;
		InputStream input = cls.getResourceAsStream(resName);
		if (input != null) {
			try {
				byte[] bytes = new byte[input.available()];
				input.read(bytes);
				str = new String(bytes, "UTF8");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		textMap.put(name, str);
	}
	
	public void loadText(String name, String path) {
		InputStream instr = null;
		ZipFile zipFile = null;
		try {
			File file = new File(ZIPFILE);
			if (file.exists() && file.canRead()) {
				zipFile = new ZipFile(ZIPFILE);
				ZipEntry entry = zipFile.getEntry(path);
				if (entry != null) {
					instr = zipFile.getInputStream(entry);
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
			} else {
				instr = new FileInputStream(WORKPATH + File.separator + path);
			}
			byte[] bytes = null;
			bytes = new byte[instr.available()];
			instr.read(bytes);
			String str = new String(bytes, "UTF8");
			textMap.put(name, str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (instr != null) {
				try {
					instr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadClassImage(String name, Class<?> cls) {
		imageMap.put(name, BitmapFactory.decodeStream(cls.getResourceAsStream(name)));
	}

	public Bitmap loadImageNoCache(String name) {
		String path = imagePathMap.get(name);
		if (path != null) {
			InputStream instr = null;
			ZipFile zipFile = null;
			try {
				File file = new File(ZIPFILE);
				if (file.exists() && file.canRead()) {
					zipFile = new ZipFile(ZIPFILE);
					ZipEntry entry = zipFile.getEntry(path);
					if (entry != null) {
						instr = zipFile.getInputStream(entry);
					} else {
						instr = new FileInputStream(WORKPATH + File.separator + path);
					}
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
				//imageMap.put(name, BitmapFactory.decodeStream(instr));
				FFFLog.traceMemory("FFFResource.loadImage " + name);
				return BitmapFactory.decodeStream(instr);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (instr != null) {
					try {
						instr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public void loadImage(String name, String path) {
		imagePathMap.put(name, path);
		if (USE_CACHE) {
			imageMap.put(name, loadImageNoCache(name));
		}
	}
	
	public void loadClassData(String name, Class<?> cls) {
		InputStream instr = cls.getResourceAsStream(name);
		byte[] bytes = null;
		try {
			bytes = new byte[instr.available()];
			instr.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataMap.put(name, ByteBuffer.wrap(bytes));
		FFFLog.traceMemory("FFFResource.loadClassData " + name);
	}
	
	public void loadData(String name, String path) {
		InputStream instr = null;
		ZipFile zipFile = null;
		try {
			File file = new File(ZIPFILE);
			if (file.exists() && file.canRead()) {
				zipFile = new ZipFile(ZIPFILE);
				ZipEntry entry = zipFile.getEntry(path);
				if (entry != null) {
					instr = zipFile.getInputStream(entry);
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
			} else {
				instr = new FileInputStream(WORKPATH + File.separator + path);
			}
			byte[] bytes = null;
			try {
				bytes = new byte[instr.available()];
				instr.read(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataMap.put(name, ByteBuffer.wrap(bytes));
			FFFLog.traceMemory("FFFResource.loadData " + name);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (instr != null) {
				try {
					instr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void loadBytes(String name, ByteBuffer bytes) {
		dataMap.put(name, bytes);
		FFFLog.traceMemory("FFFResource.loadBytes " + name);
	}
	
	public void unloadAll() {
		for (Map.Entry<String, Bitmap> entry : imageMap.entrySet()) {
			if (entry != null && entry.getValue() != null && !entry.getValue().isRecycled()) {
				entry.getValue().recycle();
				FFFLog.traceMemory("unloadAll recycle " + entry.getKey());
			} else {
				FFFLog.traceMemory("unloadAll warning!!! " + entry.getKey());
			}
		}
		imageMap.clear();
		dataMap.clear();
		textMap.clear();
	}
	
}
