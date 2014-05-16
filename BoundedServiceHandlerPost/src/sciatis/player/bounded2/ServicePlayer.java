package sciatis.player.bounded2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

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
//בדוגמא זו אנו נקרא מתיקת שירים אשר בספריה 
public class ServicePlayer extends Service implements OnCompletionListener
{
	private Handler handler = null;
	private PlayerCompletionListener listener = null;
	private MediaPlayer player = null;
	
	//רשימת השירים בספריה
	List<File> songs = new ArrayList<File>();
	
	private int currntSongIndex = 0;
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
		
		//לקרוא את שמות השירים מהספריה 
		String dirPath = Environment.getExternalStorageDirectory()
				+"/MyMusic/";
		
		File dirFile = new File(dirPath);
		if(dirFile.exists() && dirFile.isDirectory() && dirFile.canRead())
		{
			File [] listFiles = dirFile.listFiles();
			songs = Arrays.asList(listFiles);
		}
	}

	//אתחול הנגן
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
			
		//player = MediaPlayer.create(this, R.raw.rihana_disturbia);
		
		
		player = new MediaPlayer();
		try 
		{
			if(currntSongIndex == songs.size())
		{
				currntSongIndex = 0;
			}
			player.setDataSource(songs.get(currntSongIndex).getAbsolutePath());
			player.prepare();   
			player.setOnCompletionListener(this);
			currntSongIndex ++;
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
		// על ידי הצינור ישלח לנו טריגר שגמר
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
		    
				//זה בעצם השימוש בצינור דרכו 
				//נפעיל קוד להרצה
				//במקרה זה אנו בעצם נפעיל את טריגר המאזין
				//post על ידי 
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						listener.onCompletion();
						
					}
				});
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
	
	// זה הבינדר שיקבל מי שיבקש שירות הוא כולל גם את הצינור
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





























		