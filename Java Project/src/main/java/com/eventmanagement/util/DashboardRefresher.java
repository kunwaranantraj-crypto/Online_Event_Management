package com.eventmanagement.util;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * Utility class for refreshing dashboard data at regular intervals
 */
public class DashboardRefresher {
    
    private Timeline timeline;
    private Runnable refreshAction;
    private Runnable onRefreshStart;
    private Runnable onRefreshComplete;
    
    public DashboardRefresher(Runnable refreshAction) {
        this.refreshAction = refreshAction;
    }
    
    public DashboardRefresher(Runnable refreshAction, Runnable onRefreshStart, Runnable onRefreshComplete) {
        this.refreshAction = refreshAction;
        this.onRefreshStart = onRefreshStart;
        this.onRefreshComplete = onRefreshComplete;
    }
    
    /**
     * Start auto-refresh with specified interval in seconds
     */
    public void startAutoRefresh(int intervalSeconds) {
        if (timeline != null) {
            timeline.stop();
        }
        
        timeline = new Timeline(new KeyFrame(
            Duration.seconds(intervalSeconds),
            e -> {
                if (onRefreshStart != null) onRefreshStart.run();
                refreshAction.run();
                if (onRefreshComplete != null) onRefreshComplete.run();
            }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Stop auto-refresh
     */
    public void stopAutoRefresh() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    /**
     * Refresh immediately
     */
    public void refreshNow() {
        refreshAction.run();
    }
}