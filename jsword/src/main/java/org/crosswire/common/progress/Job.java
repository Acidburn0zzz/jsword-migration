/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.progress;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;

/**
 * A Generic method of keeping track of Threads and monitoring their progress.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class Job implements Progress {
    /**
     * Create a new Job. This will automatically fire a workProgressed event to
     * all WorkListeners, with the work property of this job set to 0.
     * 
     * @param description
     *            Short description of this job
     * @param worker
     *            Optional thread to use in request to stop worker
     */
    protected Job(String jobName, Thread worker) {
        this.jobName = jobName;
        this.workerThread = worker;
        this.listeners = new ArrayList();
        this.cancelable = workerThread != null;
        this.jobMode = ProgressMode.PREDICTIVE;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String)
     */
    public void beginJob(String sectionName) {
        beginJob(sectionName, 100);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String, long)
     */
    public void beginJob(String sectionName, long totalWork) {
        if (this.finished) {
            return;
        }

        synchronized (this) {
            finished = false;
            currentSectionName = sectionName;
            totalUnits = totalWork;
            jobMode = totalUnits == 100 ? ProgressMode.PERCENT : ProgressMode.UNITS;
        }

        // Report that the Job has started.
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String, java.net.URI)
     */
    public void beginJob(String sectionName, URI predictURI) {
        if (finished) {
            return;
        }

        synchronized (this) {
            finished = false;
            currentSectionName = sectionName;
            predictionMapURI = predictURI;
            jobMode = ProgressMode.PREDICTIVE;
            startTime = System.currentTimeMillis();

            fakingTimer = new Timer();
            fakingTimer.schedule(new PredictTask(), 0, REPORTING_INTERVAL);

            // Load currentPredictionMap. It's not a disaster if it doesn't load
            totalUnits = loadPredictions();

            // There were no prior predictions so punt.
            if (totalUnits == Progress.UNKNOWN) {
                // if we have nothing to go on use our assumption
                totalUnits = EXTRA_TIME;
                jobMode = ProgressMode.UNKNOWN;
            }

            // And the predictions for next time
            nextPredictionMap = new HashMap();
        }

        // Report that the Job has started.
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getJobName()
     */
    public synchronized String getJobName() {
        return jobName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getProgressMode()
     */
    public synchronized ProgressMode getProgressMode() {
        return jobMode;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getTotalWork()
     */
    public synchronized long getTotalWork() {
        return totalUnits;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setTotalWork(long)
     */
    public synchronized void setTotalWork(long totalWork) {
        this.totalUnits = totalWork;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getWork()
     */
    public synchronized int getWork() {
        return percent;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setWork(long)
     */
    public synchronized void setWork(long work) {
        setWorkDone(work);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getWorkDone()
     */
    public synchronized long getWorkDone() {
        return workUnits;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setWork(long)
     */
    public void setWorkDone(long work) {
        if (finished) {
            return;
        }

        synchronized (this) {
            if (workUnits == work) {
                return;
            }

            workUnits = work;
            percent = (int) (100 * workUnits / totalUnits);
        }

        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#incrementWorkDone(long)
     */
    public void incrementWorkDone(long step) {
        setWorkDone(getWorkDone() + step);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getSectionName()
     */
    public synchronized String getSectionName() {
        return currentSectionName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setSectionName(java.lang.String)
     */
    public void setSectionName(String sectionName) {
        if (finished) {
            return;
        }

        synchronized (this) {
            // If we are in some kind of predictive mode, then measure progress toward the expected end.
            if (jobMode == ProgressMode.PREDICTIVE || jobMode == ProgressMode.UNKNOWN) {
                updateProgress(System.currentTimeMillis());

                // We are done with the current section and are starting another
                // So record the length of the last section
                if (nextPredictionMap != null) {
                    nextPredictionMap.put(currentSectionName, Long.valueOf(workUnits));
                }
            }

            currentSectionName = sectionName;
        }

        // Tell listeners that the label changed.
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#done()
     */
    public void done() {
        synchronized (this) {
            finished = true;
            // TRANSLATOR: This shows up in a progress bar when progress is finished.
            currentSectionName = UserMsg.gettext("Done");

            // Turn off the timer
            if (fakingTimer != null) {
                fakingTimer.cancel();
                fakingTimer = null;
            }

            workUnits = totalUnits;
            percent = 100;

            if (nextPredictionMap != null) {
                nextPredictionMap.put(currentSectionName, Long.valueOf(System.currentTimeMillis() - startTime));
            }
        }

        JobManager.fireWorkProgressed(this);

        synchronized (this) {
            if (predictionMapURI != null) {
                savePredictions();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#cancel()
     */
    public void cancel() {
        if (!finished) {
            ignoreTimings();
            done();
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isFinished()
     */
    public boolean isFinished() {
        return finished;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isCancelable()
     */
    public synchronized boolean isCancelable() {
        return cancelable;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setCancelable(boolean)
     */
    public synchronized void setCancelable(boolean newInterruptable) {
        if (workerThread == null || finished) {
            return;
        }
        cancelable = newInterruptable;
        fireStateChanged();
    }

    /**
     * Add a listener to the list
     */
    public synchronized void addWorkListener(WorkListener li) {
        List temp = new ArrayList();
        temp.addAll(listeners);

        if (!temp.contains(li)) {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remote a listener from the list
     */
    public synchronized void removeWorkListener(WorkListener li) {
        if (listeners.contains(li)) {
            List temp = new ArrayList();
            temp.addAll(listeners);
            temp.remove(li);
            listeners = temp;
        }
    }

    protected void fireStateChanged() {
        final WorkEvent ev = new WorkEvent(this);

        // we need to keep the synchronized section very small to avoid deadlock
        // certainly keep the event dispatch clear of the synchronized block or
        // there will be a deadlock
        final List temp = new ArrayList();
        synchronized (this) {
            if (listeners != null) {
                temp.addAll(listeners);
            }
        }

        // We ought only to tell listeners about jobs that are in our
        // list of jobs so we need to fire before delete.
        long count = temp.size();
        for (int i = 0; i < count; i++) {
            ((WorkListener) temp.get(i)).workStateChanged(ev);
        }
    }

    /**
     * Predict a percentage complete
     */
    private synchronized long getAgeFromMap(Map props, String message) {
        if (props == null) {
            return 0;
        }

        Long time = (Long) props.get(message);
        if (time != null) {
            return time.longValue();
        }

        return 0;
    }

    /**
     * Get estimated the percent progress, extrapolating between sections
     */
    protected synchronized void updateProgress(long now) {
        workUnits = now - startTime;

        // Are we taking more time than expected?
        // Then we are at 100%
        if (workUnits > totalUnits) {
            workUnits = totalUnits;
            percent = 100;
        } else {
            percent = (int) (100 * workUnits / totalUnits);
        }
    }

    /**
     * Load the predictive timings if any
     */
    private synchronized long loadPredictions() {
        long maxAge = UNKNOWN;
        try {
            currentPredictionMap = new HashMap();
            Properties temp = NetUtil.loadProperties(predictionMapURI);

            // Determine the predicted time from the current prediction map
            Iterator iter = temp.keySet().iterator();
            while (iter.hasNext()) {
                String title = (String) iter.next();
                String timestr = temp.getProperty(title);

                try {
                    Long time = Long.valueOf(timestr);
                    currentPredictionMap.put(title, time);

                    // if this time is later than the latest
                    long age = time.longValue();
                    if (maxAge < age) {
                        maxAge = age;
                    }
                } catch (NumberFormatException ex) {
                    log.error("Time format error", ex);
                }
            }
        } catch (IOException ex) {
            log.debug("Failed to load prediction times - guessing");
        }

        return maxAge;
    }

    /**
     * Save the known timings to a properties file.
     */
    private synchronized void savePredictions() {
        // Now we know the start and the end we can convert all times to
        // percents
        Properties predictions = new Properties();
        Iterator iter = nextPredictionMap.keySet().iterator();
        while (iter.hasNext()) {
            String sectionName = (String) iter.next();
            long age = getAgeFromMap(nextPredictionMap, sectionName);
            predictions.setProperty(sectionName, Long.toString(age));
        }

        // And save. It's not a disaster if this goes wrong
        try {
            NetUtil.storeProperties(predictions, predictionMapURI, "Predicted Startup Times");
        } catch (IOException ex) {
            log.error("Failed to save prediction times", ex);
        }
    }

    /**
     * Typically called from in a catch block, this ensures that we don't save
     * the timing file because we have a messed up run.
     */
    private synchronized void ignoreTimings() {
        predictionMapURI = null;
    }

    private static final int REPORTING_INTERVAL = 100;

    /**
     * The amount of extra time if the predicted time was off and more time is needed.
     */
    private static final int EXTRA_TIME = 2 * REPORTING_INTERVAL;

    /**
     * The type of job being performed. This is used to simplify code.
     */
    private ProgressMode jobMode;

    /**
     * Total amount of work to do.
     */
    private long totalUnits;

    /**
     * Does this job allow interruptions?
     */
    private boolean cancelable;

    /**
     * Have we just finished?
     */
    private boolean finished;

    /**
     * The amount of work done against the total.
     */
    private long workUnits;

    /**
     * The officially reported progress
     */
    private int percent;

    /**
     * A short descriptive phrase
     */
    private String jobName;

    /**
     * Optional thread to monitor progress
     */
    private Thread workerThread;

    /**
     * Description of what we are doing
     */
    private String currentSectionName;

    /**
     * The URI to which we load and save timings
     */
    private URI predictionMapURI;

    /**
     * The timings loaded from where they were saved after the last run
     */
    private Map currentPredictionMap;

    /**
     * The timings as measured this time
     */
    private Map nextPredictionMap;

    /**
     * When did this job start? Measured in milliseconds since beginning of epoch.
     */
    private long startTime;

    /**
     * The timer that lets us post fake progress events.
     */
    private Timer fakingTimer;

    /**
     * People that want to know about "cancelable" changes
     */
    private List listeners;

    /**
     * So we can fake progress for Jobs that don't tell us how they are doing
     */
    final class PredictTask extends TimerTask {
        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            updateProgress(System.currentTimeMillis());
            JobManager.fireWorkProgressed(Job.this);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256721784160924983L;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Job.class);
}
