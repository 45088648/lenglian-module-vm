package com.beetech.module.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.beetech.module.activity.MainActivity;

public class DownloadService {
	private Activity context;
	private String url;
	private String path;
	private String filename;
	private int fileSize;
	private int downLoadFileSize;
	private ProgressDialog mProgressDialog;
	private String clientVersion;
	private String version;
	private String flag;
	private String loadApkUrl;
	private String alertMsg;
	private static final int GET_INFO_SUCCESS = 3;
	private static final int GET_INFO_FAILED = 4;
	private static final int SHOW_UPDATE_ALERT = 8;
	private static final int SocketException = 9;
	private static final int NO_NetWork = 10;
	private boolean cancel = false;

	private ProgressDialog pd = null;

	private AlertDialog.Builder builder = null;

	private AlertDialog.Builder b = null;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("WrongConstant")
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
					case 0:
						DownloadService.this.mProgressDialog.setMax(100);
					case 1:
						int result = DownloadService.this.downLoadFileSize * 100 / DownloadService.this.fileSize;

						DownloadService.this.mProgressDialog.setProgress(result);
						break;
					case 2:
						DownloadService.this.mProgressDialog.dismiss();
						Toast.makeText(DownloadService.this.context, "文件下载完成", 1).show();

						String fileName = DownloadService.this.path + DownloadService.this.filename;
						Intent intent = new Intent("android.intent.action.VIEW");
						intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
						DownloadService.this.context.startActivity(intent);
						DownloadService.this.context.finish();
						break;
					case -1:
						String error = msg.getData().getString("error");
						Toast.makeText(DownloadService.this.context, error, 1).show();
						break;
					case 3:
						DownloadService.this.dissmissPD();
						DownloadService.this.checkToShowAlert();
						break;
					case 4:
						DownloadService.this.dissmissPD();
						DownloadService.this.showWrongALert("升级失败,请联系客服");
						break;
					case 5:
						DownloadService.this.showWrongALert("请检查内存卡");
						break;
					case 6:
						DownloadService.this.showWrongALert("升级失败，请检查网络");
						break;
					case 8:
						DownloadService.this.showAlert();
						break;
					case 9:
						DownloadService.this.dissmissPD();
						DownloadService.this.showWrongALert("连接服务器超时,请联系客服");
						break;
					case 10:
						DownloadService.this.dissmissPD();
						DownloadService.this.showWrongALert("连接超时,请检查网络");
					case 7:
				}
			}
			super.handleMessage(msg);
		}
	};
	private FileOutputStream fos;
	private ArrayList<String> list;
	private AlertDialog ad;
	private Handler outhandler;
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			// throw new RuntimeException(); //不知道为什么抛异常，注释掉 ，2014-7-2 zhangcs
		}
	};

	public void dissmissPD() {
		if (this.pd != null)
			try {
				this.pd.dismiss();
			} catch (Exception e) {
				this.pd.hide();
			}
	}

	public DownloadService(Activity context, Handler outhandler, String clientVersion, String url, String path) {
		this.context = context;
		this.path = path;
		this.url = url;
		this.clientVersion = clientVersion;
		this.outhandler = outhandler;
	}

	public void down_file() {
		if (!checkSDCard()) {
			this.builder = null;
			this.ad = null;
			this.handler.sendEmptyMessage(5);
			return;
		}

		this.mProgressDialog = new ProgressDialog(this.context);

		this.mProgressDialog.setTitle("下载");
		this.mProgressDialog.setProgressStyle(1);

		this.mProgressDialog.setButton("隐藏", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		this.mProgressDialog.setButton2("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					DownloadService.this.cancel = true;
					DownloadService.this.fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.mProgressDialog.setCancelable(false);
		this.mProgressDialog.show();
		new downLoadThread().start();
	}

	public int downLoadFile() throws IOException {
		this.filename = this.loadApkUrl.substring(this.loadApkUrl.lastIndexOf("/") + 1);

		URL myURL = new URL(this.loadApkUrl);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();
		if (this.fileSize <= 0)
			throw new RuntimeException("无法获知文件大小 ");
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		File f = new File(this.path);

		f.mkdirs();
		f = new File(this.path + "/" + this.filename);

		this.fos = new FileOutputStream(f);

		byte[] buf = new byte[1024];
		this.downLoadFileSize = 0;
		sendMsg(0);
		while (true) {
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			this.fos.write(buf, 0, numread);
			this.downLoadFileSize += numread;

			sendMsg(1);
		}
		try {
			is.close();
		} catch (Exception ex) {
			sendMsg(9);
		}
		sendMsg(2);
		return 1;
	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		this.handler.sendMessage(msg);
	}

	public void checkUpdateByUrl() {
		if (!checkNet()) {
			sendMsg(10);
			return;
		}
		try {
			this.pd.dismiss();
		} catch (Exception localException) {
		}
		this.pd = ProgressDialog.show(this.context, null, "检查更新请稍候");
		new Thread() {
			public void run() {
				URLConnection urlConnection = null;
				InputStream is = null;
				InputStreamReader aa = null;
				BufferedReader br = null;
				try {
					URL myUrl = new URL(DownloadService.this.url);
					urlConnection = myUrl.openConnection();
					urlConnection.setConnectTimeout(3000);
					urlConnection.setReadTimeout(3000);
					is = urlConnection.getInputStream();
					aa = new InputStreamReader(is, "gb2312");

					br = new BufferedReader(aa);
					String str = null;
					DownloadService.this.list = new ArrayList<String>();
					int i = 0;
					while ((str = br.readLine()) != null) {
						DownloadService.this.list.add(str);
					}
					DownloadService.this.handler.sendEmptyMessage(3);
				} catch (SocketException e) {
					DownloadService.this.handler.sendEmptyMessage(9);
				} catch (IOException e) {
					DownloadService.this.handler.sendEmptyMessage(4);
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if (aa != null) {
							aa.close();
						}
						if (br != null) {
							br.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		try {
			Looper.getMainLooper();
			Looper.loop();
		} catch (RuntimeException e) {

		}
	}

	private void checkToShowAlert() {
		if (this.list.size() != 0) {
			try {
				this.version = ((String) this.list.get(0)).toString().trim();
				this.flag = ((String) this.list.get(1)).trim();
				this.loadApkUrl = ((String) this.list.get(2)).trim();
				this.alertMsg = new String(((String) this.list.get(3)).getBytes("gb2312"), "gbk");
			} catch (Exception localException) {
			}

			showAlert();
		} else {
			this.handler.sendEmptyMessage(4);
		}
	}

	@SuppressWarnings("deprecation")
	public void showAlert() {
		double intVersion = Double.parseDouble(this.version);
		double intClientVersion = Double.parseDouble(this.clientVersion);
		if ((intClientVersion >= intVersion) || (this.flag.equals("2"))) {
			this.handler2.sendEmptyMessage(1);
			this.outhandler.sendEmptyMessage(20);
			return;
		}

		if (this.ad != null) {
			this.ad.dismiss();
			this.ad.show();
			return;
		}

		this.ad = new AlertDialog.Builder(this.context).create();
		this.ad.setTitle("升级提示");
		this.ad.setButton("升级", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DownloadService.this.builder = null;
				DownloadService.this.ad = null;
				DownloadService.this.down_file();
			}
		});
		this.ad.setMessage(this.alertMsg);
		if (!this.flag.equals("0"))
			this.ad.setButton2("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DownloadService.this.ad = null;
					DownloadService.this.handler2.sendEmptyMessage(1);
					DownloadService.this.outhandler.sendEmptyMessage(20);
				}
			});
		else {
			this.ad.setButton2("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DownloadService.this.ad = null;
					DownloadService.this.handler2.sendEmptyMessage(1);
					DownloadService.this.outhandler.sendEmptyMessage(20);
				}
			});
		}
		this.ad.setCancelable(false);
		this.ad.show();
	}

	public void tip(String msg) {
		Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
	}

	private boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			return true;
		}
		return false;
	}

	public void exit() {
		this.context.finish();
		System.exit(0);
	}

	public void showWrongALert(String msg) {
//		if (this.builder != null) {
//			this.builder.show();
//		} else {
//			this.builder = new AlertDialog.Builder(this.context);
//			this.builder.setMessage(msg).setNegativeButton("确定", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					DownloadService.this.exit();
//				}
//			}).setCancelable(false).create().show();
//		}
		Intent intent=new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	public boolean checkNet() {
		ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] nis = cm.getAllNetworkInfo();
			if (nis != null) {
				for (int i = 0; i < nis.length; i++) {
					if (nis[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public class downLoadThread extends Thread {
		public downLoadThread() {
		}

		public void run() {
			try {
				DownloadService.this.downLoadFile();
			} catch (IOException e) {
				DownloadService.this.handler.sendEmptyMessage(4);
				e.printStackTrace();
			}
		}
	}
}