package kr.keumyoung.mukin.data;

import java.io.Serializable;

/**
 *  on 27/04/18.
 * Project: KyGroup
 */

public class PlayerLog implements Serializable {
    public enum LogType {
        TEMPO, PITCH
    }

    private LogType logType;
    private int value;
    private long tick;
    private boolean isApplied = false;

    public PlayerLog(LogType logType, int value) {
        this.logType = logType;
        this.value = value;
    }

    public void setTick(long tick) {
        this.tick = tick;
    }

    public LogType getLogType() {
        return logType;
    }

    public int getValue() {
        return value;
    }

    public long getTick() {
        return tick;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean applied) {
        isApplied = applied;
    }
}
