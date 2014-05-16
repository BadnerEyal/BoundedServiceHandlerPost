package sciatis.player.bounded.handler;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

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
 */
public class ServicePlayer extends Service implements OnCompletionListener
{
	private Handler handler = null;
	private PlayerCompletionListener listener = null;
	private MediaPlayer player = null;
	
	public ServicePlayer() {
		super();
	}

	
	private final LocalBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return binder;
	}
	
	@Override	
	public void onCreate() 
	{
		super.onCreate();
		initPlayer();
	}

	private void initPlayer() 
	{
		if(player != null)
		{
			if(player.isPlaying())
			{
				player.stop();
			}
			player.release();
			player = null;
		}
			
		player = MediaPlayer.create(this, R.raw.rihana_disturbia);
		
//		String filePath = Environment.getExternalStorageDirectory()
//					+"/MyMusic/shir1.mp3";
//		
//		player = new MediaPlayer();
		try 
		{
			//player.setDataSource(filePath);
			//player.prepare();   
			player.setOnCompletionListener(this);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
	}

	private void _startPlay() 
	{
		
		    if(!player.isPlaying())
			{
				player.start();
			}
	}
	
	private void _stopPlay() 
	{
		
		//יצרת טרד חדש בתוך השירות איך נדע שנגמר
				// על ידי הצינור ישלח לנו הודעה שגמר
	    new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{	
				if(player != null)
				{
				   if (player.isPlaying()) 
				   {
					   player.stop();
				   }
					player.release();
				}
				
				player = null;
				initPlayer();
		    
				// שליחת הודעה 
				handler.sendEmptyMessage(2);
				
//				handler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						listener.onCompletion();
//						
//					}
//				});
			}
		}).start();

	}
	
	@Override
	public void onDestroy() 
	{
		if(player != null)
		{
		   if (player.isPlaying()) 
		   {
			   player.stop();
		   }
		   player.release();
		   player = null;

		}
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		listener.onCompletion();
		initPlayer();
	}

	
	/**
	 * The returned binder will expose only the required methods
	 * for the activity alone(!). By that it will protect the service
	 * methods to be abused by the activity.
	 */
	public final class LocalBinder extends Binder implements PlayerInterface
	{
		@Override
		public void startPlay(PlayerCompletionListener listener, Handler handler) {
			ServicePlayer.this.handler = handler;
			ServicePlayer.this.listener = listener;
			_startPlay();
		}

		@Override
		public void stopPlay() {
			_stopPlay();
			
		}
	}
	
}





























		