/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.model.gametime;

import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.logging.Level;
import l1j.server.server.*;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.command.executor.L1Shutdown;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.collections.Lists;

public class L1GameTimeClock {
	private static Logger _log = Logger.getLogger(L1GameTimeClock.class.getName());

	private static L1GameTimeClock _instance;

	private volatile L1GameTime _currentTime = L1GameTime.fromSystemCurrentTime();
    private boolean isShuttingDown = false;
	private L1GameTime _previousTime = null;

	private List<L1GameTimeListener> _listeners = Lists.newConcurrentList();

    public static final String DATE_FORMAT_NOW = "HH:mm:ss";

	private class TimeUpdater implements Runnable {
		@Override
		public void run() {
			while (true) {
				_previousTime = _currentTime;
				_currentTime = L1GameTime.fromSystemCurrentTime();
				notifyChanged();
                checkReboot();
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
	}

	private boolean isFieldChanged(int field) {
		return _previousTime.get(field) != _currentTime.get(field);
	}

    private void checkReboot() {
       /* Calendar cal = Calendar.getInstance(); //Create Calendar-Object
        cal.setTime(new Date());               //Set the Calendar to now
        int hour = cal.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar
        int minute = cal.get(Calendar.MINUTE);
        if(hour == 12 && (minute >= 17 && minute <= 20))              // Check if hour is between 8 am and 11pm
        {
            if(!isShuttingDown)
            {
            L1PcInstance autoGM = new L1PcInstance();
            autoGM.load("[GM]Legends");
            Shutdown.getInstance().startShutdown(autoGM,5,true);
            isShuttingDown = true;
            }
        }
        */
    }
	private void notifyChanged() {
		if (isFieldChanged(Calendar.MONTH)) {
			for (L1GameTimeListener listener : _listeners) {
				listener.onMonthChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.DAY_OF_MONTH)) {
			for (L1GameTimeListener listener : _listeners) {
				listener.onDayChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.HOUR_OF_DAY)) {
			for (L1GameTimeListener listener : _listeners) {
				listener.onHourChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.MINUTE)) {
			for (L1GameTimeListener listener : _listeners) {
				listener.onMinuteChanged(_currentTime);
			}
		}
	}

	private L1GameTimeClock() {
		GeneralThreadPool.getInstance().execute(new TimeUpdater());
	}

	public static void init() {
		_instance = new L1GameTimeClock();
	}

	public static L1GameTimeClock getInstance() {
		return _instance;
	}

	public L1GameTime currentTime() {
		return _currentTime;
	}

	public void addListener(L1GameTimeListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(L1GameTimeListener listener) {
		_listeners.remove(listener);
	}
}
