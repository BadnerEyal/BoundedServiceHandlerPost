package sciatis.player.bounded2;

import android.os.Handler;


/**
 * This interface is used to expose from within the service
 * only the required methods for the activity
 * @author Gabriel@proto-mech.com
 *
 */
//חוזה מה יכול לראות מי שמקבל את השירות נלשח גם הנדלר בנוסף למאזין
public interface PlayerInterface 
{
	public void startPlay(PlayerCompletionListener listener, Handler handler);
	public void stopPlay();
	
}
