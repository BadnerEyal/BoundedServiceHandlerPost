package sciatis.player.bounded.handler;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * This  example was created by Sciatis Technologies and belongs to.
 * 
 * Using this samples for teaching/training or distribution requires written approval from Sciatis Technologies.
 * 
 * Sciatis Technologies will not allow the use of this examples besides than development. 
 * 
 * For any questions please contact Gabriel@proto-mech.com
 * 
 * @author Gabriel@proto-mech.com
 *
 */
public class PlayerActivity extends Activity implements PlayerCompletionListener
{
	private Handler handler = null;
		
	
	private boolean isBounded = false;
	private PlayerInterface playerService = null;
	private PlayerServiceConnection playerServiceConnection = null;
	
	private class PlayerServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			playerService = ((PlayerInterface)binder);
			isBounded = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBounded = false; 
		}
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		/**  זה כל ההבדל בין הנדלר פוסט והנדלר הודעה
		 * אנו צרכים לדעת לדעת לקבל הודעות ולפי
		 * ההודעה אנו מחלטים מה לעשות 
	    */
		//יצרת הנדלר
		handler = new Handler()
		{
            // קבלת הודעות שישלחו לנו
			@Override
			public void handleMessage(Message msg) 
			{
				//אנו שולחים מהשירות שהשיר נגמר מספר 2
				switch (msg.what) {
				case 2:
					onCompletion();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
			
		};

		
		playerServiceConnection = new PlayerServiceConnection();
		
		Button playerStartButton = (Button) findViewById(R.id.serviceStartButton);
		playerStartButton.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{

				if(isBounded && playerService != null)
				{
					playerService.startPlay(PlayerActivity.this, handler);
				}
			}
		});
		
		Button playerStopButton = (Button) findViewById(R.id.serviceStopButton);
		playerStopButton.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{

				if(isBounded && playerService != null)
				{
					playerService.stopPlay();
				}
			}
		});
		
		
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		Intent playerServiceIntent = new Intent(this, ServicePlayer.class);
		bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() 
	{
		super.onStop();
		if(isBounded)
		{
			unbindService(playerServiceConnection);
		}
	}
	
	@Override
	public void onCompletion() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Continue blay?").setMessage("Are u sure u want to blay again?");
		
		builder.setPositiveButton("Sure baba", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				playerService.startPlay(PlayerActivity.this, handler);
			}

		});
		builder.setNegativeButton("באימשך עזוב", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

		});

		builder.create().show();
		
	}

	
	
	
	
	

}
