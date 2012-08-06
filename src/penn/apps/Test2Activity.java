package penn.apps;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Test2Activity extends Activity implements OnInitListener {
	Handler handler = new Handler();

	String TAG = "";
	Facebook facebook = new Facebook("331297413557438");
	String token;
	TextToSpeech mTts;
	String urlResponse;
	String Fileto;

	private static Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Test2Activity.context = getApplicationContext();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		mTts = new TextToSpeech(this, this);

		final EditText input = (EditText) this.findViewById(R.id.input);
		final Button me = (Button) this.findViewById(R.id.Self);
		final Button friends = (Button) this.findViewById(R.id.Friends);
		final Button logoff = (Button) this.findViewById(R.id.logoff);
		final CheckBox clean = (CheckBox) this.findViewById(R.id.clean);
		final CheckBox rap = (CheckBox) this.findViewById(R.id.rap);

		// Drawable d =
		// Drawable.createFromPath("/res/drawable-hdpi/yodawg480.png");
		// l.setBackgroundDrawable(d);

		me.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Bundle stat = new Bundle();
					String cleantype = "";
					String raptype = "pimp";
					if (clean.isChecked()) {
						cleantype = "&clean=true";
					}
					if (rap.isChecked()) {
						raptype = "rap";
					}
					HttpClient hc = new DefaultHttpClient();
					HttpGet get = new HttpGet(
							"http://pennapps.valkyrieinfosystems.com/generate.php?type="
									+ raptype
									+ cleantype);


					HttpResponse r = hc.execute(get);

					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									r.getEntity()
											.getContent()));
					String status = in.readLine();
					if (rap.isChecked()) {
						String[] temp = new String[4];
						temp = status.trim().split(
								"<br/>");
						status = temp[0] + '\n'
								+ temp[1]
								+ '\n'
								+ temp[2]
								+ '\n'
								+ temp[3];
						TextToAudio(status);
					}

					r.getEntity().consumeContent();

					if (Fileto != null) {
						String url = "http://pennapps.valkyrieinfosystems.com/?play="
								+ Fileto;

						stat.putString("message",
								status
										+ '\n'
										+ url);
					} else {
						stat.putString("message",
								status);

					}
					Toast.makeText(context, "Now your friends will all respect you!", Toast.LENGTH_LONG).show();
					String s = facebook.request("me/feed/",
							stat, "POST");


				} catch (IOException e) {
					System.out.println("IOE");
				}
			}

		});

		friends.setOnClickListener(new OnClickListener() {
			String[] s;

			public void onClick(View v) {
				try {
					Bundle stat = new Bundle();
					System.out.println(input.getText()
							.toString());

					String cleantype = "";
					String raptype = "pimp";
					HttpClient hc = new DefaultHttpClient();
					if (clean.isChecked()) {
						cleantype = "&clean=true";
					}
					if (rap.isChecked()) {
						raptype = "rap";
					}
					HttpGet get = new HttpGet(
							"http://pennapps.valkyrieinfosystems.com/generate.php?type="
									+ raptype
									+ cleantype);
					HttpResponse r = hc.execute(get);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									r.getEntity()
											.getContent()));
					String status = in.readLine();
					if (rap.isChecked()) {
						String[] temp = new String[4];
						temp = status.trim().split(
								"<br/>");
						status = temp[0] + '\n'
								+ temp[1]
								+ '\n'
								+ temp[2]
								+ '\n'
								+ temp[3];
						TextToAudio(status);
					}
					if (Fileto != null) {
						String url = "http://pennapps.valkyrieinfosystems.com/?play="
								+ Fileto;

						stat.putString("message",
								status
										+ '\n'
										+ url);
					} else {
						stat.putString("message",
								status);
					}
					if (input.getText().toString()
							.equals("")) {
						s = JSONParse.parse(facebook
								.request("me/friends"));

					} else {
						s = JSONParse.parse(
								facebook.request("me/friends"),
								input.getText()
										.toString());
					}
					if(s[0] == "-1"){
						Toast.makeText(context, "Invalid User, Try Again!", Toast.LENGTH_SHORT).show();
					}
					else {
					String rres = facebook
							.request(s[0] + "/feed",
									stat,
									"POST");

					Toast.makeText(context, "Sent an endearing letter to " +s[1], Toast.LENGTH_LONG).show();
					}
				} catch (IOException e) {
					System.out.println("IOE");
				}
			}
		});

		logoff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				logout();
			}
		});

		facebook.authorize(this, new String[] { "publish_stream",
				"offline_access" }, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
			}

			@Override
			public void onFacebookError(FacebookError error) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onCancel() {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	private void logout() {
		try {
			facebook.logout(this.getBaseContext());
			this.finish();
			this.onDestroy();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
	}

	private void TextToAudio(String s) {

		File testfile = putRapToFile(s);
		//
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		String pathToOurFile = testfile.getPath();
		String urlServer = "http://pennapps.valkyrieinfosystems.com/upload.php?uploaded=true";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(
					new File(pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection",
					"Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary="
							+ boundary);

			outputStream = new DataOutputStream(
					connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"therap\";filename=\""
					+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable,
						maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0,
						bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary
					+ twoHyphens + lineEnd);

			HttpClient hc2 = new DefaultHttpClient();
			HttpGet hg = new HttpGet(urlServer);
			HttpResponse hr2 = hc2.execute(hg);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(hr2.getEntity()
							.getContent()));
			urlResponse = in.readLine();

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection
					.getResponseMessage();
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			// Exception handling
		}

	}


	private File putRapToFile(String therap) {
		HashMap<String, String> myHashRender = new HashMap<String, String>();
		File f = this.getDir("files", Context.MODE_WORLD_WRITEABLE);
		String fname = therap;
		String newfname = fname.replaceAll(" ", "_").substring(0,
				Math.min(fname.length(), 10));
		String destFileName = f.getPath() + "/" + newfname + ".wav";
		Fileto = newfname;
		myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
				therap);
		// mTts.addSpeech(therap, f.getPath() + "/" + fname + ".wav");
		mTts.synthesizeToFile(therap, myHashRender, destFileName);
		try {
			Thread.sleep(5000);
			return new File(destFileName);
		} catch (Exception e) {
			return null;
		} finally {
			mTts.stop();
			mTts.shutdown();
		}

	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.US);
			mTts.setSpeechRate((float) 0.75);
			mTts.setPitch((float) 0.82);
		} else {
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

}
